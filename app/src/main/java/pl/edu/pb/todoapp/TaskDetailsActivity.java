package pl.edu.pb.todoapp;

import androidx.fragment.app.Fragment;

import android.os.Bundle;

import java.util.UUID;

public class TaskDetailsActivity extends SingleFragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startUp();
    }

    @Override
    protected Fragment createFragment() {
        //UUID taskID = (UUID)getIntent().getSerializableExtra(TaskListFragment.KEY_EXTRA_TASK_ID);
        Bundle bundle = getIntent().getBundleExtra(TaskListFragment.KEY_TASK);
        Task task = (Task) bundle.getSerializable(TaskListFragment.KEY_TASK);
        return TaskDetailsFragment.newInstance(task);
    }
}