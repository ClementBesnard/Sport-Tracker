package com.example.sporttracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

import org.mindrot.jbcrypt.BCrypt;

import java.util.Objects;

public class SingUpActivity extends AppCompatActivity {

    private TextInputLayout firstName;
    private TextInputLayout lastName;
    private TextInputLayout password;
    private TextInputLayout height;
    private TextInputLayout weight;
    private TextInputLayout age;
    private RunningDbHelper runningDbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sing_up);

        runningDbHelper = new RunningDbHelper(this);

        firstName = findViewById(R.id.firstname);
        lastName = findViewById(R.id.lastname);
        password = findViewById(R.id.passwordSignUp);
        height = findViewById(R.id.height);
        weight = findViewById(R.id.weight);
        age = findViewById(R.id.age);
    }


    public void singUp(View view) {

        String generatedSecuredPasswordHash = BCrypt.hashpw(Objects.requireNonNull(password.getEditText()).getText().toString(), BCrypt.gensalt(12));
        User user = new User(Objects.requireNonNull(firstName.getEditText()).getText().toString(), Objects.requireNonNull(lastName.getEditText()).getText().toString(), generatedSecuredPasswordHash,
                Integer.parseInt(Objects.requireNonNull(height.getEditText()).getText().toString()), Integer.parseInt(Objects.requireNonNull(weight.getEditText()).getText().toString()), Integer.parseInt(Objects.requireNonNull(age.getEditText()).getText().toString()));

        Integer userId = Math.toIntExact(runningDbHelper.addNewUser(user));

        ((MyApplication)this.getApplication()).setCurrentUser(userId);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


    public void backToHome(View view) {
        Intent intent = new Intent(this, OpeningActivity.class);
        startActivity(intent);
    }
}