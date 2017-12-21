#!/bin/bash
# set -x
usage()
{
cat << EOF
usage: $0 options

Script to configure bcache devices on a machine.

OPTIONS:
   -h      Show this message
   -B      Comma separated values of backing devices, ex:/dev/xvdf,/dev/xvdg
   -C      Comma separated values of caching devices, ex:/dev/xvdb,/dev/xvdc
           This order should match the backing device.
           In the example above, xvdf will be backing device and
           xvdb will be caching device for the same bcache store.
   -w 	   Wipe the backing device. The only accepted value is "iknowwhatiamdoing".
           When present, this will wipe all the backing devices.
           You will lose all data present on those devices.
   -v      Verbose
EOF
}

echo "[INFO] Begin run - `date`"

BACKING_DEVICES_U=
CACHING_DEVICES_U=
WIPEFS_BACKING_U=
VERBOSE=
while getopts "hB:C:w:v" OPTION
do
     case $OPTION in
         h)
             usage
             exit 1
             ;;
         B)
             BACKING_DEVICES_U=$OPTARG
             ;;
         C)
             CACHING_DEVICES_U=$OPTARG
             ;;
         w)
             WIPEFS_BACKING_U=$OPTARG
             ;;
         v)
             VERBOSE=1
             ;;
         ?)
             usage
             exit
             ;;
     esac
done

if [[ -z $BACKING_DEVICES_U ]] || [[ -z $CACHING_DEVICES_U ]] 
then
     usage
     exit 1
fi
bcache_backing=($(echo ${BACKING_DEVICES_U[@]} | tr ',' '\n'))
bcache_caching=($(echo ${CACHING_DEVICES_U[@]} | tr ',' '\n'))

