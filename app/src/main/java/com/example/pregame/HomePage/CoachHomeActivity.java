package com.example.pregame.HomePage;

import androidx.annotation.NonNull;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.pregame.CoachesBoard.CoachesBoardFragment;
import com.example.pregame.Message.MessageFragment;
import com.example.pregame.Model.Team;
import com.example.pregame.Profile.ProfileFragment;
import com.example.pregame.R;
import com.example.pregame.Stats.GameStatsFragment;
import com.example.pregame.Stats.TeamStatsFragment;
import com.example.pregame.Team.TeamFragment;
import com.example.pregame.TrainingMatch.TrainingMatchFragment;
import com.example.pregame.Upload.UploadDocsFragment;
import com.example.pregame.Upload.UploadPictureFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

public class CoachHomeActivity extends HomeActivityTemplate {
    public static Team currentTeam = new Team();
    public static String userType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coach_home);
        buildMenu();
        hideActionBar();
    }

    @Override
    public void buildMenu() {
        NavigationView navigationView = findViewById(R.id.navigation_view);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        setUpMenu(new CoachHomeFragment(), R.id.nav_cb_home, bottomNavigationView);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                replaceBottomNavFragment(item, R.id.nav_cb_home, new CoachHomeFragment());
                replaceBottomNavFragment(item, R.id.nav_cb_team_stats, new TeamStatsFragment());
                replaceBottomNavFragment(item, R.id.nav_cb_messages, new MessageFragment());
                replaceBottomNavFragment(item, R.id.nav_cb_tactics_board, new CoachesBoardFragment());
                replaceBottomNavFragment(item, R.id.nav_cb_game_stats, new GameStatsFragment());
                return false;
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                replaceHomeFragment(item, R.id.nav_cd_home, new CoachHomeFragment(), R.id.nav_cb_home, bottomNavigationView);
                replaceFragment(item, R.id.nav_cd_profile, new ProfileFragment(), bottomNavigationView);
                replaceFragment(item, R.id.nav_cd_upload_image, new UploadPictureFragment(), bottomNavigationView);
                replaceFragment(item, R.id.nav_cd_upload_docs, new UploadDocsFragment(), bottomNavigationView);
                replaceFragment(item, R.id.nav_cd_trainings_matches, new TrainingMatchFragment(), bottomNavigationView);
                replaceFragment(item, R.id.nav_cd_team_list, new TeamFragment(), bottomNavigationView);
                if (item.getItemId() == R.id.nav_cd_logout) {
                    logout(CoachHomeActivity.this);
                    return true;
                }
                return false;
            }
        });
    }
}