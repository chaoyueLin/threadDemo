package com.example.nativehookdemo;

import com.bytedance.android.bytehook.ByteHook;

public class ThreadHook {
    static {
        System.loadLibrary("native-lib");
    }

    private static boolean sHasHook = false;



    public static synchronized void init() {
        ByteHook.init();
    }

    public static String getStack() {
        String d = null;
        try {
            d = stackTraceToString(new Throwable().getStackTrace());
        } catch (Exception e) {

        }
        return d;
    }

    private static String stackTraceToString(final StackTraceElement[] arr) {
        if (arr == null) {
            return "";
        }

        StringBuffer sb = new StringBuffer();

        for (StackTraceElement stackTraceElement : arr) {
            String className = stackTraceElement.getClassName();
            // remove unused stacks
            if (className.contains("java.lang.Thread")) {
                continue;
            }

            sb.append(stackTraceElement).append('\n');
        }
        return sb.toString();
    }

    public static void enableThreadHook() {
        if (sHasHook) {
            return;
        }
        sHasHook = true;
        enableThreadHookNative();
    }
    public static void unableThreadHook(){
        if (!sHasHook) {
            return;
        }
        sHasHook = false;
        nativeUnhook();
    }


    private static native int enableThreadHookNative();
    private static native int nativeUnhook();
}
