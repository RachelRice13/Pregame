package com.example.pregame;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.pregame.Common.CommonActivity;

public class SplashActivity extends CommonActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent splashIntent = new Intent(SplashActivity.this, LandingPage.class);
                startActivity(splashIntent);
                finish();
            }
        }, 3000);

        hideActionBar();
    }
}