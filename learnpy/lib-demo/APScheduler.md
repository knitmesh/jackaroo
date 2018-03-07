

- jobs.py
```python
import os

from apscheduler.schedulers.background import BackgroundScheduler

sched = BackgroundScheduler()

ENVIRONMENT = os.getenv("DJANGO_ENVIRONMENT", "prod")

if ENVIRONMENT != 'test':
    print('before the start funciton')
    sched.start()
    print("let us figure out the situation")


# licenses Authorization checking
@sched.scheduled_job('interval', seconds=300)
def licenses_job():
    print('This job is run every five minutes.')

```

- urls.py 在django 生命周期中  urls.py 文件只会被调用一次, 随系统启动的定时任务适合在此触发
```python

...

# licensuthorization checking job
from . import jobs

```