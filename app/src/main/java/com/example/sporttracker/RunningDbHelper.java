package com.example.sporttracker;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class RunningDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Running.db";

    public RunningDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d("DB", "CREATE");
        sqLiteDatabase.execSQL(RunningContract.Profile.SQL_CREATE_ENTRIES);
        sqLiteDatabase.execSQL(RunningContract.Itinerary.SQL_CREATE_ENTRIES);
        sqLiteDatabase.execSQL(RunningContract.Location.SQL_CREATE_ENTRIES);
        sqLiteDatabase.execSQL(RunningContract.Activity.SQL_CREATE_ENTRIES);
        sqLiteDatabase.execSQL(RunningContract.ProfileItinerary.SQL_CREATE_ENTRIES);
        sqLiteDatabase.execSQL(RunningContract.IntermediatePlace.SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(RunningContract.Profile.SQL_DELETE_ENTRIES);
        sqLiteDatabase.execSQL(RunningContract.Itinerary.SQL_DELETE_ENTRIES);
        sqLiteDatabase.execSQL(RunningContract.Location.SQL_DELETE_ENTRIES);
        sqLiteDatabase.execSQL(RunningContract.Activity.SQL_DELETE_ENTRIES);
        sqLiteDatabase.execSQL(RunningContract.ProfileItinerary.SQL_DELETE_ENTRIES);
        sqLiteDatabase.execSQL(RunningContract.IntermediatePlace.SQL_DELETE_ENTRIES);
        onCreate(sqLiteDatabase);

    }

    public void createProfile(Profile profile) {
        // TEST
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
    }
}
