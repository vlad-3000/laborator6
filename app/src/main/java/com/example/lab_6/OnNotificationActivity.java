package com.example.lab_6;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.example.lab_6.service.CommonService;
import com.example.lab_6.service.Service;

public class OnNotificationActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        long id = getIntent().getLongExtra(CommonService.EXTRA_ID, -1);
        CommonService.getInstance().showToast("Уведомление!");
        Service.getInstance().notificationsRepository.notifyUpdate();
//        Service.getInstance().notificationsRepository.removeNotification(id);
        finish();
    }
}
