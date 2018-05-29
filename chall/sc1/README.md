## 一道考察作用域的小题

这道题依然考察了作用域与变量声明，角度比较奇特，来看看

```
var name = 'World!';
(function() {
  if (typeof name === 'undefined') {
    var name = 'Jack';
    console.log('Goodbye ' + name);
  } else {
    console.log('Hello ' + name);
  }
})();

(function() {
  if (typeof name === 'undefined') {
    name = 'Jack';
    console.log('Goodbye ' + name);
  } else {
    console.log('Hello ' + name);
  }
})();

(function() {
  if (typeof name === 'undefined') {
    console.log('Goodbye ' + name);
  } else {
    var name = 'Jack';
    console.log('Hello ' + name);
  }
})();

```

注意看两个立即执行函数里对```name```的声明

### 走起来!

先说一下最后的输出：
```
Goodbye Jack
Hello World
Goodbye undefined
```

只要你知道变量声明的原理，这道题肯定so easy

#### 首先要知道的是，立即执行函数拥有自己的作用域   

来看第一个立即执行函数  
 
```
(function() {
  if (typeof name === 'undefined') {
    var name = 'Jack';
    console.log('Goodbye ' + name);
  } else {
    console.log('Hello ' + name);
  }
})();
```

这段函数在引擎中被编译后会成为这个样子   
```
(function() {
  var name;
  if (typeof name === 'undefined') {
    name = 'Jack';
    console.log('Goodbye ' + name);
  }
  //...
})();

```   

其内部又定义了一个```name```，经过变量声明提升，在```if```判断中其值为```undefined```   

所以他的结果是```Goodbye Jack```   

对于第二个立即执行函数   

```
var name = 'World!';

//...

(function() {
  if (typeof name === 'undefined') {
    name = 'Jack';
    console.log('Goodbye ' + name);
  } else {
    console.log('Hello ' + name);
  }
})();
```

因为内部没有定义```name```，```name```的作用域是外部定义的那个```name```   

而**第一个立即执行函数中定义的```name```其作用域仅限于这个立即执行函数**，和最外层声明的```name```没有关系   

没有什么疑问，它的结果是```Hello World```   

最后一个立即执行函数   

```
(function() {
  if (typeof name === 'undefined') {
    console.log('Goodbye ' + name);
  } else {
    var name = 'Jack';
    console.log('Hello ' + name);
  }
})();
```   

经过引擎编译后，他会变成这个样子   

```
(function() {
  var name;
  if (typeof name === 'undefined') {
    console.log('Goodbye ' + name);
  } else {
    name = 'Jack';
    console.log('Hello ' + name);
  }
})();
```   

他的结果是```Goodbye undefined```   

---

小题很简单，要是说不上答案可要扪心自问一下咯~