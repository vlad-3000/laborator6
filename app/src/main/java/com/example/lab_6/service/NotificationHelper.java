package com.example.lab_6.service;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class NotificationHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "lab3_db";
    public static final int DB_VERSION = 1;


    public NotificationHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Service.getInstance().notificationsRepository.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Service.getInstance().notificationsRepository.onUpgrade(db, oldVersion, newVersion);
    }
}
