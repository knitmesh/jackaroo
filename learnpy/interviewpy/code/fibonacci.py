# -*- coding: utf-8 -*-

# 斐波那契数列即著名的兔子数列：1、1、2、3、5、8、13、21、34、……
# 数列特点：该数列从第三项开始，每个数的值为其前两个数之和，用python实现起来很简单：
# F(1)=1，F(2)=1, F(n)=F(n-1)+F(n-2)（n>=2，n∈N*）

# 1. 生成数列

# 1.1 求小于n以内的数列
def fib1(n):
    a = 0
    b = 1
    while b < n:
        print(b, end=',')
        a, b = b, a + b

        # 这里 a, b = b, a+b 右边的表达式会在赋值前执行，即先执行右边，
        # 比如第一次循环得到b-->1,a+b --> 0+1 然后再执行赋值 a,b =1,0+1,所以执行完这条后a=1,b=1
    print()


# 1.2 递归方式实现 生成前20项
def fib2(n):
    lis =[]
    for i in range(n):
        # 第1,2项 都为1
        if i < 2:
            lis.append(1)
        else:
            # 从第3项开始每项值为前两项值之和
            lis.append(lis[i-2]+lis[i-1])
    print(lis)

# 2 台阶问题/斐波那契
# 一只青蛙一次可以跳上1级台阶，也可以跳上2级。求该青蛙跳上一个n级的台阶总共有多少种跳法。

# 2.1 lambda 方式递归
fib3 = lambda n: n if n <= 2 else fib3(n - 1) + fib3(n - 2)


# 2.2 装饰器法
def memo(func):
    cache = {}
    def wrap(*args):
        if args not in cache:
            cache[args] = func(*args)
        return cache[args]
    return wrap


@memo
def fib4(i):
    if i < 2:
        return 1
    return fib4(i-1) + fib4(i-2)


# 2.3 变量置换法
def fib5(n):
    a, b = 0, 1
    for _ in range(n):
        a, b = b, a + b
    return b


# 3. 变态台阶问题
# 一只青蛙一次可以跳上1级台阶，也可以跳上2级……它也可以跳上n级。求该青蛙跳上一个n级的台阶总共有多少种跳法

# 分析：
# 相比上一个跳台阶，这次可以从任意台阶跳上第n级台阶，也可以直接跳上第n级。因此其递归公式为各个台阶之和再加上直接跳上去的一种情况。
#
# F（0）= 0
# F（1）= 1
# F（2）= 2
# F（n）= F（n-1）+ F（n-2）+ … + F（2）+ F（1）+ 1 = 2 **（n-1）

# 相当于2的n次方-1
def fib6(number):
    if number == 0:
        return 0
    return 2**(number-1)


# 递归方式实现
def fib7(n):
    if n < 2:
        return n
    else:
        a = fib7(n - 1) * 2
        return a

fib8 = lambda n: n if n < 2 else fib6(n - 1) * 2

# 4. 矩形覆盖
# 我们可以用2*1的小矩形横着或者竖着去覆盖更大的矩形。请问用n个2*1的小矩形无重叠地覆盖一个2*n的大矩形，总共有多少种方法？


# 分析：
# 仔细分析这个问题实际上就是普通的跳台阶问题。
#
# F（0）= 0
# F（1）= 1
# F（2）= 2
# F（n）= F（n-1）+ F（n-2）（n > 2）

f1 = lambda n: 1 if n < 2 else f1(n - 1) + f1(n - 2)

# 5. 杨氏矩阵查找
# 在一个m行n列二维数组中，每一行都按照从左到右递增的顺序排序，每一列都按照从上到下递增的顺序排序。
# 请完成一个函数，输入这样的一个二维数组和一个整数，判断数组中是否含有该整数。

# 使用Step-wise线性搜索。
# 思路: 从右上角开始，每次将搜索值与右上角的值比较，如果大于右上角的值，则直接去除1行，否则，则去掉1列
# 参考: https://blog.csdn.net/pi9nc/article/details/9082997
def find1(l, x):
    m = len(l) - 1
    n = len(l[0]) - 1
    r = 0
    c = n
    while c >= 0 and r <= m:
        value = l[r][c]
        if value == x:
            return True
        elif value > x:
            c = c - 1
        elif value < x:
            r = r + 1
    return False

if __name__ == "__main__":
    num = 6
    fib1(num)
    fib2(num)
    print(fib3(num))
    print(fib4(num))
    print(fib5(num))
    print(fib6(num))
    print(fib7(num))
    print(fib8(num))
    print(f1(num))
    l = [
        [1, 4, 7, 11, 15],
        [2, 5, 8, 12, 19],
        [3, 6, 9, 16, 22],
        [10, 13, 14, 17, 24],
        [18, 21, 23, 26, 30],
    ]
    print(find1(l, 12))
