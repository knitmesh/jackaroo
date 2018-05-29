## 如何实现一个深拷贝
深拷贝算得上是一个相对具有深度的题目了   

一般在面试的过程中我也比较喜欢去问面试者深拷贝相关的东西   

如果一个面试者对深拷贝能有一些自己的理解和实现，我会觉得他对JavaScript的理解有些深度和广度，是一个不错的印象  

### 为何要深拷贝？   

做题前先扯一些没用的，为什么我们会需要深拷贝这个东西呢？   

要知道的是JavaScript中对象的调用都是其引用    

看一下下面的代码   

```
const a = [1,2,3];
b = a;
b.push(4);
//此时的a？

const c = {
  d: 1
}
c.e = 2
//c声明为常量怎么还可以修改？

const f = c;
f.d = 3;
//此时c.d？
```   

由于对象引用调用的关系，在为数组```b```添加了元素```4```之后，数组```a```也受到了影响变成了```[1,2,3,4]```   

而```c```仅仅保存的是对对象的引用，为对象添加或修改属性，并没有改变其引用，```c```虽然被声明为常量但赋值操作不会有问题    

JavaScript的这种机制是把双刃剑，对于对象的处理一定程度上节省了内存空间，在引用关系的基础上一些场景下我们的操作会方便很多。但如果对其不是很熟悉，就会埋下一些暗坑。有时候代码读过去觉得是好的，跑起来就会出错   

而对于react类库中的pure render来说，官方为我们提供了一个基于浅比较的[pureComponent](https://facebook.github.io/react/docs/react-api.html)，但由于是浅比较，在涉及复杂数据结构深层变化的时候它就GG了。。深拷贝可以解决这个问题，无奈其本身也有性能问题，对于追求高性能的react这也是相悖的（immutable快出来，哈哈）   

### 体验一下深拷贝

JavaScript已经为我们提供了一些具有浅拷贝性质的API   

比如说想要拷贝一个数组，```concat()```与```slice()```都可以实现   

```
var a = [1,2,3];
var fakeA = a;
var deepA = a.slice();
var anotherDeepA= a.concat();

deepA  //[1,2,3]
anotherDeepA  //[1,2,3]

deepA === a  //false
anotherDeepA === a  //false
fakeA === a  //true

deepA.push('deepA');
anotherDeepA.push('otherDeepA');
fakeA.push('a');

a  //[1,2,3,'a'] 
fakeA  //[1,2,3,'a'] 
deepA  //[1,2,3,'deepA']
anotherDeepA  //[1,2,3,'otherDeepA']
```   

可以看到，虽然经过浅拷贝的```deepA```与```anotherDeepA```已经与原来的数组```a```脱离了联系，不再会相互影响，但两个方法只是会对简单的属性起作用，若数组元素为对象的话其保存的依然是引用   

```
var foo = [1, 2, 3];
foo.push({ a: 2 });

var bar = foo.concat();
bar[3].a = 4;

foo[3]  //{ a: 4 }
```
上面的例子中可以看到，由于```foo[3]```保存的是引用，对```bar[3]```的操作影响到了```foo[3]```

深拷贝复制了一个对象，但和之前的对象具有不同的引用。不仅如此，被拷贝对象中的引用也全部返回了新的引用。经过深拷贝的对象与原对象相比具有相同的值，其在内存中保存了两份   

实现深拷贝也就是要实现两件事：   

1. 返回一个新的引用
2. 新的引用中保存了和原来对象相同的内容
3. 若是拷贝对象中有引用，重复以上的操作

光是想一想就可以体验到深拷贝的性能损耗！  
   
这里给出两种实现方法，一种基于json的序列化与反序列化，一种基于递归

#### json的序列化与反序列化

代码很简单：   

```
function deepClone(initalObj){
  if(typeof initalObj !== 'object'){
    throw new Error('param must be an object!');
  }

  return JSON.parse(JSON.stringify(initalObj));
}
```  

通过```JSON```对象提供的序列化与反序列化方法，可以很简单的实现一个对象的深拷贝    

```JSON.stringify()```可以把一个对象序列化为一个JSON串，而```JSON.parse()```可以将一个JSON串反序列化解析为一个对象   

如此一来一往，便返回了一个和原对象完全脱离引用关系的新对象   

需要注意的是，若是原对象中具有循环引用的case这种处理方式就束手无策了   

#### 基于递归的深拷贝

```
function deepClone(initalObj) {
  if(typeof initalObj !== 'object'){
    throw new Error('param must be an object!');
  }
  
  const newObj = initalObj instanceof Array ? [] : {};
  for (let key in initalObj) {
    if (initalObj.hasOwnProperty(key)) {
      newObj[key] = typeof initalObj[key] === 'object' ? deepCopy(initalObj[key]) : initalObj[key];
    }
  }
  return newObj;
}
```

在对每一个对象属性的递归过程中，其赋值操作避免了对象类型属性的引用    

乍一想好像很复杂，但写出来可以看到还是蛮简单的    

同样，这种基于递归的深拷贝实现对具有循环引用的case无法正确处理

---   

差不多就是这样了，实现不是很复杂，理解了深浅拷贝的前后因果想必大家对JavaScript的认识也会入木三分把~