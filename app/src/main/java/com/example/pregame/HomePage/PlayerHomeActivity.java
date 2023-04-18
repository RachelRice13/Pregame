package com.example.pregame.HomePage;

import androidx.annotation.NonNull;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.pregame.Fees.FeesFragment;
import com.example.pregame.InjuryReport.InjuryReportFragment;
import com.example.pregame.Message.MessageFragment;
import com.example.pregame.Model.Team;
import com.example.pregame.Profile.ProfileFragment;
import com.example.pregame.R;
import com.example.pregame.RecordShooting.RecordShootingFragment;
import com.example.pregame.Stats.TeamStatsFragment;
import com.example.pregame.Upload.UploadDocsFragment;
import com.example.pregame.Upload.UploadPictureFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

public class PlayerHomeActivity extends HomeActivityTemplate {
    public static Team currentTeam = new Team();
    public static String userType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_home);
        buildMenu();
        hideActionBar();
    }

    @Override
    public void buildMenu() {
        NavigationView navigationView = findViewById(R.id.navigation_view);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        setUpMenu(new PlayerHomeFragment(), R.id.nav_pb_home, bottomNavigationView);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                replaceBottomNavFragment(item, R.id.nav_pb_home, new PlayerHomeFragment());
                replaceBottomNavFragment(item, R.id.nav_pb_team_stats, new TeamStatsFragment());
                replaceBottomNavFragment(item, R.id.nav_pb_messages, new MessageFragment());
                replaceBottomNavFragment(item, R.id.nav_pb_record_shooting, new RecordShootingFragment());
                replaceBottomNavFragment(item, R.id.nav_pb_injury_report, new InjuryReportFragment());
                return false;
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                replaceHomeFragment(item, R.id.nav_pd_home, new PlayerHomeFragment(), R.id.nav_pb_home, bottomNavigationView);
                replaceFragment(item, R.id.nav_pd_profile, new ProfileFragment(), bottomNavigationView);
                replaceFragment(item, R.id.nav_pd_fees, new FeesFragment(), bottomNavigationView);
                replaceFragment(item, R.id.nav_pd_upload_image, new UploadPictureFragment(), bottomNavigationView);
                replaceFragment(item, R.id.nav_pd_upload_docs, new UploadDocsFragment(), bottomNavigationView);
                if (item.getItemId() == R.id.nav_pd_logout) {
                    logout(PlayerHomeActivity.this);
                    return true;
                }
                return false;
            }
        });
    }
}