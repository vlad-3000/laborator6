package com.example.lab_6.service;

import android.content.Context;

import com.example.lab_6.models.NotificationModel;
import com.example.lab_6.service.impl.SQLiteNotificationsImpl;
import com.example.lab_6.service.repository.NotificationRepository;

public class Service {
    public interface INotificationListener {
        void onInserted(NotificationModel model);

        void onRemoved(long id);

        void update();
    }

    private static Service instance;
    public NotificationRepository notificationsRepository;

    public static Service getInstance() {
        return instance;
    }

    public static Service createInstance(Context context) {
        if (instance == null) instance = new Service(context);
        return instance;
    }

    public Service(Context context) {
        NotificationHelper helper = new NotificationHelper(context);
        this.notificationsRepository = new SQLiteNotificationsImpl(helper);
    }
}