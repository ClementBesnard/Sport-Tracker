package com.example.sporttracker;


import static android.content.ContentValues.TAG;

import static java.lang.Math.round;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.content.Context;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.os.CountDownTimer;
import android.os.Parcelable;
import android.os.SystemClock;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.Task;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

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
    private static final int PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 2;
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

    private List<Polyline> polylines;

    private User currentUser;

    private Integer userId;

    private Context context;
    private ConstraintLayout bottomLayoutHome;
    private ConstraintLayout bottomLayoutHomeCopy;
    private SupportMapFragment mapFragment;
    private CardView longPressButton;
    private ProgressBar bar;
    private View stopButton;

    private Marker positionMarker;
    private boolean locationCoarsePermissionGranted;
    private ImageButton locationButton;

    private LatLng currentLoc;

    private Double cal;


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

    @RequiresApi(api = Build.VERSION_CODES.O)
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

        userId = ((MyApplication) requireActivity().getApplication()).getCurrentUser();
        currentUser = runningDbHelper.getUser(userId);


        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        // Retrieve location and camera position from saved instance state.

        context = getContext();
        LocalBroadcastManager.getInstance(requireActivity()).registerReceiver(mMessageReceiver, new IntentFilter("intentKey"));

        polylines = new ArrayList<>();

        tracking = false;
        Intent intent = new Intent(requireActivity(), LocationService.class);
        intent.setAction("start");

        context.startForegroundService(intent);






    }
        //locationManager=(LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
        /*locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER,
                0,
                1, locationListenerGPS);*/

    private final BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            double lat = intent.getDoubleExtra("lat", 0d);
            double lon = intent.getDoubleExtra("lon", 0d);
            LatLng latLng = new LatLng(lat,lon);
            currentLoc = latLng;
            Log.d("UPDATE", String.valueOf(lat));

            if (getActivity() != null)
                if (positionMarker == null) {

                    Bitmap imageBitmap = BitmapFactory.decodeResource(requireActivity().getResources(),requireActivity().getResources().getIdentifier("marker", "drawable", requireActivity().getPackageName()));
                    Bitmap marker = Bitmap.createScaledBitmap(imageBitmap, 60, 60, false);

                    MarkerOptions positionMarkerOptions = new MarkerOptions()
                            .position(latLng)
                            .anchor(0.5f, 0.5f)
                            .icon(BitmapDescriptorFactory.fromBitmap(marker));
                    positionMarker = map.addMarker(positionMarkerOptions);
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,DEFAULT_ZOOM));
                }
                else{
                    positionMarker.setPosition(latLng);
                    //map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));

                }

            if (!tracking){
                if (locationList.size() > 0)
                    locationList.remove(0);
                locationList.add(latLng);
            }



            if (tracking){
                locationList.add(latLng);
                if (locationList.size() > 1){
                    Log.d("LAT1", String.valueOf(locationList.get(locationList.size() - 1).latitude));
                    Log.d("LON1", String.valueOf(locationList.get(locationList.size() - 1).longitude));

                    Log.d("LAT2", String.valueOf(locationList.get(locationList.size() - 2).latitude));
                    Log.d("LON2", String.valueOf(locationList.get(locationList.size() - 2).longitude));

                    Polyline polyline = map.addPolyline(new PolylineOptions().color(requireActivity().getResources().getColor(R.color.secondary_color))
                            .add(
                                    locationList.get(locationList.size() - 1),
                                    locationList.get(locationList.size() - 2)));

                    polylines.add(polyline);
                    Location P1 = new Location("P1");
                    P1.setLatitude(locationList.get(locationList.size() - 1).latitude);
                    P1.setLongitude(locationList.get(locationList.size() - 1).longitude);

                    Location P2 = new Location("P2");
                    P2.setLatitude(locationList.get(locationList.size() - 2).latitude);
                    P2.setLongitude(locationList.get(locationList.size() - 2).longitude);

                    double meters = P1.distanceTo(P2);
                    distance+= meters / 1000d;
                    distanceView.setText(MessageFormat.format("{0} km", round(distance,3)));

                    cal = currentUser.getWeight() * distance;
                    calories.setText(MessageFormat.format("{0}", cal));

                }
            }






        }
    };



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_fragment);

        assert mapFragment != null;
        mapFragment.getMapAsync((OnMapReadyCallback) this);


        time = requireView().findViewById(R.id.time);
        distanceView = requireView().findViewById(R.id.distance);
        calories = requireView().findViewById(R.id.calories);
        rhythmView = requireView().findViewById(R.id.rhythm);
        RunButton = requireView().findViewById(R.id.startRun);

        locationButton = requireView().findViewById(R.id.locationButton);

        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLoc,DEFAULT_ZOOM));

            }
        });


        longPressButton = requireView().findViewById(R.id.btnProgress);

        bar = longPressButton.findViewById(R.id.progressBar);

        Vibrator v = (Vibrator) requireActivity().getSystemService(Context.VIBRATOR_SERVICE);

        longPressButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                new CountDownTimer(2000, 1){
                    @Override
                    public void onTick(long l) {
                        if (!longPressButton.isPressed()){
                            bar.setProgress(0);
                            v.cancel();
                            //Cancelar a contagem | Cancel the count
                            cancel();
                        }else{
                            bar.setProgress(1 + bar.getProgress());
                            v.vibrate(l);
                        }
                    }

                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onFinish() {
                        tracking = false;
                        getActivity().findViewById(R.id.bottomNavigationView).setVisibility(View.VISIBLE);

                        long elapsedRealtime = SystemClock.elapsedRealtime();

                        bar.setProgress(0);
                        time.stop();


                        Date date = new Date();
                        Integer duration = Math.toIntExact(TimeUnit.MILLISECONDS.toSeconds(SystemClock.elapsedRealtime() - time.getBase()));
                        Activity activity = new Activity(duration, distance, cal, date, userId, 0);
                        runningDbHelper.addNewActivity(activity);


                        time.setBase(elapsedRealtime);
                        distance = 0;
                        distanceView.setText(MessageFormat.format("{0} km", Integer.toString((int) distance)));
                        rhythmView.setText("00:00");
                        calories.setText("0");
                        RunButton.setText(R.string.start);
                        RunButton.setId(R.id.startRun);
                        RunButton.setVisibility(View.VISIBLE);
                        longPressButton.setVisibility(View.INVISIBLE);
                        for (Polyline polyline : polylines ) {
                            polyline.remove();
                        }

                        locationList.clear();
                    }
                }.start();

                return true;
            };
        });





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

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void startRun(View view) {
        long elapsedRealtime = SystemClock.elapsedRealtime();

                tracking = true;
                view.setVisibility(View.INVISIBLE);
                longPressButton.setVisibility(View.VISIBLE);
                time.setBase(elapsedRealtime);
                time.start();
                RunButton.setText(R.string.stop);
                RunButton.setId(R.id.stopRun);
        }


    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }


    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        this.map = map;



        this.map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);


        // Prompt the user for permission.
        getLocationPermission();

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();


    }


    /**
     * Prompts the user for permission to use the device location.
     */
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

    /**
     * Handles the result of the request for location permissions.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
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
                map.setMyLocationEnabled(false);
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

                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(lastKnownLocation.getLatitude(),
                                            lastKnownLocation.getLongitude()), DEFAULT_ZOOM));




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


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent intentStop = new Intent(requireActivity(), LocationService.class);
        intentStop.setAction("stop");
        context.startForegroundService(intentStop);
    }

}