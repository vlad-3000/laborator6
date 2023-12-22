package com.example.lab_6.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;

import androidx.core.app.NotificationCompat;

import com.example.lab_6.NotificationDetailActivity;
import com.example.lab_6.OnNotificationActivity;
import com.example.lab_6.R;

import java.util.logging.Logger;

public class AlarmReceiver extends BroadcastReceiver {
    private final Logger log = Logger.getLogger(AlarmReceiver.class.getSimpleName());

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            log.info("AlarmReceiver get message!");
            /** Забираем полезные данные из alarm уведомления*/
            String title = intent.getStringExtra(CommonService.EXTRA_TITLE);
            String text = intent.getStringExtra(CommonService.EXTRA_TEXT);
            long id = intent.getLongExtra(CommonService.EXTRA_ID, -1);
            long dateTime = intent.getLongExtra(CommonService.EXTRA_DATE_TIME, -1);
            log.info("title: " + title + ", text: " + text + ", id: " + id + ", dateTime: " + dateTime);
            /** Готовим интент для уведомления, чтобы при нажатии открывалось NotificationDetailActivity*/
            Intent intent1 = new Intent(context, NotificationDetailActivity.class);
            intent1.putExtra(CommonService.EXTRA_TITLE, title);
            intent1.putExtra(CommonService.EXTRA_TEXT, text);
            intent1.putExtra(CommonService.EXTRA_ID, id);
            intent1.putExtra(CommonService.EXTRA_DATE_TIME, dateTime);
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            /** Создаем уведомление в шторке*/
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, (int) id, intent1, PendingIntent.FLAG_MUTABLE);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CommonService.NOTIFICATION_CHANNEL_ID);
            builder.setSmallIcon(R.drawable.round_circle_notifications_48)
                    .setContentTitle(title)
                    .setContentText(text)
                    .setAutoCancel(true)
                    .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(BitmapFactory.decodeResource(context.getResources(), R.drawable.round_circle_notifications_48)))
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.round_circle_notifications_48))
                    .setPriority(Notification.PRIORITY_MAX)
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .setLights(0x0000FF, 3000, 2000)
                    .setContentIntent(pendingIntent);
            notificationManager.notify(CommonService.NOTIFICATION_ID, builder.build());

            /** Открываем OnNotificationActivity для удаления записи в БД (как новую задачу)*/
            Intent intent2 = new Intent(context, OnNotificationActivity.class);
            intent2.putExtra(CommonService.EXTRA_ID, id);
            intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
