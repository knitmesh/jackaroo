
greenlet支持微线程（tasklet），tasklet伪并发运行，同步在信道上交换数据。greenlet——coroutines（协程）——micro-thread（微线程）（三者意思相近）在greenlet上可自定义微线程调度顺序，灵活掌控控制流。

#### 1. greenlet简介
greenlet之间可相互切换，当一个greenlet1切换至greenlet2时，greenlet1挂起，当greenlet2运行一段时候切换回时，greenlet2挂起，greenlet1恢复运行。
每个greenlet创建时拥有一个空的栈，当切换至该greenlet时，它会运行一个特殊函数（该函数也许会调用其它函数），当最终最外层函数执行完成后，greenlet栈再次为空，greenlet死亡。greenlet也可被不可捕捉的异常杀死。


例子:

```python
from greenlet import greenlet

def test1():
    print(12)
    gr2.switch()
    print(34)

def test2():
    print(56)
    gr1.switch()
    print(78)

gr1 = greenlet(test1)
gr2 = greenlet(test2)
gr1.switch()
```
最后一行跳到test1，打印12，跳转到test2，打印56，跳回test1，打印34; 然后test1完成并且gr1死亡。此时，调用执行回到原来的gr1.switch() 。请注意，78不会打印。


#### 2. Parents
每一个greenlet有一个父greenlet，相应父greenlet在greenlet被创建时初始化。子greenlet死亡后，父greenlet继续执行。

greenlet树形组织，隐含的main greenlet为此树根节点。任何一个greenlet死亡，执行顺序将被回溯至main greenlet。异常发生将传播至parent greenlet。
switch不是调用，只是在并行的'stack containers'中传输执行。

#### 3. 实例化
greenlet类型greenlet.greenlet，有如下一些方法：

greenlet(run=None, parent=None)
创建一个新的greenlet对象但并不运行，run为可调用请求，parent为父greenlet，默认为当前greenlet。


greenlet.getcurrent()
返回当前greenlet。
greenlet.GreenletExit
此特殊异常不会传播至父greenlet，它被用于杀死一个单独的greenlet。

#### 4. 切换
greenlet切换发生在switch()）函数被调用或一个greenlet死亡时。调用switch()函数的greenlet为切换至的目标greenlet；

greenlet死亡时切换至parent greenlet。切换时，一个对象或异常发送至目标greenlet，这即是greenlets间方便的通信方式。例如：

```python
def test1(x, y):
    z = gr2.switch(x+y)
    print z

def test2(u):
    print u
    gr1.switch(42)

gr1 = greenlet(test1)
gr2 = greenlet(test2)
gr1.switch("hello", " world")
```
此段程序输出“hello world”和42.需注意test1()和test2()的参数并不是在greenlet创建时给出，而只能是在第一次切换到相应greenlet时给出。




g.switch(*args, **kwargs)
切换至greenlet g执行。

需要注意的是x = g.switch(y),会将参数y发送给g，然而稍后却有可能将毫无关联的对象经毫无关联的greenlet处理后返回给x。这可以理解为g.switch(y)的值没有立即返回，而其它greenlet的结果却先返回了！！！


switch至一个已经死亡的greenlet，最终将会switch至其parent greenlet或parent' parent greenlet，如此回溯。（最终的parent greenlet是main greenlet，永不死亡。）

#### 5. greenlets方法和属性


g.switch(*args, **kwargs)
切换至greenlet g执行。


g.run
执行后运行greenlet g，g运行后该属性不再存在。


g.parent
g的parent greenlet。该变量可写，但禁止构造循环型父子关系。


g.gr_frame
目前上层框架，或为None。


g.dead
当g死亡时值为True。


bool(g)
g处于活动状态时值为True，若死亡或还未开始值为False。


g.throw([typ, [val, [tb]]])
切换执行序列至greenlet g，在g中立即抛出给定异常。若未提供参数，异常默认为greenlet.GreenletExit。

#### 6. Greenlet和python线程
Greenlet能够和Python线程结合，每个python线程中包含一个独立的main greenlet及由其子greenlet构成的树。但不属于同一个线程的不同greenlet之间不能结合或切换。