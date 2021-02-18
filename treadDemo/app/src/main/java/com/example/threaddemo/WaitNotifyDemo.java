package com.example.threaddemo;

import android.util.Log;

public class WaitNotifyDemo {
    private static final String TAG = "WaitNotifyDemo";
    final Object lock = new Object();
    private volatile boolean conditionSatisfied;

    /**
     * @throws InterruptedException
     */
    public void startWait() throws InterruptedException {
        synchronized (lock) {
            Log.d(TAG, "等待线程获取了锁");
            while (!conditionSatisfied) {
                Log.d(TAG, "保护条件不成立，等待线程进入等待状态");
                lock.wait();
            }
            Log.d(TAG, "等待线程被唤醒，开始执行目标动作");
        }

    }

    /**
     *
     */
    public void startNotify() {
        synchronized (lock) {
            Log.d(TAG, "通知线程获取了锁");
            Log.d(TAG, "通知线程即将唤醒等待线程");
            conditionSatisfied = true;
            lock.notify();
        }
    }
}
