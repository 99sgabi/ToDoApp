package pl.edu.pb.todoapp;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class NotificationService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FOO = "pl.edu.pb.todoapp.action.FOO";
    private static final String ACTION_BAZ = "pl.edu.pb.todoapp.action.BAZ";

    // TODO: Rename parameters
    public static final String EXTRA_TASK_NAME = "pl.edu.pb.todoapp.extra.TASKNAME";
    public   static final String EXTRA_TASK_ID = "pl.edu.pb.todoapp.extra.TASKID";

    public NotificationService() {
        super("NotificationService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    /*/ TODO: Customize helper method
    public static void startActionFoo(Context context, String param1, String param2) {
        Intent intent = new Intent(context, NotificationService.class);
        intent.setAction(ACTION_FOO);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }
*/
    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    /*// TODO: Customize helper method
    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, NotificationService.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }*/

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String name = intent.getStringExtra(EXTRA_TASK_NAME);
            final int id = intent.getIntExtra(EXTRA_TASK_ID, -1);
            if(id != -1)
                createNotification(name, id);
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void createNotification(String task_name, int task_id) {
        Intent notifyIntent = new Intent(this, TaskListActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 2, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

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

        Notification notificationCompat = builder.build();
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        //id of the notification is equal to the id of the task
        managerCompat.notify(task_id, notificationCompat);
    }

}
