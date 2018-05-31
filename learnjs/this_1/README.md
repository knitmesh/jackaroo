## 考察this的一道小题

这道题来源于SegmentFault中一篇关于this的[文章](http://mp.weixin.qq.com/s/haFVIlx-CBNtDCpA3BYr9g)。
考察点很有意思，搬过来干他一炮   

```
function foo(arg) {
  this.a = arg;
  return this;
}

var a = foo(1);
var b = foo(10);

console.log(a.a);
console.log(b.a);
```   

如果你能说出正确答案，想必也会对其考察方式叹为观止吧，哈哈~   

### 走起来！

先说一下答案：
```
undefined
10
```
最大的疑惑肯定就是undefined的了

对于```a.a```，可能有的人会说是1，有的人会说是10   

细读一下这段代码   

首先，引擎在跑这段代码时会经历**变量声明的提升**，形如以下：
```
function foo(arg) {
  this.a = arg;
  return this;
}

//这里！
var a;
var b;

a = foo(1);
b = foo(10);
```  

在```a```与```b```的赋值语句执行时，两个变量已经被声明过了   

也就是说```foo(1)```与```foo(10)```在执行时引擎中这段作用域里```a```与```b```都已经被声明过！

再来看```foo```的定义

```
function foo(arg) {
  this.a = arg;
  return this;
}
```

这里就考察了面试者对this的理解   

简单说一下，```foo```中this指向的是**调用foo的对象**   

举个例子，下面这段代码中，```foo```中```this.a```分别操作了```objA```与```objB```中的```a```
```
function foo(arg) {
  this.a = arg;
}

var objA = {
  a: 1,
  bar: foo
}
objA.bar(3);
objA.a  //3

var objB = {
  a: 2
}

foo.call(objB, 3);
objB.a  //3 
```   

而像题目中```foo```的执行，其调用对象是**当前作用域**，也就是window（或者global，以下就统一只说window）

之前说过，由于变量声明的提升，```a```与```b```在```foo```执行时已经被声明   

由于```foo```的调用关系，**整段代码里```foo```中的```this.a```指的都是window中声明的这个```a```。。**

说的有些绕，我们来看看```foo()```执行的时候发生了什么来理一下  

第一个```a = foo(1)```将```a```赋值为1，然后返回```this```赋值给```a```

注意，**此时```a```的值为```window```**

```
a = foo(1);
//a先被赋值为1，接着被赋值为foo返回的this，即window
```


第二个```b = foo(10)```将```a```赋值为10，然后返回```this```将其赋值给```b```

此时```b```的值为```window```，```a```的值为10

```
b = foo(10);
//a被赋值为10，b被赋值为foo返回的this，即window
```

现在看看```a.a```与```b.a```

```a```在```foo(10)```中被赋值为常量```10```，所以```a.a```就是```undefined```   

```b```被赋值为window，```b.a```实际上就是```window.a```，即最开始声明的```a```，为```10```

很简单的题目，但结合了函数作用域及变量声明提升就变得有很意思了，哈哈~