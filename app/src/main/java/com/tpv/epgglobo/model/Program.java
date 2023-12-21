package com.tpv.epgglobo.model;

import android.annotation.SuppressLint;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Program {
    private String name;
    private Timestamp startTime;

    public Program() {}

    public Program(String name, Timestamp startTime) {
        this.name = name;
        this.startTime = startTime;
    }

    public String getName() {
        return name;
    }

    public String getStartTimeStr() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");

        return dateFormat.format(startTime.toDate());
    }

    public Date getStartTime() {
        return startTime.toDate();
    }
}

