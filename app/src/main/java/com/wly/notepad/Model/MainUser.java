package com.wly.notepad.Model;

import android.app.AlarmManager;
import android.app.PendingIntent;

import com.wly.notepad.Manager.User;
import com.wly.notepad.Manager.datepicker.CustomDatePicker;


public class MainUser {
    public static AlarmManager alarmManager;
    public static PendingIntent pendingIntent;
    public static CustomDatePicker mTimerPicker;
    public static User user;
}
