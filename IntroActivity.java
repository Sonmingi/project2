package com.example.user.jsouptest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

/**
 * Created by user on 2018-05-25.
 */

public class IntroActivity extends RuntimePermission{
    private Handler handler;
    private static final int REQUEST_PERMISSION = 10;   // 런타임 퍼미션
    SharedprefereneceUtil sharedprefereneceUtil = new SharedprefereneceUtil(IntroActivity.this);
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Intent intent = new Intent(IntroActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        requestAppPermissions(new String[]{
                        android.Manifest.permission.INTERNET,
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.READ_PHONE_STATE,
                        android.Manifest.permission.CALL_PHONE,
                        android.Manifest.permission.READ_SMS
                },
                R.string.message, REQUEST_PERMISSION);
            init();
            handler.postDelayed(runnable, 3000);

    }

    @Override
    public void onPermissionsGranted(int requsetCode) {

    }

    public void init() {
        handler = new Handler();
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        handler.removeCallbacks(runnable);
    }
}

