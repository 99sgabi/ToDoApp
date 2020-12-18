package pl.edu.pb.todoapp;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;

import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;


public class CreateTaskActivity extends SingleFragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startUp();
    }

    public Fragment createFragment()
    {
        return new CreateTaskFragment();
    }
}