package com.example.lab_6.service.repository;

import android.database.sqlite.SQLiteDatabase;

import com.example.lab_6.models.NotificationModel;
import com.example.lab_6.service.Service;

import java.util.List;

public interface NotificationRepository {
    void onCreate(SQLiteDatabase db);

    void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);

    /**
     * Добавить уведомление в БД
     *
     * @param model - данные уведомления для внесения в БД
     * @return id записи в БД ( или -1)
     */
    long insertNotification(NotificationModel model);

    /**
     * Удалить уведомление из БД
     *
     * @param id - идентификатор записи в БД
     */
    void removeNotification(long id);

    /**
     * Получить все уведомления из БД
     *
     * @return
     */
    List<NotificationModel> getNotifications();

    /**
     * Добавить слушателя изменения БД
     *
     * @param listener
     */
    void addListener(Service.INotificationListener listener);

    /**
     * Удалить слушателя изменения БД
     *
     * @param listener
     */
    void removeListener(Service.INotificationListener listener);

    /**
     * Оповестить слушателей о необходимости обновить свои данные
     */
    void notifyUpdate();
}
