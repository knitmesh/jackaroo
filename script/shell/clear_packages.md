* 清理 ci 执行后遗留的旧版本包

```bash

#!/bin/bash                                                                                                                                           
#--------------------------------------------
# 按照时间, 自动删除旧的包, 只保留时间是最新的包
AUTH_FOO=("portal_auth-100.1.0.dev.*-py2-none-any.whl" "portal_auth-100.2.0.dev.*-py2-none-any.whl")

PO_FOO=("portal_openstack-100.1.0.dev.*-py2-none-any.whl" "portal_openstack-100.2.0.dev.*-py2-none-any.whl" "portal_openstack-100.3.0.dev.*-py2-none-any.whl")

REST_FOO=("portal_rest-100.1.0.dev.*-py2-none-any.whl" "portal_rest-100.2.0.dev.*-py2-none-any.whl")

function auto_clear(){
        paks=$1
        cd /var/pypiserver/packages
        for name in ${paks[*]};
        do
                ls -lt |egrep -o $name |  sed -n '2,$p' | xargs rm -fv
        done
}

auto_clear "${AUTH_FOO[*]}"
auto_clear "${Po_FOO[*]}"
auto_clear "${REST_FOO[*]}"

```