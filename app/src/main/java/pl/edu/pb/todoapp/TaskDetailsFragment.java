package pl.edu.pb.todoapp;

import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

public class TaskDetailsFragment extends Fragment {

    TextView dateField;
    CheckBox doneCheckBox;
    TextView nameField;
    Task task;
    Chronometer chronometer;
    private TaskViewModel taskViewModel;
    public static String ARG_TASK_ID = "argTaskID";

    private void setTask(Task t)
    {
        task = t;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //UUID taskId = (UUID)getArguments().getSerializable(ARG_TASK_ID);
        //this.task = TaskStorage.getInstance().getTask(taskId);
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater,container,savedInstanceState);

        View view = inflater.inflate(R.layout.details_task, container,false);
        nameField = view.findViewById(R.id.task_name);
        dateField = view.findViewById(R.id.task_date);
        doneCheckBox = view.findViewById(R.id.task_done);
        chronometer = view.findViewById(R.id.chronometer);

        taskViewModel = ViewModelProviders.of(this).get(TaskViewModel.class);

        nameField.setInputType(InputType.TYPE_NULL);
        dateField.setInputType(InputType.TYPE_NULL);

        String displayDate = "";
        int dayOfMonth = task.getDate().getDate();
        int month = task.getDate().getMonth() + 1;
        int year = 1900 + task.getDate().getYear();

        int hour = task.getDate().getHours();
        int minutes = task.getDate().getMinutes();

        displayDate += dayOfMonth + "/";
        if(month < 10) displayDate +="0";
        displayDate+= month + "/";
        displayDate += year + "    ";

        displayDate += hour + ":";
        if(minutes<10)displayDate +="0";
        displayDate += minutes;

        dateField.setText(displayDate);
        nameField.setText(task.getName());
        doneCheckBox.setChecked(task.getDone());

        doneCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                task.setDone(isChecked);
                taskViewModel.update(task);
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        chronometer.setBase(System.currentTimeMillis() - task.getDate().getTime());
        chronometer.start();
    }

    @Override
    public void onStop() {
        super.onStop();
        chronometer.stop();
    }

    public static TaskDetailsFragment newInstance(Task task)
    {
        //Bundle bundle= new Bundle();
        //bundle.putSerializable(ARG_TASK_ID, taskId);
        TaskDetailsFragment taskDetailsFragment = new TaskDetailsFragment();
        taskDetailsFragment.setTask(task);
        //taskDetailsFragment.setArguments(bundle);
        return taskDetailsFragment;
    }


}
