
在你经常使用的命令当中有一个git branch –a 用来查看所有的分支，包括本地和远程的。但是时间长了你会发现有些分支在远程其实早就被删除了，但是在你本地依然可以看见这些被删除的分支。

你可以通过命令，`git remote show origin `来查看有关于origin的一些信息，包括分支是否tracking。

    git remote show origin 

有的时候 远程仓库的分支已经被删除了  但是 `git branch -a`  还是能够看到 ,  亦或是本地依然存在这个分支,  可以使用下面的命令同步
    
    git remote prune origin

也就是说你可以刷新本地仓库与远程仓库的保持这些改动的同步


批量删除本地分支
    
    git branch -a | grep -v -E 'master|develop' | xargs git branch -D

批量删除远程分支
    
    git branch -r| grep -v -E 'master|develop' | sed 's/origin\///g' | xargs -I {} git push origin :{}

如果有些分支无法删除，是因为远程分支的缓存问题，可以使用`git remote prune`

批量删除本地tag
    
    git tag | xargs -I {} git tag -d {}

批量删除远程tag
    
    git tag | xargs -I {} git push origin :refs/tags/{}

用到的命令说明
    
    grep -v -E 排除master 和 develop

    -v 排除
    -E 使用正则表达式
    
    xargs 将前面的值作为参数传入 git branch -D 后面
    
    -I {} 使用占位符 来构造 后面的命令