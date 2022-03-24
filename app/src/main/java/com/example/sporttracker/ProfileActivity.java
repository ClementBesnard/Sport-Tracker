package com.example.sporttracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    private RunningDbHelper runningDbHelper;
    private TextView prenom;
    private TextView nom;
    private TextView poids;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profile_avance);
        Integer userId = ((MyApplication) this.getApplication()).getCurrentUser();

        runningDbHelper = new RunningDbHelper(this);
        user = runningDbHelper.getUser(userId);
        String textePrenom = user.getFirstName();
        String texteNom = user.getLastName();
        Integer textePoids = user.getWeight();

        this.nom = findViewById(R.id.nom);
        this.nom.setText(texteNom);
        this.prenom = findViewById(R.id.prenom);
        this.prenom.setText(textePrenom);
        this.poids = findViewById(R.id.poidsInput);
        this.poids.setText(textePoids.toString());

    }

}
