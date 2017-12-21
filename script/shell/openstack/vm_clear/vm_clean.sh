#!/bin/bash

# /var/spool/cron/root
# rbd ls -p vms | grep recycle | xargs -I{}  rbd rm vms/{}

if [ x$1 != x ]
then
    echo "#Clean recycle disk" >> /var/spool/cron/root
    echo "0 0 * * * rbd ls -p $1 | grep recycle | xargs -I{}  rbd rm $1/{} >/dev/null 2>&1" >> /var/spool/cron/root
else
    echo "Please add instance pool name."
    echo "Such as:"
    echo "  sudo bash ./$0 vms"
fi
