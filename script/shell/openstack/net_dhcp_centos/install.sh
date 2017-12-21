#!/bin/bash
#
# Authors:      wanghongxu <harald@redhat.com>
#


cp ./write_net_rules /lib/udev/write_net_rules
cp mv-net-cfg /etc/init.d/mv-net-cfg
chmod +x /lib/udev/write_net_rules
chmod +x /etc/init.d/mv-net-cfg
ln -s /etc/init.d/mv-net-cfg /etc/rc3.d/S09mv-net-cfg
