package com.example.lab_6;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.example.lab_6.databinding.DialogAddNotificationBinding;
import com.example.lab_6.models.NotificationModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DialogAddNotification extends Dialog {
    private IResultListener mListener;
    private TextWatcher mTextWatcher;
    private TimePickerDialog.OnTimeSetListener mTimeListener;
    private DatePickerDialog.OnDateSetListener mDateListener;
    private Calendar mCalendar;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

    public interface IResultListener {
        void onResult(NotificationModel model);
    }

    public DialogAddNotification(Context context, IResultListener listener) {
        super(context);
        this.mListener = listener;
        mTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.btnAccept.setEnabled(canSave());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        mTimeListener = (view, hourOfDay, minute) -> {
            mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            mCalendar.set(Calendar.MINUTE, minute);
            showDatePicker();
        };
        mDateListener = (view, year, month, dayOfMonth) -> {
            mCalendar.set(Calendar.YEAR, year);
            mCalendar.set(Calendar.MONTH, month);
            mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            binding.btnAccept.setEnabled(canSave());
            binding.tvDateTime.setText(dateFormat.format(new Date(mCalendar.getTime().getTime())));
        };
        mCalendar = Calendar.getInstance();
    }

    private void showTimePicker() {
        new TimePickerDialog(getContext(),
                this.mTimeListener,
                mCalendar.get(Calendar.HOUR_OF_DAY),
                mCalendar.get(Calendar.MINUTE),
                true).show();
    }

    private void showDatePicker() {
        new DatePickerDialog(getContext(),
                this.mDateListener,
                mCalendar.get(Calendar.YEAR),
                mCalendar.get(Calendar.MONTH),
                mCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private DialogAddNotificationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.binding = DialogAddNotificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.teTitle.addTextChangedListener(mTextWatcher);
        binding.teText.addTextChangedListener(mTextWatcher);

        binding.btnCancel.setOnClickListener((v) -> dismiss());
        binding.btnAccept.setOnClickListener(this::onAcceptClick);
        binding.btnSetDateTime.setOnClickListener(this::onSetTimeClick);
    }

    private void onSetTimeClick(View v) {
        showTimePicker();
    }

    private void onAcceptClick(View v) {
        Date date = new Date(mCalendar.getTime().getTime());
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        String strDate = dateFormat.format(date);
        try {
            Date utcDate = dateFormat.parse(strDate);
            NotificationModel model = new NotificationModel(binding.teTitle.getText().toString(), binding.teText.getText().toString(), utcDate.getTime());
            mListener.onResult(model);
        } catch (Exception e) {
            e.printStackTrace();
        }
        dismiss();
    }

    private boolean canSave() {
        Date now = new Date();
        boolean correctDateTime = mCalendar.getTime().getTime() > now.getTime();
        boolean correctTitle = !binding.teTitle.getText().toString().isEmpty();
        return correctDateTime && correctTitle;
    }
}
