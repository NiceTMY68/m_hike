package com.example.m_hike;

import java.io.Serializable;

public class Observation implements Serializable {
    private long id;
    private long hikeId;
    private String observation;
    private String time;
    private String additionalComments;

    public Observation() {
    }

    public Observation(long hikeId, String observation, String time, String additionalComments) {
        this.hikeId = hikeId;
        this.observation = observation;
        this.time = time;
        this.additionalComments = additionalComments;
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getHikeId() {
        return hikeId;
    }

    public void setHikeId(long hikeId) {
        this.hikeId = hikeId;
    }

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAdditionalComments() {
        return additionalComments;
    }

    public void setAdditionalComments(String additionalComments) {
        this.additionalComments = additionalComments;
    }
}

