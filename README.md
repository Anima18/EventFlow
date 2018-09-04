# EventFlow
一个Android的事件流框架，简化多重事件传递，使得事件可以链式调用。  

android中的处理集可以称为事件。  
事件可以分 **异步事件**(比如说网络请求) 和 **同步事件**(比如说UI事件) ；  
事件可以分 **耗时事件**(比如大量读写数据) 和 **非耗时操作**(比如简单的数据处理)。  
这是事件需要在不同的线程执行，如何在不同的线程切换时，保证事件的执行顺序呢？  

EventFlow是对RxJava进行封装，并提供一下功能：  
1. 支持嵌套事件，下一个事件需要居于上个事件的结果。
2. 支持顺序事件，保证多个事件的执行顺序，并把事件结果返回
3. 支持并发事件，同时并发执行多个事件，并保证事件结果的顺序。


## 使用


```
compile 'com.anima:EventFlow:1.0.4'
```

## 示例
1. 嵌套事件

```
Event event1 = new Event() {
    @Override
    protected Object run() {
        return "1";
    }
};

EventFlow.create(this, event1).showMessage("嵌套请求中，请稍后...").nest(new EventFlow.NestFlatMapCallback() {
    @Override
    public Event flatMap(final Object o) {
        return new Event() {
            @Override
            protected Object run() {
                return o.toString() + "1";
            }
        };
    }
}).subscribe(new EventResult() {
    @Override
    public void onResult(Object data) {
        Log.d(TAG, data.toString());
    }
});
```

2. 顺序事件

```
Event event1 = new Event() {
    @Override
    protected Object run() {
        return "1";
    }
};

Event event2 = new Event() {
    @Override
    protected Object run() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
};

EventFlow.create(this)
        .showMessage("顺序请求中，请稍后...")
        .sequence(event1)
        .sequence(event2)
        .subscribe(new EventResultList() {
            @Override
            public void onResult(List<Object> dataList) {
                Log.d(TAG, dataList.toString());
            }
        });
```
3. 并发事件

```
Event event1 = new Event() {
    @Override
    protected Object run() {
        return "1";
    }
};

Event event2 = new Event() {
    @Override
    protected Object run() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "2";
    }
};

EventFlow.create(this)
        .showMessage("并发请求中，请稍后...")
        .merge(event1)
        .merge(event2)
        .subscribe(new EventResultList() {
            @Override
            public void onResult(List<Object> dataList) {
                Log.d(TAG, dataList.toString());
            }
        });
```



