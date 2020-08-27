package com.example.threaddemo;

import android.util.Log;

import java.util.Map;

/*****************************************************************
 * * File: - ThreadUtil
 * * Description: 
 * * Version: 1.0
 * * Date : 2020/8/27
 * * Author: linchaoyue
 * *
 * * ---------------------- Revision History:----------------------
 * * <author>   <date>     <version>     <desc>
 * * linchaoyue 2020/8/27    1.0         create
 ******************************************************************/
public class ThreadUtil {
    public static void print() {
        Map<Thread, StackTraceElement[]> threadMap = Thread.getAllStackTraces();
        Log.e("albertThreadDebug", "all start==============================================");
        for (Map.Entry<Thread, StackTraceElement[]> entry : threadMap.entrySet()) {
            Thread thread = entry.getKey();
            StackTraceElement[] stackElements = entry.getValue();
            Log.e("albertThreadDebug", "name:" + thread.getName() + " id:" + thread.getId() + " thread:" + thread.getPriority() + " begin==========");
            for (int i = 0; i < stackElements.length; i++) {
                StringBuilder stringBuilder = new StringBuilder("    ");
                stringBuilder.append(stackElements[i].getClassName() + ".")
                        .append(stackElements[i].getMethodName() + "(")
                        .append(stackElements[i].getFileName() + ":")
                        .append(stackElements[i].getLineNumber() + ")");
                Log.e("albertThreadDebug", stringBuilder.toString());
            }
            Log.e("albertThreadDebug", "name:" + thread.getName() + " id:" + thread.getId() + " thread:" + thread.getPriority() + " end==========");
        }
        Log.e("albertThreadDebug", "all end==============================================");
    }
}
