## 今天来实现一个apply()方法

先看一下apply()的定义：
>apply() 方法调用一个函数, 其具有一个指定的this值，以及作为一个数组（或类似数组的对象）提供的参数
```
fun.apply(thisArg, [argsArray])
```

apply()接受两个参数，具体如下   

thisArg

>在 fun 函数运行时指定的 this 值。需要注意的是，指定的 this 值并不一定是该函数执行时真正的 this 值，如果这个函数处于非严格模式下，则指定为 null 或 undefined 时会自动指向全局对象（浏览器中就是window对象），同时值为原始值（数字，字符串，布尔值）的 this 会指向该原始值的自动包装对象。

argsArray
>一个数组或者类数组对象，其中的数组元素将作为单独的参数传给 fun 函数。如果该参数的值为null 或 undefined，则表示不需要传入任何参数。从ECMAScript 5 开始可以使用类数组对象。浏览器兼容性请参阅本文底部内容。


## 走起来！

很多人看到这个题目已经被吓到了，尤其是像我这样初出茅庐的愣头青   

其实apply()做的事很简单。对于a.apply(b)，思考以下的代码
```
b.fakeA = a;
b.fakeA();
delete b.fakeA;
```

很简单是吧～   
照着这个思路，按照定义来走，就可以了

### 一个简单的实现

首先给出一个这道题目的实现   
```
Function.prototype.fakeApply = function(context, args) {
  const tempContext = context || window;
  const fn = Symbol('fn');
  tempContext[fn] = this;
  tempContext[fn](...args);
  delete tempContext[fn];
}
```

### 分析一下

#### 上下文的处理
```
const tempContext = context || window;
```
注意对于apply()的第一个参数，如果为空的话，默认是window

#### 使用Symbol为上下文添加属性
```
const fn = Symbol('fn');
tempContext[fn] = this;
```
使用es6的Symbol可以为context添加一个唯一key的属性      
如果不使用Symbol，很难保证我们添加的key与context原有的key不冲突

#### 使用es6的解构将args展开供函数调用
```
tempContext[fn](...args);
```
由于apply的第二个参数是以数组的形式传递进来的，在调用的时候要想办法将其展开   
es6的解构很完美的解决了这个需求   
***
以上，这个题目基本完成   
祝好～
