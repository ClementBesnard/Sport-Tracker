package com.example.sporttracker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;

import org.mindrot.jbcrypt.BCrypt;

import java.util.Objects;

public class LogInActivity extends AppCompatActivity {

    private AutoCompleteTextView spinner;
    private RunningDbHelper runningDbHelper;
    private User selectedUser;
    private TextInputLayout password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        runningDbHelper = new RunningDbHelper(this);

        this.password = findViewById(R.id.password);
        TextInputLayout textInputLayout = ((TextInputLayout)findViewById(R.id.spinner));

        spinner = (AutoCompleteTextView) textInputLayout.getEditText();


        ArrayAdapter<User> adapter = new ArrayAdapter<User>(this,
                R.layout.list_item, runningDbHelper.getUsers());

        adapter.setDropDownViewResource(R.layout.list_item);

        this.spinner.setAdapter(adapter);

        // When user select a List-Item.
        this.spinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Adapter adapter = adapterView.getAdapter();
                selectedUser = (User) adapter.getItem(i);
            }


        });



    }

    public void LogIn(View view) {

        if (BCrypt.checkpw(Objects.requireNonNull(password.getEditText()).getText().toString(), selectedUser.getPassword())){
            ((MyApplication)this.getApplication()).setCurrentUser(selectedUser.getId());
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }

    }

    public void backToHome(View view) {
        Intent intent = new Intent(this, OpeningActivity.class);
        startActivity(intent);
    }
}