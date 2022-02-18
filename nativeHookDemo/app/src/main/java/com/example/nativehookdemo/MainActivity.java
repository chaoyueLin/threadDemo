package com.example.nativehookdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.example.nativehookdemo.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {


    private ActivityMainBinding binding;
    private static final String TAG = "hook_tag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Example of a call to a native method
        TextView tv = binding.sampleText;
        Button hook = binding.hook;
        Button unhook = binding.unhook;
        Button threadB = binding.thread;
        hook.setOnClickListener((view) -> {

            ThreadHook.enableThreadHook();
        });

        unhook.setOnClickListener((view) -> {
            ThreadHook.unableThreadHook();
        });
        threadB.setOnClickListener((view) -> {
            new Thread(() -> {
                Log.e(TAG, "thread name:" + Thread.currentThread().getName());
                Log.e(TAG, "thread id:" + Thread.currentThread().getId());
                new Thread(() -> {
                    Log.e(TAG, "inner thread name:" + Thread.currentThread().getName());
                    Log.e(TAG, "inner thread id:" + Thread.currentThread().getId());

                }).start();
            }).start();
        });
        ThreadHook.init();
    }

}