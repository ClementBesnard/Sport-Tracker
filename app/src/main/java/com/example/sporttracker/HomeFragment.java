package com.example.sporttracker;


import static android.content.ContentValues.TAG;

import static java.lang.Math.round;

import android.content.Context;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Parcelable;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.Task;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements OnMapReadyCallback {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private final LatLng defaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int DEFAULT_ZOOM = 15;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private GoogleMap map;
    private double latitude;
    private double longitude;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private boolean locationPermissionGranted;
    private Location lastKnownLocation;
    private List<LatLng> locationList;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    private Parcelable cameraPosition;
    private GoogleApiClient mGoogleApiClient;
    private LocationManager locationManager;
    private Chronometer time;
    private double distance;
    private int locationChangeCounter;
    private TextView distanceView;
    private TextView calories;
    private Button RunButton;
    private Boolean tracking;
    private TextView rhythmView;

    private RunningDbHelper runningDbHelper;
    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        this.locationList = new ArrayList<>();
        tracking = Boolean.FALSE;
        distance = 0;
        locationChangeCounter = 0;

        this.runningDbHelper = new RunningDbHelper(getContext());
        SQLiteDatabase database = runningDbHelper.getReadableDatabase();
        // TEST
        runningDbHelper.createProfile(new Profile());


        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        // Retrieve location and camera position from saved instance state.

        locationManager=(LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER,
                0,
                1, locationListenerGPS);




    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_fragment);
        assert mapFragment != null;
        mapFragment.getMapAsync((OnMapReadyCallback) this);

        time = requireView().findViewById(R.id.time);
        distanceView = requireView().findViewById(R.id.distance);
        calories = requireView().findViewById(R.id.calories);
        rhythmView = requireView().findViewById(R.id.rhythm);
        RunButton = requireView().findViewById(R.id.startRun);

        time.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {

            @Override

            public void onChronometerTick(Chronometer chronometer) {
                long elapsedMillis = SystemClock.elapsedRealtime() - time.getBase();
                Log.d("P1", String.valueOf((elapsedMillis / 60000d)));
                Log.d("P2", String.valueOf(distance));

                Log.d("rythme1", String.valueOf((elapsedMillis / 60000d) / distance));
                // Minutes divided by kilometer
                rhythmView.setText(MessageFormat.format("{0}", (elapsedMillis / 60000d) / distance));
            }

        });
    }

    public void startRun(View view) {
        long elapsedRealtime = SystemClock.elapsedRealtime();
        switch (view.getId()){
            case R.id.startRun:
                time.setBase(elapsedRealtime);
                time.start();
                tracking = Boolean.TRUE;
                RunButton.setText(R.string.stop);
                RunButton.setId(R.id.stopRun);
                break;
            case R.id.stopRun:
                time.stop();
                time.setBase(elapsedRealtime);
                tracking = Boolean.FALSE;
                distance = 0;
                locationChangeCounter = 0;
                distanceView.setText(MessageFormat.format("{0} km", Integer.toString((int) distance)));
                rhythmView.setText("00:00");
                RunButton.setText(R.string.start);
                RunButton.setId(R.id.startRun);
                break;
        }

    }

    private static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }


    LocationListener locationListenerGPS=new LocationListener() {
        @Override
        public void onLocationChanged(android.location.Location location) {
            if (tracking){
                getDeviceLocation();
                distanceView.setText(MessageFormat.format("{0} km", round(distance,3)));
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        this.map = map;

        // Prompt the user for permission.
        getLocationPermission();

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();

    }


    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(requireActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        locationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    private void updateLocationUI() {

        if (map == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                map.setMyLocationEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(true);

            } else {
                map.setMyLocationEnabled(false);
                map.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        // Set the map's camera position to the current location of the device.
                        lastKnownLocation = task.getResult();
                        if (lastKnownLocation != null) {
                            //map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                            //        new LatLng(lastKnownLocation.getLatitude(),
                            //                lastKnownLocation.getLongitude()), DEFAULT_ZOOM));


                            locationList.add(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()));
                            if (locationList.size() > 1){
                                Log.d("LAT1", String.valueOf(this.locationList.get(locationList.size() - 1).latitude));
                                Log.d("LON1", String.valueOf(this.locationList.get(locationList.size() - 1).longitude));

                                Log.d("LAT2", String.valueOf(this.locationList.get(locationList.size() - 2).latitude));
                                Log.d("LON2", String.valueOf(this.locationList.get(locationList.size() - 2).longitude));
                                map.addPolyline(new PolylineOptions()
                                        .add(
                                                this.locationList.get(locationList.size() - 1),
                                                this.locationList.get(locationList.size() - 2)));
                                Location P1 = new Location("P1");
                                P1.setLatitude(this.locationList.get(locationList.size() - 1).latitude);
                                P1.setLongitude(this.locationList.get(locationList.size() - 1).longitude);

                                Location P2 = new Location("P2");
                                P2.setLatitude(this.locationList.get(locationList.size() - 2).latitude);
                                P2.setLongitude(this.locationList.get(locationList.size() - 2).longitude);

                                double meters = P1.distanceTo(P2);
                                distance+= meters / 1000d;
                            }

                        }


                    } else {
                        Log.d(TAG, "Current location is null. Using defaults.");
                        Log.e(TAG, "Exception: %s", task.getException());
                        map.moveCamera(CameraUpdateFactory
                                .newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                        map.getUiSettings().setMyLocationButtonEnabled(false);
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (map != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, map.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, lastKnownLocation);
        }
        super.onSaveInstanceState(outState);
    }

}