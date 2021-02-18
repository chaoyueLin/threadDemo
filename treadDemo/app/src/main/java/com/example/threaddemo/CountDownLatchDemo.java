package com.example.threaddemo;

import android.util.Log;

import java.util.concurrent.CountDownLatch;

public class CountDownLatchDemo {
    private static final String TAG="CountDownLatchDemo";
    final CountDownLatch latch = new CountDownLatch(1);
    private int result=0;
    public void read(){
        new Thread(()->{
            Log.d(TAG,"耗时读取开始了");
            try {
                Thread.sleep(3000);
                result=100;
                latch.countDown();
                Log.d(TAG,"耗时读取结束了");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void readWait(){
        try {
            Log.d(TAG,"单独线程读取");
            latch.await();
            Log.d(TAG,"单独线程读取到结果了 result="+result);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
