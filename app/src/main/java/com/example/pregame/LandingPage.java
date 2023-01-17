package com.example.pregame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.pregame.RegisterLogin.LoginActivity;
import com.example.pregame.RegisterLogin.RegisterActivity;

public class LandingPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);

        // Go to Register Page
        Button goToRegisterPage = findViewById(R.id.register_button);
        goToRegisterPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerIntent = new Intent(LandingPage.this, RegisterActivity.class);
                startActivity(registerIntent);
            }
        });

        // Go to Login Page
        Button goToLoginPage = findViewById(R.id.login_button);
        goToLoginPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loginIntent = new Intent(LandingPage.this, LoginActivity.class);
                startActivity(loginIntent);
            }
        });

        // Hides the action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }
}