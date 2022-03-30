package com.example.sporttracker;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;

public class ProfileActivity extends AppCompatActivity {

    private RunningDbHelper runningDbHelper;
    private TextView prenom;
    private TextView nom;
    private TextView poids;
    private TextView taille;
    private TextView age;
    private TextView locationP;
    private User user;
    private FusedLocationProviderClient fusedLocationClient;
    private Geocoder geocoder;
    private List<Address> addresses;


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

        this.locationP = findViewById(R.id.locationInput);

        this.nom = findViewById(R.id.nomInput);
        this.nom.setText(texteNom);
        this.prenom = findViewById(R.id.prenomInput);
        this.prenom.setText(textePrenom);
        this.age = findViewById(R.id.age);
        this.age.setText("Age : " + texteAge.toString());
        this.poids = findViewById(R.id.poidsInput);
        this.poids.setText(textePoids.toString());
        this.poids.setText(textePoids.toString() + " Kg");
        this.taille = findViewById(R.id.tailleInput);
        this.taille.setText(texteTaille.toString() + " Cm");

        geocoder = new Geocoder(this, Locale.getDefault());

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener( this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            try {
                                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                String address = addresses.get(0).getAddressLine(0);
                                String city = addresses.get(0).getLocality();
                                String state = addresses.get(0).getAdminArea();
                                String country = addresses.get(0).getCountryName();
                                String postalCode = addresses.get(0).getPostalCode();
                                String knownName = addresses.get(0).getFeatureName();

                                locationP.setText(address);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

    }

    public void retour(View view){
        Intent i = new Intent(ProfileActivity.this, MainActivity.class);
        startActivity(i);
    }

}
