

方案1:  
------------------------------
    由于使用memcached时，memcached不提供集群功能，因为集群的要素是负载均衡和单点恢复；  
    我的思路是用magent来实现负载均衡，利用repcached来实现单点恢复。  
    使用magent+repcache的方式，最大程度利用服务器来存储不同的数据，和使用相同的资源；  
    同时解决无法同步数据，容易造成单点故障等问题。

Repcached说明:

Repcached是一个单master单 slave的方案，但它的 master/slave都是可读写的，而且可以相互同步。如果 master down掉， slave侦测到连接断了，它会自动 listen而成为 master；而如果 slave坏掉， master也会侦测到连接断，它就会重新 listen等待新的 slave加入。 