if [ ${#bcache_backing[@]} -ne ${#bcache_caching[@]} ]
then
	echo "ERROR: Number of backing and caching devices do not match, exiting"
	exit 1
fi
if [[ ${WIPEFS_BACKING_U} == 'iknowwhatiamdoing' ]]
then
	wipefs_backing='y'
else
	wipefs_backing='n'
fi


initialize_backing='y'
bcache_count=${#bcache_backing[@]}
err_count=0
sleep_time=3

run_cmd(){
	echo "[INFO] Running $@"
	sleep 1
	if [[ -n $VERBOSE ]]; then
		redirect=' 2> /dev/null'
	else
		redirect=' &> /dev/null'
	fi
	if [[ $@ == *"make-bcache"* ]]; then
		redirect=' '
	fi

	eval $@ $redirect
	retval=$?
	debug "return value of last execution is $retval"
	if [ $retval -ne 0 ]; then
		warn "Possible failure in last execution, might need to check again"
	fi
	echo ' '
	sleep 1
	sudo sync
	return $retval
}

stop_b_running(){
	device=`echo $1|rev|cut -f 1 -d '/'|rev`
	if echo "${device}" | grep '[0-9]' >/dev/null; then
	    device_p=`echo $device|rev |cut -b 2-|rev`
	    device="${device_p}/${device}"
	fi
	run_cmd "echo 1|sudo tee /sys/block/${device}/bcache/running"
}

debug(){
	if [[ -n $VERBOSE ]]; then
		echo "[DEBUG] $@"
	fi
}

info(){
	echo -e "[INFO] $@"
}

info_ok(){
	echo -e "[OK] $@"
}

error(){
	echo -e "[ERROR] $@"	
}

warn(){
	echo -e "[WARN] $@"
}

# Make sure the devices mentioned are not symbolic links
for i in ${bcache_backing[@]} ${bcache_caching[@]}
do
    if [ -L $i ]; then
        echo "The device file is a symbolic link: $i. Please use the actual device file."
        exit 1
    fi
done

info "Stopping all existing bcaches"
run_cmd "echo 1|sudo tee /sys/fs/bcache/*-*-*-*-*/stop"
run_cmd "echo 1|sudo tee /sys/block/bcache*/bcache/stop"
for i in ${bcache_backing[@]}
do
	# read -p "Wipefs on backing device $i ? This will remove all existing data on the disk:(n/y) "
	#-e -i $wipefs_backing wipefs_backing_c
	wipefs_backing_c=$wipefs_backing
	if [[ ${wipefs_backing_c} == 'y' ]]; then
		run_cmd "sudo wipefs $i"
		run_cmd "sudo wipefs -a $i"
	fi
	# read -p "Initialize bcache on backing device $i ?" -e -i
	#$initialize_backing initialize_backing_c
	initialize_backing_c=$initialize_backing
	if [[ ${initialize_backing_c} == 'y' ]]; then
		run_cmd "sudo make-bcache -B $i"
		info "Register backing device $i"
		run_cmd "echo $i|sudo tee /sys/fs/bcache/register"
	fi
        ##Need to cross check the initial states of this
	info "Stopping bcache in case its still running"
	stop_b_running $i
done

info "Checking bcache devices"
c=0
err_count_local=0
while [ $c -lt $bcache_count ]
do
	bcache_device=/sys/block/bcache${c}
	info "checking ${bcache_device}"
	ls "${bcache_device}" &>/dev/null
	retval=$?
	if [ $retval -ne 0 ]; then
		error "No device found: ${bcache_device}"
		info "Trying to recover from previous error"
		stop_b_running ${bcache_backing[$c]}
		if [ $err_count_local -le 5 ]; then
			let c=c-1
		else
			err_count_local=0
		fi

		let err_count_local=err_count_local+1
		let err_count=err_count+1
	else
		info_ok "device $bcache_device exists"
	fi
	let c=c+1
done

for i in ${bcache_caching[@]}
do
	info "Wiping and initializing caching device $i \n"
	mount|grep $i|awk '{print $3}'|xargs -I f sudo umount f &>/dev/null
	run_cmd "sudo wipefs $i"
	run_cmd "sudo wipefs -a $i"
	run_cmd "sudo make-bcache -C $i"
	run_cmd "echo $i|sudo tee /sys/fs/bcache/register"
	c_device=`echo $i|cut -f 3 -d '/'`
	info "Checking if $c_device is attached to bcache"
	c_attached=0
	c_attached_err=0
	while [ $c_attached -ne 1 ] && [ $c_attached_err -le 5 ]
	do
		ls -l /sys/fs/bcache/*-*-*-*-*/cache0|grep $c_device &>/dev/null
		retval=$?
		if [ $retval -ne 0 ]; then
			error "Caching device not attached"
			info "Trying to recover from last error"
			echo $i|sudo tee /sys/fs/bcache/register
			let c_attached_err=c_attached_err+1
		else
			info_ok "$i is attached to bcache"
			c_attached=1
		fi
	done
done

#Now find and attach the write /sys/fs/bcache/UUID device to the write backing device
info "Checking and attaching the caching device to the backing device"
c=0
for x in ${bcache_caching[@]}
do
	debug "Generating caching_device[$c] with $x"
	caching_device[$c]=`echo $x|rev|cut -f 1 -d '/'|rev`
	let c=c+1
done
c=0
for x in ${bcache_backing[@]}
do
	backing_device[$c]=`echo $x|rev|cut -f 1 -d '/'|rev`
	let c=c+1
done

for device in `ls -d /sys/fs/bcache/*-*-*-*-*`
do
	c=0
	while [ $c -lt $bcache_count ]
	do
		caching_device_n=`readlink ${device}/cache0`
		debug "Trying to match $caching_device_n with ${caching_device[$c]} \n"
		if [[ $caching_device_n == *${caching_device[$c]}* ]]; then 
			debug "$c has $caching_device_n ${bcache_caching[$c]} \n"
			info "Checking corresponding bcache and backing device"
			backing_device_n=`readlink /sys/block/bcache${c}/bcache`
			debug "comparing $backing_device_n AND ${backing_device[$c]}"
			if [[ $backing_device_n == *${backing_device[$c]}* ]]; then
				info_ok "${bcache_caching[$c]} has UUID `basename $device` to be attached to ${bcache_backing[$c]}"
				echo `basename $device`|sudo tee /sys/block/bcache${c}/bcache/attach 
				let c=$bcache_count #Done with this device successfully
			else
				error "Error in matching backing with caching"
				let err_count=err_count+1
				
			fi
		else
			warn " No match found"
		fi
		let c=c+1
	done
done

info "Setting bcache congested to 0"
run_cmd "echo 0|sudo tee /sys/fs/bcache/*-*-*-*/congested_*"
info "Setting bcache sequential cutoff to 0"
run_cmd "echo 0|sudo tee /sys/block/bcache*/bcache/sequential_cutoff"
info "Checking all the states"
do_ls[0]='/sys/fs/bcache/*-*-*-*/congested*'
do_ls[1]='/sys/block/bcache*/bcache/sequential_cutoff'
do_ls[2]='/sys/block/bcache*/bcache/state'

for f in ${do_ls[@]}
do
	for param in `ls $f`
	do 
		run_cmd "cat $param"
	done
done
oven=0
for param in `ls ${do_ls[2]}`
do 
	state=`cat $param`
	if [[ "$state" != "clean" ]]; then
		error "$param state is $state (not clean), please run this script again"
		let oven=oven+1
	else
		info "$param is $state. Things look good."
	fi
done
if [ $oven -eq 0 ]; then
	info "The oven is primed!"
else
	warn "There have been some additional errors. Please run the script again"
fi
