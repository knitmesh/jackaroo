#!/bin/bash
### install
# cache_devs='sdb1 sdb2 sdb3'
# backing_devs='sdc sdd sde'

cache_devs='sdb1 sdb2'
backing_devs='sdc sdd'

for dev in ${cache_devs} ${backing_devs}
do
    if [ ! -e "/dev/${dev}" ];then
        echo "Err: ${dev} not exist"
        exit 1
    fi
done

cache_devs=(${cache_devs// / })
backing_devs=(${backing_devs// / })
cache_dev_num=${#cache_devs[*]}
backing_dev_num=${#backing_devs[*]}
if [ "$cache_dev_num" -ne "$backing_dev_num" ];then
    echo "cache disk number not equal to backing disk number..."
    exit 1
fi

for ((i=0;i<$backing_dev_num;i++));do
    backing_dev=${backing_devs[$i]}
    cache_dev=${cache_devs[$i]}
    echo "Init bcache: $backing_dev -- $cache_dev"

    echo "wipe backing device $backing_dev ..."
    wipefs /dev/${backing_dev}
    wipefs -a /dev/${backing_dev}
    sleep 1

    # init every bcache dev
    echo "Init dev ${cache_dev} -- ${backing_dev}..."
    backing_dev_uuid=`make-bcache -B /dev/${backing_dev} --wipe-bcache | grep -i 'Set UUID:' | awk -F ':' '{print $2}'`
    backing_dev_uuid=`echo $backing_dev_uuid | sed -e 's/^ *//' -e 's/ *$//'`

    cache_dev_uuid=`make-bcache --bucket 512k -C /dev/${cache_dev} --wipe-bcache | grep -i 'Set UUID:' | awk -F ':' '{print $2}'`
    cache_dev_uuid=`echo $cache_dev_uuid | sed -e 's/^ *//' -e 's/ *$//'`

    # register
    echo "Register ${cache_dev} -- ${backing_dev} to bcache..."
    echo /dev/${cache_dev} > /sys/fs/bcache/register
    echo /dev/${backing_dev} > /sys/fs/bcache/register

    sleep 3

    # get the bcache dev name
    disk_uuid=`bcache-super-show /dev/${backing_dev} | grep 'dev.uuid' | awk '{print $2}'`
    bcache_dev=`ls -l /dev/bcache/by-uuid/${disk_uuid} | awk -F '->' '{print $2}' | xargs basename`

    # attach cache dev to backing dev
    echo ${cache_dev_uuid} > /sys/block/${bcache_dev}/bcache/attach

    # tunning
    echo writeback > /sys/block/${bcache_dev}/bcache/cache_mode
    echo 0 > /sys/block/${bcache_dev}/bcache/writeback_percent
    echo 10000 >/sys/block/${bcache_dev}/bcache/writeback_rate

    echo 0 > /sys/fs/bcache/${cache_dev_uuid}/congested_read_threshold_us
    echo 0 > /sys/fs/bcache/${cache_dev_uuid}/congested_write_threshold_us

    # open seq write cache
    # echo 0 > /sys/block/${bcache_dev}/bcache/sequential_cutoff

    # default: close seq write cache
    # echo 4M > /sys/block/${bcache_dev}/bcache/sequential_cutoff
done

### 卸载
for cache_uuid in `find /sys/fs/bcache/ -name '*-*-*-*-*'`
do
    echo 'close cache dev ${cache_uuid}'
    echo 1 >${cache_uuid}/unregister
done

for dev in `find /sys/block/ -name 'bcache*'`
do
    echo 'stop backing dev ${dev}'
    echo 1 >${dev}/bcache/stop
done


### 测试
fio -runtime=3000 -filename=/dev/bcache0 -direct=1 -thread -rw=randwrite -bs=4k -size=400G -iodepth=64 -rate_iops=3000 -ioengine=libaio -name=mytest
fio -runtime=3000 -filename=/dev/bcache1 -direct=1 -thread -rw=write -bs=512k -size=400G -iodepth=64 -rate_iops=3000 -ioengine=libaio -name=mytest
fio -runtime=3000 -directory=/mnt/test -direct=1 -thread -rw=write -bs=512k -size=100G -iodepth=64 -rate_iops=3000 -ioengine=libaio -name=mytest

### 监控
# 查看脏数据
cat /sys/block/bcache1/bcache/dirty_data

# cache stat
cat /sys/fs/bcache/${cache_dev_uuid}/stats_total/cache_hit_ratio
cat /sys/fs/bcache/${cache_dev_uuid}/stats_total/cache_misses
cat /sys/fs/bcache/${cache_dev_uuid}/stats_total/bypassed

# 触发垃圾回收
cat /sys/fs/bcache/${cache_dev_uuid}/internal/trigger_gc




################
./remove_bcache_dev
./init_ceph_env
modprobe bcache


./part_disk sdb 3
./part_backing_dev "sdc sdd sde" "20"
./init_bcache_dev "sdb1 sdb2 sdb3" "sdc2 sdd2 sde2"
./deploy_osd_for_multi_osd_on_sata_disk_with_bcache "bcache0 bcache1 bcache2" "sdc1 sdd1 sde1"
./deploy_osd_for_multi_osd_on_sata_disk_with_bcache "bcache1" "sdd1"

./part_disk sdb 6
./init_bcache_dev "sdb1 sdb2 sdb3" "sdc sdd sde"
./deploy_osd_for_multi_osd_on_sata_disk_with_bcache "bcache0 bcache1 bcache2" "sdb4 sdb5 sdb6"
./deploy_osd_for_multi_osd_on_sata_disk_with_bcache "bcache1" "sdb5"


iostat -xm 1 /dev/bcache* /dev/sd{b,c,d,e}

cat /sys/block/bcache0/bcache/dirty_data
cat /sys/block/bcache1/bcache/dirty_data
cat /sys/block/bcache2/bcache/dirty_data

echo 0 > /sys/block/bcache0/bcache/sequential_cutoff
echo 0 > /sys/block/bcache1/bcache/sequential_cutoff
echo 0 > /sys/block/bcache2/bcache/sequential_cutoff

echo 4M > /sys/block/bcache0/bcache/sequential_cutoff
echo 4M > /sys/block/bcache1/bcache/sequential_cutoff
echo 4M > /sys/block/bcache2/bcache/sequential_cutoff

