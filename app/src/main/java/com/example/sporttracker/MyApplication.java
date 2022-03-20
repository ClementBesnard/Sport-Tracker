package com.example.sporttracker;

import android.app.Application;

public class MyApplication extends Application {

    private Integer currentUser;


    public Integer getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(Integer currentUser) {
        this.currentUser = currentUser;
    }
}
