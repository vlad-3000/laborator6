package com.example.lab_6.service;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.example.lab_6.models.NotificationModel;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CommonService {
    public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final String EXTRA_TITLE = "extra_title";
    public static final String EXTRA_TEXT = "extra_text";
    public static final String EXTRA_ID = "extra_id";
    public static final String EXTRA_DATE_TIME = "extra_date_time";
    public static final String NOTIFICATION_CHANNEL_NAME = "lab6_notification_channel";
    public static final String NOTIFICATION_CHANNEL_ID = "lab6_notification_channel_id";
    public static final int NOTIFICATION_ID = 123;
    public static final String NOTIFICATION_DESCRIPTION = "lab6_notification_description";
    public static final int IMPORTANCE = NotificationManager.IMPORTANCE_DEFAULT;
    private static CommonService instance;
    public final Context context;
    private Handler mHandler;

    public CommonService(Context context) {
        this.context = context;
        mHandler = new Handler(Looper.getMainLooper());
    }

    public static CommonService getInstance() {
        return instance;
    }

    public static void createInstance(Context context) {
        if (instance == null) instance = new CommonService(context);
    }

    public void showToast(String message) {
        mHandler.post(() -> {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        });
    }

    public void addNotification(NotificationModel model) {
        long id = Service.getInstance().notificationsRepository.insertNotification(model);
        if (id < 0) {
            CommonService.getInstance().showToast("Не удалось создать уведомление");
            return;
        }

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(CommonService.EXTRA_TITLE, model.title);
        intent.putExtra(CommonService.EXTRA_TEXT, model.text);
        intent.putExtra(CommonService.EXTRA_ID, id);
        intent.putExtra(CommonService.EXTRA_DATE_TIME, model.dateTime);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) id, intent, PendingIntent.FLAG_IMMUTABLE);
        try {
            alarmManager.set(AlarmManager.RTC_WAKEUP, model.dateTime, pendingIntent);
            CommonService.getInstance().showToast("Запланировано на " + simpleDateFormat.format(new Date(model.dateTime)));
        } catch (SecurityException e) {
            e.printStackTrace();
            CommonService.getInstance().showToast(e.getMessage());
        }
    }

    public void removeNotification(long id) {
        Service.getInstance().notificationsRepository.removeNotification(id);
        try {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) id, intent, PendingIntent.FLAG_IMMUTABLE);
            alarmManager.cancel(pendingIntent);
        } catch (Exception e) {
            e.printStackTrace();
            CommonService.getInstance().showToast(e.getMessage());
        }
    }

    public Handler getHandler() {
        return mHandler;
    }
}
