package com.example.m_hike;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DateTimePickerHelper {
    private Calendar calendar;
    private SimpleDateFormat dateTimeFormat;

    public DateTimePickerHelper() {
        calendar = Calendar.getInstance();
        dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
    }

    public void showDateTimePicker(android.app.Activity activity, TextView tvTime) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(activity,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    showTimePicker(activity, tvTime);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void showTimePicker(android.app.Activity activity, TextView tvTime) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(activity,
                (view, hourOfDay, minute) -> {
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);
                    tvTime.setText(dateTimeFormat.format(calendar.getTime()));
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true);
        timePickerDialog.show();
    }

    public String getCurrentDateTime() {
        return dateTimeFormat.format(calendar.getTime());
    }

    public void setDateTime(String dateTime) {
        try {
            calendar.setTime(dateTimeFormat.parse(dateTime));
        } catch (Exception e) {
            calendar = Calendar.getInstance();
        }
    }
}

