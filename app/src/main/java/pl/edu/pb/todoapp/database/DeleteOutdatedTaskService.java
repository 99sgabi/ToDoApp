package pl.edu.pb.todoapp.database;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

import androidx.lifecycle.ViewModelProviders;

public class DeleteOutdatedTaskService extends IntentService {
    private static final String ACTION_FOO = "pl.edu.pb.todoapp.database.action.FOO";
    private static final int DELETE_TIME = 3600001;
    private TaskViewModel taskViewModel;

    public DeleteOutdatedTaskService() {
        super("DeleteOutdatedTaskService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FOO.equals(action)) {
               // taskViewModel = ViewModelProviders.of(this.getCo).get(TaskViewModel.class);
                long currentTime = System.currentTimeMillis();
                taskViewModel.deleteOutdatedTasks(currentTime - currentTime);
            }
        }
    }

}
