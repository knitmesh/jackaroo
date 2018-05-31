## 一道考察闭包与作用域的小题

这道题简单的考察了闭包与作用域，很有意思   

```
function test() {
  var n = 1;

  function add() {
    n++;
    console.log(n)
  }

  return {n: n, add: add}
}

var result = test();
var result2 = test();

result.add();
result.add();
console.log(result.n);
result2.add();
```

注意```test()```中定义和返回的东西，不要看走眼咯😀   

### 走起来！

先说一下，最后输出的结果是   

```
2
3
1
2
```

这道题的精髓是```test()```的返回   

```test()```定义了变量```n```与函数```add```并将其返回，使得```test()```在执行后形成了闭包可以调用```test()```内部```n```和```add```   

要注意的是，```test()```返回了一个对象，**对象的```n```是```test```的```n```的值，```add```是```test()```的```add```**   

```
function test() {
  //...

  return {n: n, add: add}
}
```

这是关键！   

之后定义了两个变量```result```和```result2```   

```
var result = test();
var result2 = test();
```   

要注意的是，虽然```result```和```result2```同时执行```test()```，但**两个闭包的作用域是独立的**，其中的```n```与```add```不会相互影响   

```
result.add();
result.add();
```   

```result```的两次```add```，会影响```result```形成的闭包中的```n```，此时```result```闭包中的```n```为```3```，而```result2```闭包中的```n```依然是```1```！   


```
console.log(result.n);
```

```result```的```n```值为```1```，注意```result```中的```n```与```result```形成闭包中的```n```的区别   

这是```test()```返回结构的一个障眼法   

把代码改成以下看看，会帮助你来理解

```
function test() {
  //...

  return {x: n, add: add}//这里这里这里！
}

result.add();
result.add();
console.log(result.n);//想想这个结果
```   

两个闭包的作用域不会相互影响，所以最后```result2.add()```后闭包里的```n```是```2```
```
result2.add();//2
```

---

这道题是对闭包最基础也是最经典的考察，还是希望大家能够从这道题中举一反三，融会贯通
