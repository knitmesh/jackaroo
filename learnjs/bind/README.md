## 今天来实现一个bind()方法

先看一下bind()的定义：
> bind() 函数会创建一个新函数（称为绑定函数），新函数与被调函数（绑定函数的目标函数）具有相同的函数体（在 ECMAScript 5 规范中内置的call属性）。当目标函数被调用时 this 值绑定到 bind() 的第一个参数，该参数不能被重写。绑定函数被调用时，bind() 也接受预设的参数提供给原函数。一个绑定函数也能使用new操作符创建对象：这种行为就像把原函数当成构造器。提供的 this 值被忽略，同时调用时的参数被提供给模拟函数。

#### 翻译翻译   
bind()主要实现了三个功能：
- 改变调用函数的this
- 函数柯里化（预设参数给原函数）
- 使用new时将原函数当做构造器并忽略提供的this值


知晓了其功能，来具体实现一下
## 走起来！

这里参照MDN上的polyfill来给出一种实现   
（此实现基于apply()，关于apply()的实现可以参照[这里](https://github.com/lidad/every-day-a-challenge/tree/master/apply)）

```
Function.prototype.bind || Object.defineProperty(Function.prototype, 'bind', {
  value: function(context, ...curryArgs) {
    const fn = this;
    const noop = function() {};
    const bound = function(...args) {
      return fn.apply(this instanceof noop
        ? this
        : context || this, [
        ...curryArgs,
        ...args
      ]);
    }

    noop.prototype = this.prototype;
    bound.prototype = new noop();

    return bound;
  },
  enumerable: false
})
```

### 分析一下

该实现返回了一个新的函数，新函数用apply()改变了上下文并实现了函数柯里化

#### Object.defineProperty()

因为bind()在定义中是不可遍历的属性，使用defineProperty将其descriptor的enumerable置为false

#### apply()
主要功能使用apply()来实现
```
const fn = this;
//...
const bound = function(...args) {
  return fn.apply(context, [
    ...curryArgs,
    ...args
  ]);
}
//...
return bound;
```   

1. **使用fn定义了当前的被调用函数**   
    ```
    const fn = this;
    ```
2. **使用apply()改变fn的上下文this**   

    apply()不仅改变了this，还实现了柯里化
    ```
    fn.apply(context, [
      ...curryArgs,
      ...args
    ])//注意此处的两个解构
    ```
3. **返回bound**   

    要注意的是，bound定义没有使用箭头函数而使用了function，这是因为**箭头函数不能作为构造器**使用new 来调用！   


#### noop及其prototype
```
//...
const noop = function() {};
//...

//bound内，判断是否使用new来调用返回函数
fn.apply(this instanceof noop
  ? this
  : context || this, ...)

//...
noop.prototype = this.prototype;
bound.prototype = new noop();
//...
```
noop的作用主要是为了处理new调用返回函数时原函数的this及prototype   
处理后返回函数的prototype便不会受到影响   

***
以上便是整个的实现及其分析，希望大家能在理解的基础上深入思考   
祝好～