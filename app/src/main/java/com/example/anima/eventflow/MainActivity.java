package com.example.anima.eventflow;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.anima.eventflow.Event;
import com.anima.eventflow.EventFlow;
import com.anima.eventflow.EventResult;
import com.anima.eventflow.EventResultList;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Event_flow_example";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void nest(View view) {
        Log.d(TAG, "===========nest===========");
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
    }

    public void sequence(View view) {
        Log.d(TAG, "===========sequence===========");

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
    }

    public void merge(View view) {
        Log.d(TAG, "===========merge===========");
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
    }
}
