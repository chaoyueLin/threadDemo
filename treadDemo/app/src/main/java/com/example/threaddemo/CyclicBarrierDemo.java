package com.example.threaddemo;

import android.util.Log;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class CyclicBarrierDemo {
    private static final String TAG = "CyclicBarrierDemo";
    int N = 4;


    public void main() {
        CyclicBarrier barrier = new CyclicBarrier(4, new Runnable() {

            @Override
            public void run() {
                Log.d(TAG, Thread.currentThread().getName() + ",后续流程");
            }
        });
        for (int i = 0; i < N; i++) {
            new Writer(barrier).start();
        }
    }

    static class Writer extends Thread {
        CyclicBarrier barrier;

        Writer(CyclicBarrier barrier) {
            this.barrier = barrier;
        }

        @Override
        public void run() {
            Log.d(TAG, Thread.currentThread().getName() + ",正在写入数据");
            try {
                Thread.sleep(3000);
                barrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "所有线程都写完了，才继续下面的流程");
        }
    }
}
