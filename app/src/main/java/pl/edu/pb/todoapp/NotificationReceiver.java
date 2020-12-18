package pl.edu.pb.todoapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationReceiver extends BroadcastReceiver {
    public static String NOTIFICATION_TASK_ID = "notification_id";
    public static String NOTIFICATION_TASK_NAME = "notification";

    @Override
    public void onReceive(final Context context, Intent intent) {
        Intent intentNotification = new Intent(context, NotificationService.class);
        String task_name = intent.getStringExtra(NOTIFICATION_TASK_NAME);
        int task_id = intent.getIntExtra(NOTIFICATION_TASK_ID, -1);
        intentNotification.putExtra(NotificationService.EXTRA_TASK_NAME, task_name);
        intentNotification.putExtra(NotificationService.EXTRA_TASK_ID, task_id);
        context.startService(intentNotification);
    }
}
