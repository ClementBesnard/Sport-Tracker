package com.example.sporttracker;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    //Button profileBtn = (Button)getView().findViewById(R.id.plusDetails);

    private RunningDbHelper runningDbHelper;

    private List<Activity> activityList;
    private List<Activity> activityListWeek;


    private TextView distanceActivity;
    private TextView durationActivity;
    private TextView dateActivity;
    private TextView prenomActivity;
    private TextView distanceWeek;
    private TextView timeWeek;

    private User user;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters

        public static ProfileFragment newInstance(String param1, String param2) {
            ProfileFragment fragment = new ProfileFragment();
            Bundle args = new Bundle();
            args.putString(ARG_PARAM1, param1);
            args.putString(ARG_PARAM2, param2);
            fragment.setArguments(args);
            return fragment;
        }

        ImageView photoProfil;

        @SuppressLint("SetTextI18n")
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);


            if (getArguments() != null) {
                mParam1 = getArguments().getString(ARG_PARAM1);
                mParam2 = getArguments().getString(ARG_PARAM2);

            }


            this.runningDbHelper = new RunningDbHelper(getContext());

            Integer userId = ((MyApplication) requireActivity().getApplication()).getCurrentUser();

            user = runningDbHelper.getUser(userId);


            try {
                this.activityList = runningDbHelper.getUserActivity(userId);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            try {
                activityListWeek = runningDbHelper.getUserWeekActivity(userId);
            } catch (ParseException e) {
                e.printStackTrace();
            }








        }

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.distanceActivity = requireView().findViewById(R.id.distanceActivity);
        this.durationActivity = requireView().findViewById(R.id.durationActivity);
        this.dateActivity = requireView().findViewById(R.id.dateActivity);
        this.prenomActivity = requireView().findViewById(R.id.prenomActivity);
        this.distanceWeek = requireView().findViewById(R.id.distanceWeek);
        this.timeWeek = requireView().findViewById(R.id.timeWeek);

        LocalTime timeOfDay = LocalTime.ofSecondOfDay(activityList.get(0).getDuration());
        String time = timeOfDay.toString();

        Date today = new Date();

        Date date = activityList.get(0).getDate();

        long difference = today.getTime() - date.getTime();

        long difference_In_Seconds
                = (difference
                / 1000)
                % 60;

        long difference_In_Minutes
                = (difference
                / (1000 * 60))
                % 60;

        long difference_In_Hours
                = (difference
                / (1000 * 60 * 60))
                % 24;

        long difference_In_Years
                = (difference
                / (1000l * 60 * 60 * 24 * 365));

        long difference_In_Days
                = (difference
                / (1000 * 60 * 60 * 24))
                % 365;

        Log.d("Activity", String.valueOf(activityList.get(0)));
        Log.d("len", String.valueOf(activityList.size()));
        Log.d("null ?", String.valueOf(this.distanceActivity == null));
        this.distanceActivity.setText(HomeFragment.round(activityList.get(0).getDistance(), 2 )+ " km");
        this.durationActivity.setText(time);
        this.dateActivity.setText("il y a "+ difference_In_Minutes + " minutes");
        this.prenomActivity.setText(user.getFirstName());



        Double distanceWeek = activityListWeek.stream().mapToDouble(Activity::getDistance).sum();

        this.distanceWeek.setText(HomeFragment.round(distanceWeek, 2) + " km");

        Log.d("Week", String.valueOf(activityListWeek.size()));

        Integer timeWeekSecond = activityListWeek.stream().mapToInt(Activity::getDuration).sum();

        Log.d("time", String.valueOf(timeWeekSecond));

        LocalTime timeOfDay2 = LocalTime.ofSecondOfDay(timeWeekSecond);
        String timeWeek2 = timeOfDay2.toString();

        this.timeWeek.setText(timeWeek2);


    }

    @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            return inflater.inflate(R.layout.fragment_profile, container, false);
        }

}