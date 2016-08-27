package com.yingnanwang.statuschecker.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.yingnanwang.statuschecker.util.ActivityCollector;


public class BaseActivity extends AppCompatActivity {

    private static final String TAG = "BaseActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        Log.d(TAG, "onCreate: " + getClass().getSimpleName());

        ActivityCollector.addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);

//        Log.d(TAG, "onDestroy: "+ActivityCollector.getActivityCount());
    }
}
