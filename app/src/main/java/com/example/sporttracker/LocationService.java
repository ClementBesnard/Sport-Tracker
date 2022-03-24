package com.example.sporttracker;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.provider.SyncStateContract;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.text.MessageFormat;
import java.util.concurrent.Executor;

public class LocationService extends Service {

    public static final String TAG = LocationService.class.getSimpleName();
    private static final long LOCATION_REQUEST_INTERVAL = 10000;
    private static final float LOCATION_REQUEST_DISPLACEMENT = 1.0f;

    private GoogleApiClient mGoogleApiClient;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    LocationManager m_locationManager;

    @Override
    public void onCreate() {
        super.onCreate();

        //this.m_locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        Toast.makeText(getApplicationContext(), "Location Service starts", Toast.LENGTH_SHORT).show();

        showNotificationAndStartForegroundService();

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                //here you get the continues location updated based on the interval defined in
                //location request

                for (Location location : locationResult.getLocations()) {
                    // Update UI with location data
                    // ...
                    Toast.makeText(getApplicationContext(), "Update", Toast.LENGTH_SHORT).show();
                    sendMessageToActivity(location.getLatitude(), location.getLongitude());

                }

            }
        };

        createLocationRequest();


    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent.getAction().equals("start")) {
            // your start service code
            Toast.makeText(getApplicationContext(), "Service starts", Toast.LENGTH_SHORT).show();

            //final String channel_id = "Foreground Service Id";
            //NotificationChannel channel = new NotificationChannel(
            //        channel_id,
            //        channel_id,
            //        NotificationManager.IMPORTANCE_LOW
            //);

            //getSystemService(NotificationManager.class).createNotificationChannel(channel);
            //Notification.Builder notification = new Notification.Builder(this, channel_id)
            //        .setContentText("Service is running")
            //        .setContentTitle("Service enabled")
            //        .setSmallIcon(R.drawable.ic_launcher_background);





            // Notification ID cannot be 0.
            //startForeground(101, notification.build());



            //  Here I offer two options: either you are using satellites or the Wi-Fi services to get user's location
            //  this.m_locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3000, 0, this); //  User's location is retrieve every 3 seconds
            //this.m_locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

            //this.m_locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);

            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

            mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());


        }
        else if (intent.getAction().equals("stop")) {
            //your end servce code
            //this.m_locationManager.removeUpdates(this);
            removeLocationUpdate();
            stopForeground(true);
            stopSelfResult(startId);
        }






        return START_STICKY;
    }


    /**
     * Method used for creating location request
     * After successfully connection of the GoogleClient ,
     * This method used for to request continues location
     */
    private void createLocationRequest() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(LOCATION_REQUEST_INTERVAL);
        mLocationRequest.setSmallestDisplacement(LOCATION_REQUEST_DISPLACEMENT);

    }


    private void removeLocationUpdate() {
        mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
    }

    /**
     * This Method shows notification for ForegroundService
     * Start Foreground Service and Show Notification to user for android all version
     */
    private void showNotificationAndStartForegroundService() {

        final String CHANNEL_ID = BuildConfig.APPLICATION_ID.concat("_notification_id");
        final String CHANNEL_NAME = BuildConfig.APPLICATION_ID.concat("_notification_name");
        final int NOTIFICATION_ID = 100;

        NotificationCompat.Builder builder;
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_NONE;
            assert notificationManager != null;
            NotificationChannel mChannel = notificationManager.getNotificationChannel(CHANNEL_ID);
            if (mChannel == null) {
                mChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance);
                notificationManager.createNotificationChannel(mChannel);
            }
            builder = new NotificationCompat.Builder(this, CHANNEL_ID);
            builder.setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(getString(R.string.app_name));
            startForeground(NOTIFICATION_ID, builder.build());
        } else {
            builder = new NotificationCompat.Builder(this, CHANNEL_ID);
            builder.setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(getString(R.string.app_name));
            startForeground(NOTIFICATION_ID, builder.build());
        }
    }




    private void sendMessageToActivity(double lat, double lon) {
        Intent intent = new Intent("intentKey");
        // You can also include some extra data.
        intent.putExtra("lat", lat);
        intent.putExtra("lon", lon);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }


}

