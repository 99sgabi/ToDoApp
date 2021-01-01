package pl.edu.pb.todoapp;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import androidx.core.app.TaskStackBuilder;
import android.content.Intent;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationService extends IntentService {

    public static final String EXTRA_TASK_NAME = "pl.edu.pb.todoapp.extra.TASKNAME";
    public static final String EXTRA_TASK_ID = "pl.edu.pb.todoapp.extra.TASKID";

    public NotificationService() {
        super("NotificationService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String name = intent.getStringExtra(EXTRA_TASK_NAME);
            final int id = intent.getIntExtra(EXTRA_TASK_ID, -1);
            if(id != -1)
                createNotification(name, id);
        }
    }

    private void createNotification(String task_name, int task_id) {
        Intent notifyIntent = new Intent(this, TaskDetailsActivity.class);
        notifyIntent.putExtra(TaskListFragment.KEY_EXTRA_TASK_ID, task_id);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(notifyIntent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                //PendingIntent.getActivity(this, 2, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            builder = new Notification.Builder(this,TaskListActivity.CHANNEL_NOTIFICATION_ID);
        else
            builder = new Notification.Builder(this);
        builder.setContentTitle(getString(R.string.notification_title));
        builder.setContentText(task_name);
        builder.setSmallIcon(R.drawable.ic_notification);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);
        builder.setCategory(NotificationCompat.CATEGORY_ALARM);
        builder.setVisibility(Notification.VISIBILITY_PUBLIC);
        builder.setPriority(Notification.PRIORITY_DEFAULT);

        Notification notification = builder.build();
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        //id of the notification is equal to the id of the task
        managerCompat.notify(task_id, notification);
    }

}
