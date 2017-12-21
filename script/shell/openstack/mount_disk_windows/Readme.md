## Windows 自动挂载云硬盘脚本

### 文件
* auto_mount_disk.ps1 -- 自动挂载云硬盘的powershell脚本
* auto_mount_disk.bat -- 启动auto_mount_disk.ps1的脚本

### 使用
* 方法一：将auto_mount_disk.ps1放入策略组里面设置为开机启动
* 方法二：将两个文件放在同一目录下，将auto_mount_disk.bat放入策略组里面设置为开机启动

### 效果
在windows开机的时候自动检测离线的云硬盘并自动将云硬盘设置为在线状态
