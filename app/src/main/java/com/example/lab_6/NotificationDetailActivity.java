package com.example.lab_6;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lab_6.databinding.ActivityNotificationDetailBinding;
import com.example.lab_6.models.NotificationModel;
import com.example.lab_6.service.CommonService;

import java.util.Objects;

public class NotificationDetailActivity extends AppCompatActivity {
    private NotificationModel mModel;
    private ActivityNotificationDetailBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.binding = ActivityNotificationDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        this.mModel = new NotificationModel(getIntent().getStringExtra(CommonService.EXTRA_TITLE), getIntent().getStringExtra(CommonService.EXTRA_TEXT), getIntent().getLongExtra(CommonService.EXTRA_DATE_TIME, -1));
        mModel.setId(getIntent().getLongExtra(CommonService.EXTRA_ID, -1));

        setSupportActionBar(binding.toolbar);
        binding.toolbar.setTitle(mModel.title);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        String text = getIntent().getStringExtra(CommonService.EXTRA_TEXT);
        if (text != null) binding.tvText.setText(text);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
