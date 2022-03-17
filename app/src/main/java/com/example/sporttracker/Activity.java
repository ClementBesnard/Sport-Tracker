package com.example.sporttracker;

import java.util.Date;

public class Activity {

    private Integer duration;
    private Double distance;
    private Integer calories;
    private Date date;
    private Integer profileId;
    private Integer itineraryId;

    public Activity(Integer duration, Double distance, Integer calories, Date date, Integer profileId, Integer itineraryId){
        this.duration = duration;
        this.distance = distance;
        this.calories = calories;
        this.date = date;
        this.profileId = profileId;
        this.itineraryId = itineraryId;
    }


    public Integer getDuration() {
        return duration;
    }

    public Double getDistance() {
        return distance;
    }

    public Integer getCalories() {
        return calories;
    }

    public Date getDate() {
        return date;
    }

    public Integer getProfileId() {
        return profileId;
    }

    public Integer getItineraryId() {
        return itineraryId;
    }
}