问题阐述

当有多个git账号的时候，比如一个github，用于自己进行一些开发活动，再来一个gitlab，一般是公司内部的git。这两者你的邮箱如果不同的话，就会涉及到一个问题，生成第二个git的key的时候会覆盖第一个的key，导致必然有一个用不了。

问题解决

我们可以在~/.ssh目录下新建一个config文件配置一下，就可以解决问题

具体步骤

生成第一个ssh key(这里我用于github，用的gmail邮箱)
    
    ssh-keygen -t rsa -C "yourmail@gmail.com"
这里不要一路回传，让你选择在哪里选择存放key的时候写个名字，比如 `id_rsa_github`，之后的两个可以回车。
完成之后我们可以看到~/.ssh目录下多了两个文件

`~/.ssh`目录下的文件

    -rw------- 1 jing jing 1.7K 10月 13  2017 id_rsa_github
    -rw-r--r-- 1 jing jing  398 10月 13  2017 id_rsa_github.pub


生成第二个ssh key（这里我用于gitlab，用的是公司邮箱）

    ssh-keygen -t rsa -C "jingh.lib@gmail.com"
    
还是一样不要一路回车，在第一个对话的时候继续写个名字，比如 `id_rsa_gitlab`,之后的两个可以回车。

完成之后我们可以看到~/.ssh目录，一样出现两个文件。（一个公钥一个私钥）

打开ssh-agent

这里如果你用的github官方的bash，`ssh-agent -s`,如果是其他的，比如`msysgit`, `eval $(ssh-agent -s)`

添加私钥

    ssh-add ~/.ssh/id_rsa_github
    ssh-add ~/.ssh/id_rsa_gitlab
    
创建并修改config文件

在windows下新建一个txt文本，然后将名字后缀一起改成config即可

在bash下的话直接touch config 即可。

添加以下内容

```
# gitlab
Host t2cloud
# HostName 192.168.89.100:10080 
HostName 172.16.99.4:10080 
PreferredAuthentications publickey
IdentityFile ~/.ssh/id_rsa
User lijinghui

# github
Host github.com
HostName github.com
PreferredAuthentications publickey
IdentityFile ~/.ssh/id_rsa_github
User knitmesh


 # my gitlab     
Host my_gitlib 
HostName 183.136.128.39:10080 
PreferredAuthentications publickey
IdentityFile ~/.ssh/id_rsa_my_gitlib
User jingh


```

在github和gitlab上添加公钥即可，这里不再多说。

测试


分别测试githab和gitlub

PS:如果到这里你没有成功的话，别急，教你解决问题的终极办法--debug

    github: ssh -vT git@github.com
    gitlib: ssh -vT git@git.iboxpay.com
    
-v 是输出编译信息，然后根据编译信息自己去解决问题吧。就我自己来说一般是config里的host那块写错了。


---

补充一下

如果之前有设置全局用户名和邮箱的话，需要unset一下

    git config --global --unset user.name
    git config --global --unset user.email
然后在不同的仓库下设置局部的用户名和邮箱

比如在公司的repository下

    git config user.name "yourname" 
    git config user.email "youremail" 
    
在自己的github的仓库在执行刚刚的命令一遍即可。

这样就可以在不同的仓库，已不同的账号登录。