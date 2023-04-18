package com.example.pregame;

import android.os.Bundle;

import com.example.pregame.Common.CommonActivity;
import com.example.pregame.RegisterLogin.LoginActivity;
import com.example.pregame.RegisterLogin.RegisterActivity;

public class LandingPage extends CommonActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);

        goToPage(R.id.register_button, LandingPage.this, RegisterActivity.class);
        goToPage(R.id.login_button, LandingPage.this, LoginActivity.class);
        hideActionBar();
    }
}