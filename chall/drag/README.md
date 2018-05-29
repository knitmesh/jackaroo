## 原生JS实现 DOM 拖动效果

使用原声js实现DOM拖动   

注意：  
- 事件兼容
- 拖拽边界处理，比如浏览器边界，限制在某个容器内拖动，限制在X轴或Y轴方向拖动等，可以只选择某一种方案来实现
- 拖拽体验，比如拖动连贯性，拖动过程中鼠标位置是否正确等，可以在浏览器中调试   

用例如下：

```
<div id="container" style="border:1px solid red; position: absolute; width:100px; height: 100px">something</div>

<script>
    // code here
</script>
```

### 走起来！

这里使用es6的```class```来给出一种实现   

对于测试用例中直接写在```<script>```标签中的这种形式也势必会面对浏览器的兼容问题   

但既然题设里没有限制，就暂且先这样写啦

```
class DragElement {
  constructor(target) {
    if (!target) {
      throw new Error('can not init without an element!')
    }
    this.target = target;
    this.eleLeft = this._getStyle('left');
    this.eleTop = this._getStyle('top');
    this.targetHeight = parseInt(this._getStyle('height'));
    this.targetWidth = parseInt(this._getStyle('width'));
    this.currentX = 0;
    this.currentY = 0;
    this.isDraging = false;

    target.addEventListener('mousedown', (event) => {
      const e = this._handleEvent(event)
      this.isDraging = true;
      this.currentX = e.clientX;
      this.currentY = e.clientY;
    });

    document.addEventListener('mouseup', () => {
      this.isDraging = false;
      this.eleLeft = this._getStyle('left');
      this.eleTop = this._getStyle('top');
    })

    document.addEventListener('mousemove', (e) => {
      if (this.isDraging) {
        const e = this._handleEvent(event)
        const clientX = this._getDis(e.clientX, document.body.clientWidth, this.targetWidth);
        const clientY = this._getDis(e.clientY, document.body.clientHeight, this.targetHeight);
        const moveX = clientX - this.currentX
        const moveY = clientY - this.currentY;
        this.target.style.left = parseInt(this.eleLeft) + moveX + "px";
        this.target.style.top = parseInt(this.eleTop) + moveY + "px";
        return false;
      }
    })
  }

  _handleEvent(event) {
    return event || window.event;
  }

  _getStyle(style) {
    return this.target.currentStyle ? this.target.currentStyle[style] : window.getComputedStyle(this.target, false)[style];
  }

  _getDis(minDis, maxDis, targetAttr) {
    return Math.min(Math.max(minDis, targetAttr), maxDis - targetAttr);
  }
}

new DragElement(document.querySelector('#container'))

```

这里主要是通过修改元素css的```top```和```left```属性来实现位置变化，有一个很关键的条件就是被拖动元素的```position```必须是```absolute```   

监听元素的```mouseover```事件，同时修改元素位置   

注意题目中对于元素移动边界的要求，元素的移动不能超过容器边界。这里在```mouseover```回调事件中就要有所处理


先来看一下```DragElement```中的三个私有方法   

```_handleEvent()```来处理事件兼容   

```
_handleEvent(event) {
  return event || window.event;
}
```  

```_getStyle()```来获得拖动元素的样式  

```
_getStyle(style) {
  return this.target.currentStyle ? this.target.currentStyle[style] : window.getComputedStyle(this.target, false)[style];
}
```

```_getDis()```处理边界问题   

**若是到了左上边界，返回元素的长宽。到了下右边界，返回边界值与元素长宽的差**   

```
_getDis(minDis, maxDis, targetAttr) {
  //minDis代表元素当前的位置，maxDis为边界，targetAttr代表元素长宽
  return Math.min(Math.max(minDis, targetAttr), maxDis - targetAttr);
}
```   

整个拖动的相关处理逻辑都在```DragElement```的构造函数```constructor```中   

逻辑很简单，绑定了```mousedown```，```mousevoer```与```mouseup```三个事件   

```mousedown```事件中确定元素开始被拖动，将```isDraging```置为```true```并保存元素当前的位置信息   

```mousevoer```事件中处理拖动过程中元素的位置变化   

```mouseup```事件中确定元素拖动结束，将```isDraging```置为```false```  

```
//若没有目标元素，则在构造函数中抛出异常
if (!target) {
   throw new Error('can not init without an element!')
}
this.target = target;

//eleLeft与eleTop保存元素被拖动时的位置信息
this.eleLeft = this._getStyle('left');
this.eleTop = this._getStyle('top');

//targetHeight与targetWidth是元素的长宽信息，用来处理边界问题
this.targetHeight = parseInt(this._getStyle('height'));
this.targetWidth = parseInt(this._getStyle('width'));

//currentX与currentY保存元素开始拖动时的位置，用来计算被拖动时需要移动的位置
this.currentX = 0;
this.currentY = 0;

//isDraging是元素被拖动状态标志。由于mouseover绑定了整个document，需要一个标志在其回调中判断是否需要处理
this.isDraging = false;

//鼠标在元素上按下，元素开始被拖动
target.addEventListener('mousedown', (event) => {
  //处理事件
  const e = this._handleEvent(event)
  this.isDraging = true;
  
  //保存开始拖动时的位置信息
  this.currentX = e.clientX;
  this.currentY = e.clientY;
});

//鼠标在元素上弹起，元素拖动结束
document.addEventListener('mouseup', () => {
  this.isDraging = false;
  
  //保存元素当前的位置，为的是下一次拖动时元素就在此处开始而不会跳动
  this.eleLeft = this._getStyle('left');
  this.eleTop = this._getStyle('top');
})

document.addEventListener('mousemove', (e) => {
  if (this.isDraging) {
    const e = this._handleEvent(event)
    
    //处理边界问题，返回处理的过鼠标位置
    const clientX = this._getDis(e.clientX, document.body.clientWidth, this.targetWidth);
    const clientY = this._getDis(e.clientY, document.body.clientHeight, this.targetHeight);
    
    //计算需要移动的距离
    const moveX = clientX - this.currentX
    const moveY = clientY - this.currentY;
    
    //修改元素css进行元素的移动
    this.target.style.left = parseInt(this.eleLeft) + moveX + "px";
    this.target.style.top = parseInt(this.eleTop) + moveY + "px";
    return false;
  }
})
``` 

---

题目很简单。只要想好了思路，实现起来基本没有什么难点   

可能对原生DOM操作不熟悉的同学一开始会找不到头绪。这个也真没办法，多练练此类题目慢慢就得心应手啦

