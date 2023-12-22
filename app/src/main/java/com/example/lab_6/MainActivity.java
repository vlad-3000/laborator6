package com.example.lab_6;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab_6.databinding.ActivityMainBinding;
import com.example.lab_6.models.NotificationModel;
import com.example.lab_6.service.CommonService;
import com.example.lab_6.service.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity implements Service.INotificationListener {
    private ActivityMainBinding binding;
    private NotificationsAdapter mAdapter;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        requestPermissions();

        setSupportActionBar(binding.toolbar);
        binding.toolbar.setTitle("Список уведомлений");

        binding.fabAdd.setOnClickListener(this::onFabClick);
        List<NotificationModel> notifications = Service.getInstance().notificationsRepository.getNotifications();
        mAdapter = new NotificationsAdapter(this, notifications);
        binding.rvNotifications.setLayoutManager(new LinearLayoutManager(this));
        binding.rvNotifications.setAdapter(mAdapter);

        Service.getInstance().notificationsRepository.addListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Service.getInstance().notificationsRepository.removeListener(this);
    }

    private void requestPermissions() {
        String[] permissions = new String[]{Manifest.permission.USE_EXACT_ALARM, Manifest.permission.POST_NOTIFICATIONS, Manifest.permission.SCHEDULE_EXACT_ALARM, Manifest.permission.WAKE_LOCK};
        for (String perm : permissions) {
            if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, permissions, 0);
                return;
            }
        }
    }

    private void onFabClick(View v) {
        DialogAddNotification dialogAddNotification = new DialogAddNotification(this, (model) -> {
            CommonService.getInstance().addNotification(model);
        });
        dialogAddNotification.show();
    }

    @Override
    public void onInserted(NotificationModel model) {
        List<NotificationModel> models;
        if (mAdapter.mData != null) {
            models = mAdapter.mData;
            models.add(model);
        } else {
            models = Service.getInstance().notificationsRepository.getNotifications();
        }
        mAdapter.setData(models);
    }

    @Override
    public void onRemoved(long id) {
        List<NotificationModel> models = mAdapter.mData.stream().filter((model) -> model.ID != id).collect(Collectors.toList());
        if (models.size() == 0) {
            mAdapter = new NotificationsAdapter(this, models);
            binding.rvNotifications.setAdapter(mAdapter);
            return;
        }
        mAdapter.setData(models);
    }

    @Override
    public void update() {
        List<NotificationModel> models = Service.getInstance().notificationsRepository.getNotifications();
        mAdapter = new NotificationsAdapter(this, models);
        binding.rvNotifications.setAdapter(mAdapter);
    }

    class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.NotificationViewHolder> {
        private LayoutInflater mInflater;
        private List<NotificationModel> mData;
        private Context mContext;

        public NotificationsAdapter(Context context, List<NotificationModel> data) {
            this.mInflater = LayoutInflater.from(context);
            this.mData = data;
            this.mContext = context;
        }

        public void setData(List<NotificationModel> data) {
            this.mData = data;
            notifyDataSetChanged();
        }

        @Override
        public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
            if (mData == null) return;
            holder.setData(mData.get(position));
        }

        @Override
        public int getItemCount() {
            if (mData == null) return 0;
            return mData.size();
        }

        @NonNull
        @Override
        public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.item_notification, parent, false);
            return new NotificationViewHolder(view);
        }

        class NotificationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private final View mItemView;
            private TextView mTitle;
            private TextView mDateTime;
            private ImageButton mBtnRemove;
            private NotificationModel mModel;

            public NotificationViewHolder(@NonNull View itemView) {
                super(itemView);
                this.mItemView = itemView;
                this.mItemView.setOnClickListener(this);
                mTitle = mItemView.findViewById(R.id.tv_title);
                mDateTime = mItemView.findViewById(R.id.tv_dateTime);
                mBtnRemove = mItemView.findViewById(R.id.btn_remove);
                mBtnRemove.setOnClickListener(this::onRemoveClick);
            }

            private void onRemoveClick(View v) {
                if (this.mModel.ID == null) {
                    CommonService.getInstance().showToast("Неизвестный ID");
                    return;
                }
                CommonService.getInstance().removeNotification(this.mModel.ID);
            }

            public void setData(NotificationModel model) {
                mTitle.setText(model.title);
                mDateTime.setText(dateFormat.format(new Date(model.dateTime)));
                this.mModel = model;
                Date now = new Date();
                if (mModel.dateTime <= now.getTime()) {
                    ImageView logo = (ImageView) mItemView.findViewById(R.id.img_notification);
                    logo.setImageTintList(ColorStateList.valueOf(Color.RED));
                }
            }

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, NotificationDetailActivity.class);
                intent.putExtra(CommonService.EXTRA_TITLE, mModel.title);
                intent.putExtra(CommonService.EXTRA_TEXT, mModel.text);
                intent.putExtra(CommonService.EXTRA_DATE_TIME, mModel.dateTime);
                intent.putExtra(CommonService.EXTRA_ID, mModel.ID);
                startActivity(intent);
            }
        }
    }
}