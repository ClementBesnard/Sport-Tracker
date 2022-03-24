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
    private TextView taille;
    private TextView age;
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
        Integer texteTaille = user.getHeight();
        Integer texteAge = user.getAge();

        this.nom = findViewById(R.id.nom);
        this.nom.setText("Nom : " + texteNom);
        this.prenom = findViewById(R.id.prenom);
        this.prenom.setText("Prenom : " + textePrenom);
        this.age = findViewById(R.id.age);
        this.age.setText("Age : " + texteAge.toString());
        this.poids = findViewById(R.id.poidsInput);
        this.poids.setText(textePoids.toString() + " Kg");
        this.taille = findViewById(R.id.tailleInput);
        this.taille.setText(texteTaille.toString() + " Cm");

    }

    public void retour(View view){
        Intent i = new Intent(ProfileActivity.this, MainActivity.class);
        startActivity(i);
    }

}
