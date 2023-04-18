package com.example.pregame.Common;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class CommonActivity extends AppCompatActivity {

    public void hideActionBar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    public void goToPage(int id, Context comeFrom, Class goToClass) {
        Button goToButton = findViewById(id);
        goToButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(comeFrom, goToClass);
                startActivity(intent);
            }
        });
    }

}