## 今天来一个小题，初始化一个数组

这道题目是我自己杜撰的︿(￣︶￣)︿
大概要求为：

- 为Array对象添加一个属性 initArr
- initArr为一个函数，接受一个参数 len，返回一个长度为len的数组，数组元素从1到 len
- 尽量不要使用loop

测试用例如下
```
const arr = Array.initArr(3);
arr //[1,2,3]
```

虽然看起来很简单，但毫不夸张，很多来面试的同学面对这道题都没有给出很好的解决(⊙﹏⊙)
***

## 走起来！

分析一下题目
- 首先是要为Array添加属性，很多同学想都不想直接就Array.proyotype.initArr...
- 要求不用loop，那么map，reduce（脑路清奇也可以递归....）

考察的知识点包括：
- Object.defineProperty()
- Array的了解程度
- Array的API

先给出一个标准的答案：
```
Object.defineProperty(Array, 'initArr', {
  value: (len) => Array.apply(null, {length: len}).map((item, i) => i + 1),
  writable: false
})
```

### 分析一下具体实现

#### 使用Object.defineProperty()定义属性，并在descriptor将writable置为false防止修改
> Object.defineProperty() 方法会直接在一个对象上定义一个新属性，或者修改一个对象的现有属性， 并返回这个对象。   

使用Object.defineProperty()将initArr直接定义在Array上，而不是为Array.proyotype添加。   
需要稍加注意的是Object.defineProperty()的用法   

#### Array.apply(null, {length: len})返回一个长度为len的数组

这里先要说一下Array(len)与Array.apply(null, {length: len})的区别   
Array(len)即为new Array(len)，它返回了一个长度为len的**空**的数组   
要注意的是，这个数组元素是**空**的，并不是undefined，这个数组也不能使用数组的map()进行操作
```
//chrome下
const testArr = Array(3)
testArr //(3) [undefined × 3]
testArr.map(item=>console.log(item)) //(3) [undefined × 3]
```

对于Array.apply(null, {length: len})，它返回了一个长度为len的数组，数组元素是undefined，可以使用map()进行操作
```
//chrome下
const testArr = Array.apply(null, {length: 3})
testArr //(3) [undefined, undefined, undefined]  注意这里
testArr.map(item=>console.log(item))
// undefined
// undefined
// undefined
```

#### 使用map()返回一个从1到len的数组

这里就没有什么好说的了，要注意的是map()接收的函数中第一个参数为当前的数组元素，第二个参数为当前元素在数组中的下标   



题目不是很难，但考察的了面试者在平时的编码中是否有深入思考过   
希望大家能够消化掉这个题目并在题目中拓展一下自己的思维
