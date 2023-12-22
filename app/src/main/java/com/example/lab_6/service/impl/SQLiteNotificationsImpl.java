package com.example.lab_6.service.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.lab_6.models.NotificationModel;
import com.example.lab_6.service.CommonService;
import com.example.lab_6.service.NotificationHelper;
import com.example.lab_6.service.Service;
import com.example.lab_6.service.repository.NotificationRepository;

import java.util.ArrayList;
import java.util.List;

public class SQLiteNotificationsImpl implements NotificationRepository {
    private final String TABLE_NAME = "notifications";
    private final String ID = "_id";
    private final String TITLE = "title";
    private final String TEXT = "text";
    private final String DATE_TIME = "date_time";
    private NotificationHelper mHelper;

    private List<Service.INotificationListener> mListeners;

    public void addListener(Service.INotificationListener listener) {
        if (mListeners.contains(listener)) return;
        mListeners.add(listener);
    }

    public void removeListener(Service.INotificationListener listener) {
        if (!mListeners.contains(listener)) return;
        mListeners.remove(listener);
    }

    private void notifyInsert(NotificationModel model) {
        for (Service.INotificationListener l : mListeners) l.onInserted(model);
    }

    private void notifyRemove(long id) {
        for (Service.INotificationListener l : mListeners) {
            l.onRemoved(id);
        }
    }

    public SQLiteNotificationsImpl(SQLiteOpenHelper helper) {
        this.mHelper = (NotificationHelper) helper;
        this.mListeners = new ArrayList<>();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT NOT NULL, %s TEXT NOT NULL, %s INTEGER NOT NULL)",
                    TABLE_NAME,
                    ID,
                    TITLE,
                    TEXT,
                    DATE_TIME));
        } catch (SQLException e) {
            e.printStackTrace();
            CommonService.getInstance().showToast(e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL(String.format("DROP TABLE IF EXISTS %s", TABLE_NAME));
            onCreate(db);
        } catch (SQLException e) {
            e.printStackTrace();
            CommonService.getInstance().showToast(e.getMessage());
        }
    }

    @Override
    public long insertNotification(NotificationModel model) {
        ContentValues values = getContentValues(model);
        try {
            long row_id = mHelper.getWritableDatabase().insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
            model.setId(row_id);
            notifyInsert(model);
            return row_id;
        } catch (SQLException e) {
            e.printStackTrace();
            CommonService.getInstance().showToast(e.getMessage());
        }
        return -1;
    }

    @Override
    public void removeNotification(long id) {
        try {
            mHelper.getWritableDatabase().delete(TABLE_NAME, ID + "=" + id, null);
            notifyRemove(id);
        } catch (SQLException e) {
            e.printStackTrace();
            CommonService.getInstance().showToast(e.getMessage());
        }
    }

    @Override
    public List<NotificationModel> getNotifications() {
        List<NotificationModel> data = new ArrayList<>();
        try {
            Cursor cursor = mHelper.getWritableDatabase().query(TABLE_NAME, new String[]{ID, TITLE, TEXT, DATE_TIME}, null, null, null, null, DATE_TIME);
            if (cursor == null) return null;
            if (!cursor.moveToFirst()) return null;
            String title, text;
            long date_time = -1;
            Long id = -1L;
            do {
                id = cursor.getLong(0);
                title = cursor.getString(1);
                text = cursor.getString(2);
                date_time = cursor.getLong(3);
                NotificationModel mate = new NotificationModel(title, text, date_time);
                mate.setId(id);
                data.add(mate);
            } while (cursor.moveToNext());
        } catch (RuntimeException e) {
            e.printStackTrace();
            CommonService.getInstance().showToast(e.getMessage());
        }
        return data;
    }

    /**
     * Формирование значений для вставки в БД
     *
     * @param model - Класс с нужными полями
     * @return ContentValues заполненный нужными полями
     */
    private ContentValues getContentValues(NotificationModel model) {
        ContentValues values = new ContentValues();
        values.put(TITLE, model.title);
        values.put(TEXT, model.text);
        values.put(DATE_TIME, model.dateTime);
        return values;
    }

    @Override
    public void notifyUpdate() {
        for (Service.INotificationListener l : mListeners) {
            l.update();
        }
    }
}
