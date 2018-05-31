一 、设置全局的user和email


设置Git的user name和email：

    git config --global user.name "lijinghui"
    git config --global user.email "lijinghui@t2cloud.net"

二、生成并添加SSH密钥过程：

1. 查看是否已经有了ssh密钥：cd ~/.ssh

    如果没有密钥则不会有此文件夹，有则备份删除

2. 生成密钥：

    ssh-keygen -t rsa -C "lijinghui@t2cloud.net"
按3个回车，密码为空。
```
Your identification has been saved in /home/tekkub/.ssh/id_rsa.
Your public key has been saved in /home/tekkub/.ssh/id_rsa.pub.
The key fingerprint is:
………………
```
最后得到了两个文件：id_rsa和id_rsa.pub

3. 密钥添加到SSH agent中

因为默认只读取id_rsa，为了让SSH识别新的私钥，需将其添加到SSH agent中：
    
    ssh-add ~/.ssh/id_rsa
如果出现Could not open a connection to your authentication agent的错误，就试着用以下命令：
    
    ssh-agent bash
    ssh-add ~/.ssh/id_rsa

4. 在github上添加ssh密钥，这要添加的是“id_rsa.pub”里面的公钥。

    打开http://192.168.89.100:10080/，登陆自己的账户，然后添加ssh。

三、设置ssh key后push为什么还要输入用户名和密码

因为使用的是https而不是ssh。 可以更新一下origin

查看协议:

    git remote -v

    git remote remove origin
    git remote add origin git@github.com:Username/Your_Repo_Name.git

之后还需要重新设置track branch，比如：
    
    git branch --set-upstream-to=origin/master master

对于HTTPS方式，可以在~/.netrc文件里设定用户名密码，不过这样的风险在于密码是明文存放在这个文件里的，比较容易泄露

    machine github.com
    login Username
    password Password



