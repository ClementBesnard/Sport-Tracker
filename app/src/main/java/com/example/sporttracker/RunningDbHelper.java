package com.example.sporttracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class RunningDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 2;
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

    public void addNewActivity(Activity activity){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        ContentValues values = new ContentValues();
        values.put(RunningContract.Activity.COLUMN_NAME_DURATION, activity.getDuration());
        values.put(RunningContract.Activity.COLUMN_NAME_DISTANCE, activity.getDistance());
        values.put(RunningContract.Activity.COLUMN_NAME_CALORIES, activity.getCalories());
        values.put(RunningContract.Activity.COLUMN_NAME_DATE, dateFormat.format(activity.getDate()));
        values.put(RunningContract.Activity.COLUMN_NAME_PROFILE_ID, activity.getProfileId());
        values.put(RunningContract.Activity.COLUMN_NAME_ITINERARY_ID, activity.getItineraryId());




        long newRowId = sqLiteDatabase.insert(RunningContract.Activity.TABLE_NAME, null, values);


    }

    public long addNewUser(User user){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(RunningContract.Profile.COLUMN_NAME_FIRSTNAME, user.getFirstName());
        values.put(RunningContract.Profile.COLUMN_NAME_LASTNAME, user.getLastName());
        values.put(RunningContract.Profile.COLUMN_NAME_PASSWORD, user.getPassword());
        values.put(RunningContract.Profile.COLUMN_NAME_HEIGHT, user.getHeight());
        values.put(RunningContract.Profile.COLUMN_NAME_WEIGHT, user.getWeight());
        values.put(RunningContract.Profile.COLUMN_NAME_AGE, user.getAge());


        long newRowId = sqLiteDatabase.insert(RunningContract.Profile.TABLE_NAME, null, values);

        return newRowId;
    }

    public User getUser(Integer id){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        String[] projection = {
                BaseColumns._ID,
                RunningContract.Profile.COLUMN_NAME_FIRSTNAME,
                RunningContract.Profile.COLUMN_NAME_LASTNAME,
                RunningContract.Profile.COLUMN_NAME_PASSWORD,
                RunningContract.Profile.COLUMN_NAME_HEIGHT,
                RunningContract.Profile.COLUMN_NAME_WEIGHT,
                RunningContract.Profile.COLUMN_NAME_AGE,
        };

        String selection = RunningContract.Profile._ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};

        String sortOrder =
                RunningContract.Profile.COLUMN_NAME_FIRSTNAME + " DESC";


        Cursor cursor = sqLiteDatabase.query(
            RunningContract.Profile.TABLE_NAME,   // The table to query
            projection,             // The array of columns to return (pass null to get all)
            selection,              // The columns for the WHERE clause
            selectionArgs,          // The values for the WHERE clause
            null,                   // don't group the rows
            null,                   // don't filter by row groups
            sortOrder               // The sort order
            );


        cursor.moveToFirst();
        User user = new User(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getInt(4), cursor.getInt(5), cursor.getInt(6));

        return user;


    }

    public List<User> getUsers(){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();


        Cursor cursor = sqLiteDatabase.query(
                    RunningContract.Profile.TABLE_NAME, null, null, null, null, null, null);

        List<User> users = new ArrayList<>();

        while (cursor.moveToNext()){
            User user = new User(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getInt(4), cursor.getInt(5), cursor.getInt(6));
            users.add(user);
        }

        cursor.close();

        return users;

    }
}
