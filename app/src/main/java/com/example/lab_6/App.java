package com.example.lab_6;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;

import com.example.lab_6.service.CommonService;
import com.example.lab_6.service.Service;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        initializeServices();
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CommonService.NOTIFICATION_CHANNEL_ID, CommonService.NOTIFICATION_CHANNEL_NAME, CommonService.IMPORTANCE);
            channel.setDescription(CommonService.NOTIFICATION_DESCRIPTION);
            channel.enableLights(true);
            channel.setLightColor(Color.MAGENTA);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});

            NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);
        }
    }

    private void initializeServices() {
        CommonService.createInstance(getApplicationContext());
        Service.createInstance(getApplicationContext());
    }
}
