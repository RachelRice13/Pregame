package com.example.pregame.HomePage;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.pregame.LandingPage;
import com.example.pregame.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public abstract class HomeActivityTemplate extends AppCompatActivity {
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private DrawerLayout drawerLayout;
    private ImageView toolbarIconEnd;

    public abstract void buildMenu();

    public void hideActionBar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    public boolean replaceBottomNavFragment(MenuItem item, int id, Fragment fragment) {
        if (item.getItemId() == id) {
            getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
            return true;
        }
        return false;
    }

    public boolean replaceHomeFragment(MenuItem item, int id, Fragment fragment, int bNId, BottomNavigationView bottomNavigationView) {
        if (item.getItemId() == id) {
            getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
            drawerLayout.close();
            toolbarIconEnd.setVisibility(View.GONE);
            bottomNavigationView.setVisibility(View.VISIBLE);
            bottomNavigationView.getMenu().findItem(bNId).setChecked(true);
            return true;
        }
        return false;
    }

    public boolean replaceFragment(MenuItem item, int id, Fragment fragment, BottomNavigationView bottomNavigationView) {
        if (item.getItemId() == id) {
            getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
            drawerLayout.close();
            toolbarIconEnd.setVisibility(View.GONE);
            bottomNavigationView.setVisibility(View.INVISIBLE);
            return true;
        }
        return false;
    }

    public void setUpMenu(Fragment fragment, int id, BottomNavigationView bottomNavigationView) {
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        bottomNavigationView.getMenu().findItem(id).setChecked(true);
        toolbarIconEnd = findViewById(R.id.toolbar_end_icon);
        toolbarIconEnd.setVisibility(View.GONE);
        drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.bringToFront();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

    }

    public void logout(Activity activity) {
        AlertDialog.Builder logoutAD = new AlertDialog.Builder(activity);

        logoutAD
                .setCancelable(false)
                .setTitle("Do you want to logout?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        firebaseAuth.signOut();
                        PlayerHomeActivity.userType = " ";
                        CoachHomeActivity.userType = " ";
                        Intent logoutIntent = new Intent(activity, LandingPage.class);
                        startActivity(logoutIntent);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                        drawerLayout.close();
                    }
                });

        AlertDialog alertDialog = logoutAD.create();
        alertDialog.show();
    }
}