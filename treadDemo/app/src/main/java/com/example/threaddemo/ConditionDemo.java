package com.example.threaddemo;

import android.util.Log;

import java.util.Date;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ConditionDemo {

    private static final String TAG = "ConditionDemo";
    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();
    private volatile boolean conditionSatisfied = false;

    private void startWait() {
        lock.lock();
        Log.d(TAG,"等待线程获取了锁");
        try {
            while (!conditionSatisfied) {
                Log.d(TAG,"保护条件不成立，等待线程进入等待状态");
                condition.await();
            }
            Log.d(TAG,"等待线程被唤醒，开始执行目标动作");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
            Log.d(TAG,"等待线程释放了锁");
        }
    }

    public void startNotify() {
        lock.lock();
        Log.d(TAG,"通知线程获取了锁");
        try {
            conditionSatisfied = true;
            Log.d(TAG,"通知线程即将唤醒等待线程");
            condition.signal();
        } finally {
            Log.d(TAG,"通知线程释放了锁");
            lock.unlock();
        }
    }

    private void startTimedWait() throws InterruptedException {
        lock.lock();
        Log.d(TAG,"等待线程获取了锁");
        // 3 秒后超时
        Date date = new Date(System.currentTimeMillis() + 3 * 1000);
        boolean isWakenUp = true;
        try {
            while (!conditionSatisfied) {
                if (!isWakenUp) {
                    Log.d(TAG,"已超时，结束等待任务");
                    return;
                } else {
                    Log.d(TAG,"保护条件不满足，并且等待时间未到，等待进入等待状态");
                    isWakenUp = condition.awaitUntil(date);
                }
            }
            Log.d(TAG,"等待线程被唤醒，开始执行目标动作");
        } finally {
            lock.unlock();
        }
    }


}
