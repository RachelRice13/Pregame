package com.example.pregame.RegisterLogin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.pregame.R;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Hides the action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }
}