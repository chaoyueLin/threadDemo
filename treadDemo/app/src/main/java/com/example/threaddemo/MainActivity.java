package com.example.threaddemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.tv).setOnClickListener(v -> new Thread(new Runnable() {
            @Override
            public void run() {
                ThreadUtil.print();
                Log.d("test", "test");
            }
        }).start());
        findViewById(R.id.join).setOnClickListener(v -> {
            tryJoin();
        });
        WaitNotifyDemo waitNotifyDemo = new WaitNotifyDemo();
        findViewById(R.id.start).setOnClickListener(v -> {
            //start和notify必须运行在不同线程
            new Thread(() -> {
                try {
                    waitNotifyDemo.startWait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();

        });


        findViewById(R.id.notify).setOnClickListener((v) -> {
            waitNotifyDemo.startNotify();
        });
    }

    public void tryJoin() {
        Thread threadA = new ThreadA();
        Thread threadB = new ThreadB(threadA);
        threadA.start();
        threadB.start();
    }


    public static class ThreadA extends Thread {
        @Override
        public void run() {
            Log.d("test", "线程 A 开始执行");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.d("test", "线程 A 执行结束");
        }
    }

    public static class ThreadB extends Thread {
        private final Thread threadA;

        public ThreadB(Thread thread) {
            threadA = thread;
        }

        @Override
        public void run() {
            try {
                Log.d("test", "线程 B 开始等待线程 A 执行结束");
                threadA.join();
                Log.d("test", "线程 B 结束等待，开始做自己想做的事情");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


}