package com.example.sporttracker;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    BottomNavigationView bottomNavigationView;
    private RunningDbHelper runningDbHelper;
    private HomeFragment homeFragment;
    private ProfileFragment profileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        runningDbHelper = new RunningDbHelper(this);

        Integer userId = ((MyApplication) this.getApplication()).getCurrentUser();

        User user = runningDbHelper.getUser(userId);

        if (this.hasWindowFocus()) {
            Toast.makeText(this, "Hello " + user.getFirstName(), Toast.LENGTH_SHORT).show();
        }


        homeFragment = new HomeFragment();
        profileFragment = new ProfileFragment();


        bottomNavigationView = findViewById(R.id.bottomNavigationView);


        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.activity);


    }




    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.activity:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, homeFragment).commit();
                return true;
            case R.id.profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, profileFragment).commit();
                return true;
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void startRunButton(View view) {
        homeFragment.startRun(view);
        if (view.getId() == R.id.startRun)
            bottomNavigationView.setVisibility(View.VISIBLE);
        else
            bottomNavigationView.setVisibility(View.GONE);
    }

    public void openProfile(View view){
        Intent i = new Intent(MainActivity.this, ProfileActivity.class);
        startActivity(i);
    }

}