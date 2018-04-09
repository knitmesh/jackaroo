#!/bin/bash
function usage() {
        echo "Usage: [-e <service_port>] [-n <service_name DEFAULT "t2cloud-portal">]"

	echo -e "\033[33mUSAGE: $0 [options] \033[0m"
	echo -e "\033[33mOptions: \033[0m"
	echo -e "\033[33m-e                  Service port. \033[0m"
	echo -e "\033[33m-n <service_name>   Service name, default 't2cloud-portal'. \033[0m"
        exit 1
}

function generate_digest() {


	SYSTEM_NUMBER=$(dmidecode -s system-serial-number)

	echo -n ${SYSTEM_NUMBER}|md5sum|cut -d ' ' -f1

}

while getopts ':e:n:h' OPT;
do
    case $OPT in
        e)
            SERVICE_PORT="$OPTARG"
	    ;;
        n)
            SERVICE_NAME="$OPTARG"
  	    ;;
	    :)
	    echo "The option -$OPTARG requires an argument."
	    exit 1
	    ;;
        h)
            usage
            exit
            ;;
        ?)
	    echo "Invalid option: -$OPTARG"
            exit 2;
    esac
done

if [[ "${SERVICE_PORT}aa" = "aa" ]]  ; then
	echo "error: argument -e is required";
	exit -1
fi


if [[ "${SERVICE_NAME}aa" = "aa" ]]  ; then
	SERVICE_NAME="t2cloud_portal"
fi




SYSTEM_DIGEST=$(generate_digest)

echo ${SERVICE_PORT}
echo ${SERVICE_NAME}
echo ${SYSTEM_DIGEST}


docker run --rm \
        -v /etc/portal:/etc/portal \
        -v /var/log/httpd:/var/log/httpd \
        -v /etc/localtime:/etc/localtime:ro \
		-e "SERVICE_PORT=${SERVICE_PORT}" \
		-e "DIGEST=${SYSTEM_DIGEST}" \
		--network host \
		--name ${SERVICE_NAME} t2cloud runportal
