package com.example.lab_6.models;

import androidx.annotation.Nullable;

public class NotificationModel {
    public final String title;
    public final String text;
    public final long dateTime;
    @Nullable
    public Long ID;

    public NotificationModel(String title, String text, long dateTime) {
        this.title = title;
        this.text = text;
        this.dateTime = dateTime;
    }

    public void setId(long id) {
        this.ID = id;
    }
}
