## 大整数加法
实现一个大整数的加法器   

大整数可能比Number.MAX_VALUE大，所以...好好思考一下哈，很简单的

```
var BigInt = function (str) {
 //code here
};

BigInt.prototype.plus = function(bigint) {
 //code here
};

BigInt.prototype.toString = function () {
 //code here
};

var bigint1 = new BigInt('1234232453525454546445458814343421545454545454');
var bigint2 = new BigInt('1234232453525454546445459914343421536654545454');

console.log(bigint1.plus(bigint2));
```

### 走起来！   

先来给出一个实现   

```
var BigInt = function(str) {
  this.bigInt = str;
};

BigInt.prototype.plus = function(bigint) {
  const bigInt = '' + bigint;
  const { longNumber, shortNumber } = this.bigInt.length >= bigInt.length ? {
    longNumber: this.bigInt.split(''),
    shortNumber: bigInt.split('')
  } : {
    longNumber: bigInt.split(''),
    shortNumber: this.bigInt.split('')
  }

  const { result } = longNumber.reduceRight((tempResult, firstSingleNum) => {
    const { result, carry } = tempResult;
    const secondSingleNum = shortNumber.pop() || 0;
    const tempAddResult = +firstSingleNum + (+secondSingleNum) + carry;

    return {
      result: tempAddResult % 10 + result,
      carry: tempAddResult > 9
    }
  }, {
    result: '',
    carry: 0
  });

  return result;
};

BigInt.prototype.toString = function() {
  return this.bigInt;
};
```   

这道题考察的点其实是js中```string```类型的长度

string类型长度很长，以前在知乎上贺老的答案中看到过具体数字，很惭愧没有深究。。。只是记住了贺老的一句话：“实际情况中```string```的长度只会受引擎内存的限制”。也就是说规范中它的长度已经超过了现如今引擎的内存。。。。。   

对于这道题来说，其关键在于以```string```为存储方式来重现整数的加法   

有了思路具体实现起来就很简单了

直接来看```BigInt```原型链上的```plus```方法   

先将参数转化为字符串，然后挑出两个参数中较长的与较短的，以方便接下来的操作

```
  const bigInt = '' + bigint;
  const { longNumber, shortNumber } = this.bigInt.length >= bigInt.length ? {
    longNumber: this.bigInt.split(''),
    shortNumber: bigInt.split('')
  } : {
    longNumber: bigInt.split(''),
    shortNumber: this.bigInt.split('')
  }
```   

接下来遍历较长的一个加数，同时在较短的参数中取对位进行相加操作，若对位没有则置为0   

这里采用了```Array```内置的```reduce()```API，好用的一笔   

```
const { result } = longNumber.reduceRight((tempResult, firstSingleNum) => {
    const { result, carry } = tempResult;
    const secondSingleNum = shortNumber.pop() || 0;
    const tempAddResult = +firstSingleNum + (+secondSingleNum) + carry;

    return {
      result: tempAddResult % 10 + result,
      carry: tempAddResult > 9
    }
  }, {
    result: '',
    carry: 0
  });
```   

注意对结果和进位的处理   

当前相加的结果是整数，而之前的结果保存为字符串，这里利用整数与字符串相加时的类型转换机制去自动处理   

进位保存为bool型，相加时也是利用bool与整数加法时的类型转换机制   

---

还有一些细节地方有欠处理，但对于这道题的case以上解决方案已经足够了   

强调一下，大伙儿还是要对js的基础重视起来，这道题所考查的东西也不过是js基础中的基础啦~
