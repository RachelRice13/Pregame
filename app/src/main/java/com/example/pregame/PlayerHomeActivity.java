package com.example.pregame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.pregame.Fees.FeesFragment;
import com.example.pregame.HomePage.PlayerHomeFragment;
import com.example.pregame.InjuryReport.InjuryReportFragment;
import com.example.pregame.Message.MessageFragment;
import com.example.pregame.Profile.ProfileFragment;
import com.example.pregame.RecordShooting.RecordShootingFragment;
import com.example.pregame.Stats.TeamStatsFragment;
import com.example.pregame.Upload.UploadFragment;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PlayerHomeActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private MaterialToolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;

    // Fragments
    private PlayerHomeFragment playerHomeFragment = new PlayerHomeFragment();
    private ProfileFragment profileFragment = new ProfileFragment();
    private FeesFragment feesFragment = new FeesFragment();
    private UploadFragment uploadFragment = new UploadFragment();

    private TeamStatsFragment teamStatsFragment = new TeamStatsFragment();
    private MessageFragment messageFragment = new MessageFragment();
    private RecordShootingFragment recordShootingFragment = new RecordShootingFragment();
    private InjuryReportFragment injuryReportFragment = new InjuryReportFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_home);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        bottomNavigationView = findViewById(R.id.player_bottom_navigation);
        buildMenu();

        // Hides the action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    public void buildMenu() {
        // Bottom Navigation
        getSupportFragmentManager().beginTransaction().replace(R.id.player_container, playerHomeFragment).commit();
        bottomNavigationView.getMenu().findItem(R.id.nav_pb_home).setChecked(true);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_pb_home:
                        getSupportFragmentManager().beginTransaction().replace(R.id.player_container, playerHomeFragment).commit();
                        return true;
                    case R.id.nav_pb_team_stats:
                        getSupportFragmentManager().beginTransaction().replace(R.id.player_container, teamStatsFragment).commit();
                        return true;
                    case R.id.nav_pb_messages:
                        getSupportFragmentManager().beginTransaction().replace(R.id.player_container, messageFragment).commit();
                        return true;
                    case R.id.nav_pb_record_shooting:
                        getSupportFragmentManager().beginTransaction().replace(R.id.player_container, recordShootingFragment).commit();
                        return true;
                    case R.id.nav_pb_injury_report:
                        getSupportFragmentManager().beginTransaction().replace(R.id.player_container, injuryReportFragment).commit();
                        return true;
                }
                return false;
            }
        });

        // Drawer Navigation
        toolbar = findViewById(R.id.player_top_app_bar);
        drawerLayout = findViewById(R.id.player_drawer_layout);
        navigationView = findViewById(R.id.player_navigation_view);
        drawerLayout.bringToFront();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_pd_home:
                        getSupportFragmentManager().beginTransaction().replace(R.id.player_container, playerHomeFragment).commit();
                        drawerLayout.close();
                        bottomNavigationView.setVisibility(View.VISIBLE);
                        bottomNavigationView.getMenu().findItem(R.id.nav_pb_home).setChecked(true);
                        return true;
                    case R.id.nav_pd_profile:
                        getSupportFragmentManager().beginTransaction().replace(R.id.player_container, profileFragment).commit();
                        drawerLayout.close();
                        bottomNavigationView.setVisibility(View.INVISIBLE);
                        return true;
                    case R.id.nav_pd_fees:
                        getSupportFragmentManager().beginTransaction().replace(R.id.player_container, feesFragment).commit();
                        drawerLayout.close();
                        bottomNavigationView.setVisibility(View.INVISIBLE);
                        return true;
                    case R.id.nav_pd_upload:
                        getSupportFragmentManager().beginTransaction().replace(R.id.player_container, uploadFragment).commit();
                        drawerLayout.close();
                        bottomNavigationView.setVisibility(View.INVISIBLE);
                        return true;
                    case R.id.nav_pd_logout:
                        logout();
                        return true;
                }
                return false;
            }
        });
    }

    public void logout() {
        AlertDialog.Builder logoutAD = new AlertDialog.Builder(PlayerHomeActivity.this);

        logoutAD
                .setCancelable(false)
                .setTitle("Do you want to logout?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(PlayerHomeActivity.this, currentUser.getEmail() + " has signed out", Toast.LENGTH_SHORT).show();
                        firebaseAuth.signOut();
                        Intent logoutIntent = new Intent(PlayerHomeActivity.this, LandingPage.class);
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