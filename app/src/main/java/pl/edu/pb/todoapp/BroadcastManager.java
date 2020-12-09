package pl.edu.pb.todoapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BroadcastManager extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Task task = new Task();
    }

    private void createNotification()
    {

    }
}
