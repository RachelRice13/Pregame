package com.example.pregame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends AppCompatActivity {

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

        // Hides the action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }
}