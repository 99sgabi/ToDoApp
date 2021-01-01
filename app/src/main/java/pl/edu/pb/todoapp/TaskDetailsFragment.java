package pl.edu.pb.todoapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import pl.edu.pb.todoapp.database.Task;
import pl.edu.pb.todoapp.database.TaskViewModel;

public class TaskDetailsFragment extends Fragment {

    TextView dateField;
    CheckBox doneCheckBox;
    TextView nameField;
    TextView categoryField;
    Task task;
    int taskID;
    boolean isChronometerRunning;
    Chronometer chronometer;
    Switch notificationButton;
    private TaskViewModel taskViewModel;
    public static String ARG_TASK_ID = "argTaskID";
    public static String KEY_TASK = "TasksSerializable";

    private void setTask(Task t)
    {
        task = t;
    }

    private void chronometerManagement()
    {
        if(!isChronometerRunning)
        {
            chronometer.setBase(task.getDate().getTime() - System.currentTimeMillis());
            chronometer.start();
            isChronometerRunning = true;
        }
    }

    private void setUpFields()
    {
        if(task.getNotifyUser())
            notificationButton.setChecked(true);
        else
            notificationButton.setChecked(false);

        dateField.setText(getTasksDateString());
        nameField.setText(task.getName());
        doneCheckBox.setChecked(task.getDone());
        chronometerManagement();
    }

    private void setTask(int id)
    {
        taskViewModel.findTaskById(id).observe(getViewLifecycleOwner(), new Observer<Task>() {
            @Override
            public void onChanged(Task t) {
                task = t;
                setUpFields();
            }
        });
    }

    private void setTaskID(int taskID)
    {
        this.taskID = taskID;
    }

    public static void scheduleNotification(Context context, Task task) {
        if(task.getDate().getTime() <= System.currentTimeMillis() || task == null) return;

        Intent notificationIntent = new Intent(context, NotificationReceiver.class);
        notificationIntent.putExtra(NotificationReceiver.NOTIFICATION_TASK_ID, task.getId());
        notificationIntent.putExtra(NotificationReceiver.NOTIFICATION_TASK_NAME, task.getName());
        PendingIntent pendingIntent2 = PendingIntent.getBroadcast(context, task.getId(), notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        long timeOfTheNotification = task.getDate().getTime();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                    timeOfTheNotification, pendingIntent2);
        }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AlarmManager.AlarmClockInfo alarmClockInfo = new AlarmManager.AlarmClockInfo(timeOfTheNotification, pendingIntent2);
            alarmManager.setAlarmClock(alarmClockInfo, pendingIntent2);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater,container,savedInstanceState);

        View view = inflater.inflate(R.layout.details_task, container,false);
        nameField = view.findViewById(R.id.task_name);
        categoryField = view.findViewById(R.id.task_category);
        dateField = view.findViewById(R.id.task_date);
        doneCheckBox = view.findViewById(R.id.task_done);
        chronometer = view.findViewById(R.id.chronometer);
        notificationButton = view.findViewById(R.id.notifications_switch);
        notificationButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                task.setNotifyUser(isChecked);
                taskViewModel.update(task);
                if(isChecked)
                {
                    scheduleNotification(getActivity().getApplicationContext(), task);
                }
                else
                {
                    cancelNotification(getActivity().getApplicationContext(), task);
                }
            }
        });

        taskViewModel = ViewModelProviders.of(this).get(TaskViewModel.class);
        setTask(taskID);
        nameField.setInputType(InputType.TYPE_NULL);
        dateField.setInputType(InputType.TYPE_NULL);

        taskViewModel.getCategoryName(taskID).observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String categoryName) {
                categoryField.setText(getResources().getString(R.string.task_details_category_label,categoryName));
            }
        });

        doneCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                task.setDone(isChecked);
                taskViewModel.update(task);
            }
        });
        setVisibilityForChronometer();
        return view;
    }

    public static void cancelNotification(Context context, Task task) {
        Intent notificationIntent = new Intent(context, NotificationReceiver.class);
        notificationIntent.putExtra(NotificationReceiver.NOTIFICATION_TASK_ID, task.getId());
        notificationIntent.putExtra(NotificationReceiver.NOTIFICATION_TASK_NAME, task.getName());
        PendingIntent pendingIntent2 = PendingIntent.getBroadcast(context, task.getId(), notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent2);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(task != null)
            chronometerManagement();
    }

    @Override
    public void onPause() {
        super.onPause();
        chronometer.stop();
        isChronometerRunning = false;
    }

    /*public static TaskDetailsFragment newInstance(Task task)
    {
        //Bundle bundle= new Bundle();
        //bundle.putSerializable(ARG_TASK_ID, taskId);
        TaskDetailsFragment taskDetailsFragment = new TaskDetailsFragment();
        taskDetailsFragment.setTask(task);
        //taskDetailsFragment.setArguments(bundle);
        return taskDetailsFragment;
    }*/

    public static TaskDetailsFragment newInstance(int taskID)
    {
        TaskDetailsFragment taskDetailsFragment = new TaskDetailsFragment();
        taskDetailsFragment.setTaskID(taskID);
        return taskDetailsFragment;
    }

    private String getTasksDateString()
    {
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
        return displayDate;
    }

    private void setVisibilityForChronometer()
    {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N)
        {
            chronometer.setVisibility(View.GONE);
        }
    }
}
