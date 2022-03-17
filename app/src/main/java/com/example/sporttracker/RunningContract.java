package com.example.sporttracker;

import android.provider.BaseColumns;

public final class RunningContract {
    private RunningContract(){}

    public static class Profile implements BaseColumns {
        public static final String TABLE_NAME = "profile";
        public static final String COLUMN_NAME_FIRSTNAME = "firstname";
        public static final String COLUMN_NAME_LASTNAME = "lastname";
        public static final String COLUMN_NAME_HEIGHT = "height";
        public static final String COLUMN_NAME_WEIGHT = "weight";
        public static final String COLUMN_NAME_AGE = "age";

        public static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + Profile.TABLE_NAME + " (" +
                        Profile._ID + " INTEGER PRIMARY KEY," +
                        Profile.COLUMN_NAME_FIRSTNAME + " TEXT," +
                        Profile.COLUMN_NAME_LASTNAME + " TEXT," +
                        Profile.COLUMN_NAME_HEIGHT + " INTEGER," +
                        Profile.COLUMN_NAME_WEIGHT + " INTEGER," +
                        Profile.COLUMN_NAME_AGE + " INTEGER" +
                        ")";

        public static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + Profile.TABLE_NAME;
    }

    public static class Itinerary implements BaseColumns {
        public static final String TABLE_NAME = "itinerary";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_ONE_WAY = "oneWay";
        public static final String COLUMN_NAME_ROUND_TRIP = "roundTrip";
        public static final String COLUMN_NAME_START_LOCATION_ID = "startLocationId";
        public static final String COLUMN_NAME_END_LOCATION_ID = "endLocationId";


        public static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + Itinerary.TABLE_NAME + " (" +
                        Itinerary._ID + " INTEGER PRIMARY KEY," +
                        Itinerary.COLUMN_NAME_NAME + " TEXT," +
                        Itinerary.COLUMN_NAME_ONE_WAY + " BOOLEAN CHECK ( " + Itinerary.COLUMN_NAME_ONE_WAY + " IN (0,1)) ," +
                        Itinerary.COLUMN_NAME_ROUND_TRIP + " BOOLEAN CHECK ( " + Itinerary.COLUMN_NAME_ROUND_TRIP + " IN (0,1)) ," +
                        Itinerary.COLUMN_NAME_START_LOCATION_ID + " INTEGER," +
                        Itinerary.COLUMN_NAME_END_LOCATION_ID + " INTEGER," +
                        "CONSTRAINT FK_startLocation " +
                            "FOREIGN KEY (" + Itinerary.COLUMN_NAME_START_LOCATION_ID + ") REFERENCES " + Location.TABLE_NAME + " (" + Location._ID + ")," +
                        "CONSTRAINT FK_startLocation " +
                            "FOREIGN KEY (" + Itinerary.COLUMN_NAME_END_LOCATION_ID + ") REFERENCES " + Location.TABLE_NAME + " (" + Location._ID + ")" +
                        ")";

        public static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + Itinerary.TABLE_NAME;
    }

    public static class Location implements BaseColumns {
        public static final String TABLE_NAME = "location";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_LATITUDE = "latitude";
        public static final String COLUMN_NAME_LONGITUDE = "longitude";

        public static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + Location.TABLE_NAME + " (" +
                        Location._ID + " INTEGER PRIMARY KEY," +
                        Location.COLUMN_NAME_NAME + " TEXT," +
                        Location.COLUMN_NAME_LATITUDE + " REAL," +
                        Location.COLUMN_NAME_LONGITUDE + " REAL" +
                        ")";

        public static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + Location.TABLE_NAME;
    }

    public static class Activity implements BaseColumns {
        public static final String TABLE_NAME = "activity";
        public static final String COLUMN_NAME_DURATION = "duration"; // seconds
        public static final String COLUMN_NAME_DISTANCE = "distance"; // kilometers
        public static final String COLUMN_NAME_CALORIES = "calories";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_PROFILE_ID = "profileId";
        public static final String COLUMN_NAME_ITINERARY_ID = "itineraryId";



