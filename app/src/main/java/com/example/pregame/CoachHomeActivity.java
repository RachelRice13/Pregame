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

import com.example.pregame.CoachesBoard.CoachesBoardFragment;
import com.example.pregame.HomePage.CoachHomeFragment;
import com.example.pregame.Message.MessageFragment;
import com.example.pregame.Profile.ProfileFragment;
import com.example.pregame.Stats.GameStatsFragment;
import com.example.pregame.Stats.TeamStatsFragment;
import com.example.pregame.Team.TeamFragment;
import com.example.pregame.TrainingMatch.TrainingMatchFragment;
import com.example.pregame.Upload.UploadFragment;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CoachHomeActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private MaterialToolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;

    // Fragments
    private CoachHomeFragment coachHomeFragment = new CoachHomeFragment();
    private ProfileFragment profileFragment = new ProfileFragment();
    private UploadFragment uploadFragment = new UploadFragment();
    private TrainingMatchFragment trainingMatchFragment = new TrainingMatchFragment();
    private TeamFragment teamFragment = new TeamFragment();

    private TeamStatsFragment teamStatsFragment = new TeamStatsFragment();
    private MessageFragment messageFragment = new MessageFragment();
    private CoachesBoardFragment coachesBoardFragment = new CoachesBoardFragment();
    private GameStatsFragment gameStatsFragment = new GameStatsFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coach_home);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        bottomNavigationView = findViewById(R.id.coach_bottom_navigation);
        buildMenu();

        // Hides the action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    public void buildMenu() {
        // Bottom Navigation
        getSupportFragmentManager().beginTransaction().replace(R.id.coach_container, coachHomeFragment).commit();
        bottomNavigationView.getMenu().findItem(R.id.nav_cb_home).setChecked(true);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_cb_home:
                        getSupportFragmentManager().beginTransaction().replace(R.id.coach_container, coachHomeFragment).commit();
                        return true;
                    case R.id.nav_cb_team_stats:
                        getSupportFragmentManager().beginTransaction().replace(R.id.coach_container, teamStatsFragment).commit();
                        return true;
                    case R.id.nav_cb_messages:
                        getSupportFragmentManager().beginTransaction().replace(R.id.coach_container, messageFragment).commit();
                        return true;
                    case R.id.nav_cb_tactics_board:
                        getSupportFragmentManager().beginTransaction().replace(R.id.coach_container, coachesBoardFragment).commit();
                        return true;
                    case R.id.nav_cb_game_stats:
                        getSupportFragmentManager().beginTransaction().replace(R.id.coach_container, gameStatsFragment).commit();
                        return true;
                }
                return false;
            }
        });

        // Drawer Navigation
        toolbar = findViewById(R.id.coach_top_app_bar);
        drawerLayout = findViewById(R.id.coach_drawer_layout);
        navigationView = findViewById(R.id.coach_navigation_view);
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
                    case R.id.nav_cd_home:
                        getSupportFragmentManager().beginTransaction().replace(R.id.coach_container, coachHomeFragment).commit();
                        drawerLayout.close();
                        bottomNavigationView.setVisibility(View.VISIBLE);
                        bottomNavigationView.getMenu().findItem(R.id.nav_cb_home).setChecked(true);
                        return true;
                    case R.id.nav_cd_profile:
                        getSupportFragmentManager().beginTransaction().replace(R.id.coach_container, profileFragment).commit();
                        drawerLayout.close();
                        bottomNavigationView.setVisibility(View.INVISIBLE);
                        return true;
                    case R.id.nav_cd_upload:
                        getSupportFragmentManager().beginTransaction().replace(R.id.coach_container, uploadFragment).commit();
                        drawerLayout.close();
                        bottomNavigationView.setVisibility(View.INVISIBLE);
                        return true;
                    case R.id.nav_cd_trainings_matches:
                        getSupportFragmentManager().beginTransaction().replace(R.id.coach_container, trainingMatchFragment).commit();
                        drawerLayout.close();
                        bottomNavigationView.setVisibility(View.INVISIBLE);
                        return true;
                    case R.id.nav_cd_team_list:
                        getSupportFragmentManager().beginTransaction().replace(R.id.coach_container, teamFragment).commit();
                        drawerLayout.close();
                        bottomNavigationView.setVisibility(View.INVISIBLE);
                        return true;
                    case R.id.nav_cd_logout:
                        logout();
                        return true;
                }
                return false;
            }
        });
    }

    public void logout() {
        AlertDialog.Builder logoutAD = new AlertDialog.Builder(CoachHomeActivity.this);

        logoutAD
                .setCancelable(false)
                .setTitle("Do you want to logout?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(CoachHomeActivity.this, currentUser.getEmail() + " has signed out", Toast.LENGTH_SHORT).show();
                        firebaseAuth.signOut();
                        Intent logoutIntent = new Intent(CoachHomeActivity.this, LandingPage.class);
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