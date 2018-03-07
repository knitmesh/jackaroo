
### 创建和使用同步执行程序

```python
# NOTE: enable printing timestamp for additional data

import sys
import futurist
import eventlet

def delayed_func():
    print("started")
    eventlet.sleep(3)
    print("done")

#print(datetime.datetime.now().strftime("%Y-%m-%d %H:%M:%S"))
e = futurist.SynchronousExecutor()
fut = e.submit(delayed_func)
eventlet.sleep(1)
print("Hello")
eventlet.sleep(1)
e.shutdown()
#print(datetime.datetime.now().strftime("%Y-%m-%d %H:%M:%S"))

```

Expected output:

```
started
done
Hello
```

创建和使用基于green线程的执行程序
```python
# NOTE: enable printing timestamp for additional data

import sys
import futurist
import eventlet

def delayed_func():
    print("started")
    eventlet.sleep(3)
    print("done")

#print(datetime.datetime.now().strftime("%Y-%m-%d %H:%M:%S"))
e = futurist.GreenThreadPoolExecutor()
fut = e.submit(delayed_func)
eventlet.sleep(1)
print("Hello")
eventlet.sleep(1)
e.shutdown()
#print(datetime.datetime.now().strftime("%Y-%m-%d %H:%M:%S"))

```
Expected output:

```
started
Hello
done
```

创建和使用基于线程的执行程序
```python
import time

import futurist

def delayed_func():
    time.sleep(0.1)
    return "hello"

e = futurist.ThreadPoolExecutor()
fut = e.submit(delayed_func)
print(fut.result())
e.shutdown()

```
例2
```python
import time

import futurist

speak = []

def delayed_func1():
    time.sleep(0.1)
    speak.append("hello1")
    
def delayed_func2():
    time.sleep(0.1)
    speak.append("hello2")
    
def delayed_func3():
    time.sleep(0.1)
    speak.append("hello3")

with futurist.ThreadPoolExecutor(max_workers=3) as e:
    e.submit(fn=delayed_func1)
    e.submit(fn=delayed_func2)
    e.submit(fn=delayed_func3)

print(speak)

```
Expected output:

```
Hello
```


创建和使用基于进程的执行程序
```python

import time

import futurist

def delayed_func():
    time.sleep(0.1)
    return "hello"

e = futurist.ProcessPoolExecutor()
fut = e.submit(delayed_func)
print(fut.result())
e.shutdown()
```
Expected output:

```
hello
```


定期运行一组函数
```python
import futurist
from futurist import periodics

import time
import threading


@periodics.periodic(1)
def every_one(started_at):
    print("1: %s" % (time.time() - started_at))


@periodics.periodic(2)
def every_two(started_at):
    print("2: %s" % (time.time() - started_at))


@periodics.periodic(4)
def every_four(started_at):
    print("4: %s" % (time.time() - started_at))


@periodics.periodic(6)
def every_six(started_at):
    print("6: %s" % (time.time() - started_at))


started_at = time.time()
callables = [
    # The function to run + any automatically provided positional and
    # keyword arguments to provide to it everytime it is activated.
    (every_one, (started_at,), {}),
    (every_two, (started_at,), {}),
    (every_four, (started_at,), {}),
    (every_six, (started_at,), {}),
]
w = periodics.PeriodicWorker(callables)

# In this example we will run the periodic functions using a thread, it
# is also possible to just call the w.start() method directly if you do
# not mind blocking up the current program.
t = threading.Thread(target=w.start)
t.daemon = True
t.start()

# Run for 10 seconds and then stop.
while (time.time() - started_at) <= 10:
    time.sleep(0.1)
w.stop()
w.wait()
t.join()

```


定期运行一组函数（使用执行程序）
```python
import futurist
from futurist import periodics

import time
import threading


@periodics.periodic(1)
def every_one(started_at):
    print("1: %s" % (time.time() - started_at))
    time.sleep(0.5)


@periodics.periodic(2)
def every_two(started_at):
    print("2: %s" % (time.time() - started_at))
    time.sleep(1)


@periodics.periodic(4)
def every_four(started_at):
    print("4: %s" % (time.time() - started_at))
    time.sleep(2)


@periodics.periodic(6)
def every_six(started_at):
    print("6: %s" % (time.time() - started_at))
    time.sleep(3)


started_at = time.time()
callables = [
    # The function to run + any automatically provided positional and
    # keyword arguments to provide to it everytime it is activated.
    (every_one, (started_at,), {}),
    (every_two, (started_at,), {}),
    (every_four, (started_at,), {}),
    (every_six, (started_at,), {}),
]

# To avoid getting blocked up by slow periodic functions we can also
# provide a executor pool to make sure that slow functions only block
# up a thread (or green thread), instead of blocking other periodic
# functions that need to be scheduled to run.
executor_factory = lambda: futurist.ThreadPoolExecutor(max_workers=2)
w = periodics.PeriodicWorker(callables, executor_factory=executor_factory)

# In this example we will run the periodic functions using a thread, it
# is also possible to just call the w.start() method directly if you do
# not mind blocking up the current program.
t = threading.Thread(target=w.start)
t.daemon = True
t.start()

# Run for 10 seconds and then stop.
while (time.time() - started_at) <= 10:
    time.sleep(0.1)
w.stop()
w.wait()
t.join()

```


停止周期性函数再次运行（使用NeverAgain异常）

```python

import futurist
from futurist import periodics

import time
import threading


@periodics.periodic(1)
def run_only_once(started_at):
    print("1: %s" % (time.time() - started_at))
    raise periodics.NeverAgain("No need to run again after first run !!")


@periodics.periodic(1)
def keep_running(started_at):
    print("2: %s" % (time.time() - started_at))


started_at = time.time()
callables = [
    # The function to run + any automatically provided positional and
    # keyword arguments to provide to it everytime it is activated.
    (run_only_once, (started_at,), {}),
    (keep_running, (started_at,), {}),
]
w = periodics.PeriodicWorker(callables)

# In this example we will run the periodic functions using a thread, it
# is also possible to just call the w.start() method directly if you do
# not mind blocking up the current program.
t = threading.Thread(target=w.start)
t.daemon = True
t.start()

# Run for 10 seconds and then stop.
while (time.time() - started_at) <= 10:
    time.sleep(0.1)
w.stop()
w.wait()
t.join()
```

当不再有周期性工作时，自动停止定期工作
```python

import futurist
from futurist import periodics

import time
import threading


@periodics.periodic(1)
def run_only_once(started_at):
    print("1: %s" % (time.time() - started_at))
    raise periodics.NeverAgain("No need to run again after first run !!")


@periodics.periodic(2)
def run_for_some_time(started_at):
    print("2: %s" % (time.time() - started_at))
    if (time.time() - started_at) > 5:
        raise periodics.NeverAgain("No need to run again !!")


started_at = time.time()
callables = [
    # The function to run + any automatically provided positional and
    # keyword arguments to provide to it everytime it is activated.
    (run_only_once, (started_at,), {}),
    (run_for_some_time, (started_at,), {}),
]
w = periodics.PeriodicWorker(callables)

# In this example we will run the periodic functions using a thread, it
# is also possible to just call the w.start() method directly if you do
# not mind blocking up the current program.
t = threading.Thread(target=w.start, kwargs={'auto_stop_when_empty': True})
t.daemon = True
t.start()

# Run for 10 seconds and then check to find out that it had
# already stooped.
while (time.time() - started_at) <= 10:
    time.sleep(0.1)
print(w.pformat())
t.join()
```