        public static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + Activity.TABLE_NAME + " (" +
                        Activity._ID + " INTEGER PRIMARY KEY," +
                        Activity.COLUMN_NAME_DURATION + " INT," +
                        Activity.COLUMN_NAME_DISTANCE + " REAL," +
                        Activity.COLUMN_NAME_CALORIES + " INT," +
                        Activity.COLUMN_NAME_DATE + " TEXT," +
                        Activity.COLUMN_NAME_PROFILE_ID + " INTEGER," +
                        Activity.COLUMN_NAME_ITINERARY_ID + " INTEGER," +
                        "CONSTRAINT FK_startLocation " +
                            "FOREIGN KEY (" + Activity.COLUMN_NAME_PROFILE_ID + ") REFERENCES " + Profile.TABLE_NAME + " (" + Profile._ID + ")," +
                        "CONSTRAINT FK_startLocation " +
                            "FOREIGN KEY (" + Activity.COLUMN_NAME_ITINERARY_ID + ") REFERENCES " + Itinerary.TABLE_NAME + " (" + Itinerary._ID + ")" +
                        ")";

        public static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + Activity.TABLE_NAME;
    }

    public static class ProfileItinerary implements BaseColumns {
        public static final String TABLE_NAME = "profileItinerary";
        public static final String COLUMN_NAME_PROFILE_ID = "profileId";
        public static final String COLUMN_NAME_ITINERARY_ID = "itineraryId";


        public static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + ProfileItinerary.TABLE_NAME + " (" +
                        ProfileItinerary.COLUMN_NAME_PROFILE_ID + " INTEGER," +
                        ProfileItinerary.COLUMN_NAME_ITINERARY_ID + " INTEGER," +
                        "CONSTRAINT profile_itinerary PRIMARY KEY (" + ProfileItinerary.COLUMN_NAME_PROFILE_ID + ", "
                                                                     + ProfileItinerary.COLUMN_NAME_ITINERARY_ID + ")," +
                        "CONSTRAINT FK_profile " +
                            "FOREIGN KEY (" + ProfileItinerary.COLUMN_NAME_PROFILE_ID + ") REFERENCES " + Profile.TABLE_NAME + " (" + Profile._ID + ")," +
                        "CONSTRAINT FK_itinerary " +
                            "FOREIGN KEY (" + ProfileItinerary.COLUMN_NAME_ITINERARY_ID + ") REFERENCES " + Itinerary.TABLE_NAME + " (" + Itinerary._ID + ")" +
                        ")";

        public static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + ProfileItinerary.TABLE_NAME;
    }

    public static class IntermediatePlace implements BaseColumns {
        public static final String TABLE_NAME = "intermediatePlace";
        public static final String COLUMN_NAME_ITINERARY_ID = "itineraryId";
        public static final String COLUMN_NAME_LOCATION_ID = "locationId";


        public static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + IntermediatePlace.TABLE_NAME + " (" +
                        IntermediatePlace.COLUMN_NAME_ITINERARY_ID + " INTEGER," +
                        IntermediatePlace.COLUMN_NAME_LOCATION_ID + " INTEGER," +
                        "CONSTRAINT profile_itinerary PRIMARY KEY (" + IntermediatePlace.COLUMN_NAME_ITINERARY_ID + ", "
                                                                     + IntermediatePlace.COLUMN_NAME_LOCATION_ID + ")," +
                        "CONSTRAINT FK_profile " +
                            "FOREIGN KEY (" + IntermediatePlace.COLUMN_NAME_ITINERARY_ID + ") REFERENCES " + Itinerary.TABLE_NAME + " (" + Itinerary._ID + ")," +
                        "CONSTRAINT FK_itinerary " +
                            "FOREIGN KEY (" + IntermediatePlace.COLUMN_NAME_LOCATION_ID + ") REFERENCES " + Location.TABLE_NAME + " (" + Location._ID + ")" +
                        ")";

        public static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + IntermediatePlace.TABLE_NAME;
    }
}
