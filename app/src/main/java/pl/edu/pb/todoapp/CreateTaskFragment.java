package pl.edu.pb.todoapp;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CreateTaskFragment extends Fragment {

    private Button createButton;
    private EditText taskNameBox;
    private EditText taskDateBox;
    private EditText taskTimeBox;
    private EditText taskDescriptionBox;
    private RadioGroup taskPriorityButton;
    private DatePickerDialog datePicker;
    private TimePickerDialog timePicker;
    private TaskViewModel taskViewModel;
    private CategoryViewModel categoryViewModel;
    private Task currentTask;
    private TextView header;
    private Spinner categorySpinner;
    private ArrayList<Integer> categoryIdList;
    private List<Category> categoryNameList;
    private Integer selectedCategoryId = null;
    public static final String KEY_ADDED_SUCCESSFULLY = "was added successfully?";

    private String timeIntoString(int hourOfDay, int minute)
    {
        String time = "";
        if (hourOfDay < 10) time += 0;
        time += Integer.toString(hourOfDay);
        time += ":";
        if (minute < 10) time += 0;
        time += Integer.toString(minute);
        return time;
    }

    private String dateIntoString(int year, int month, int dayOfMonth)
    {
        String date = "";
        if (dayOfMonth < 10) date += 0;
        date += Integer.toString(dayOfMonth);
        date += ".";
        if (month + 1 < 10) date += 0;
        date += Integer.toString(month + 1);
        date += ".";
        date += Integer.toString(year);
        return date;
    }

    private boolean checkIfEmpty()
    {
        String name = taskNameBox.getText().toString();
        String date = taskDateBox.getText().toString();
        String time = taskTimeBox.getText().toString();

        if(name.isEmpty() || date.isEmpty() || time.isEmpty())
        {
            return false;
        }
        return true;
    }

    private boolean addNewTask()
    {
        if(!checkIfEmpty()) return false;
        taskViewModel.insert(changeTask(new Task()));
        return true;
    }

    private boolean updateTask(Task currentTask)
    {
        if(!checkIfEmpty()) return false;
        taskViewModel.update(changeTask(currentTask));
        return true;
    }

    private Task changeTask(Task task)
    {
        String name = taskNameBox.getText().toString();
        String date = taskDateBox.getText().toString();
        String time = taskTimeBox.getText().toString();

        task.setName(name);
        task.setDescription(taskDescriptionBox.getText().toString());
        Priority taskPriority = Priority.LOW;

        if(taskPriorityButton.getCheckedRadioButtonId() == R.id.high_priority)
            taskPriority = Priority.HIGH;
        else if(taskPriorityButton.getCheckedRadioButtonId() == R.id.medium_priority)
            taskPriority = Priority.MEDIUM;

        task.setPriority(taskPriority);

        Category category = (Category)categorySpinner.getSelectedItem();
        if(category != null)
            task.setCategoryId(category.getId());

        int day = Integer.parseInt(date.substring(0,2));
        int month = Integer.parseInt(date.substring(3,5)) - 1;
        int year = Integer.parseInt(date.substring(6,10)) - 1900;
        int hour = Integer.parseInt(time.substring(0,2));
        int minutes = Integer.parseInt(time.substring(3,5));

        task.setDate(
                new Date(year, month,day,hour,minutes
        ));
        return task;
    }

    private void setFields()
    {
        if(currentTask != null) {
            taskNameBox.setText(currentTask.getName());
            taskDescriptionBox.setText(currentTask.getDescription());
            if(currentTask.getPriority() == Priority.HIGH)
                taskPriorityButton.check(R.id.high_priority);
            else if(currentTask.getPriority() == Priority.MEDIUM)
                taskPriorityButton.check(R.id.medium_priority);
            else
                taskPriorityButton.check(R.id.low_priority);

            Date date = currentTask.getDate();
            taskDateBox.setText(dateIntoString(date.getYear() + 1900,date.getMonth(),date.getDate()));
            taskTimeBox.setText(timeIntoString(date.getHours(), date.getMinutes()));
            createButton.setText(getString(R.string.edit_task_title));
            header.setText(getString(R.string.edit_task_header));
        }
    }

    private void setUpAdapter()
    {
        ArrayAdapter<Category> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categoryNameList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        categoryIdList = new ArrayList<>();
        //categoryNameList = new ArrayList<>();
        taskViewModel = ViewModelProviders.of(this).get(TaskViewModel.class);
        Intent startngIntent = getActivity().getIntent();
        final int id = startngIntent.getIntExtra(TaskListFragment.KEY_EXTRA_TASK_ID, -1);
        taskViewModel.findTaskById(id).observe(this, new Observer<Task>() {
            @Override
            public void onChanged(Task task) {
                currentTask = task;
                setFields();
            }
        });

        categoryViewModel = ViewModelProviders.of(this).get(CategoryViewModel.class);
        categoryViewModel.findAllCategories().observe(this, new Observer<List<Category>>() {
            @Override
            public void onChanged(List<Category> categories) {
                categoryNameList = (ArrayList<Category>) categories;
                if(categorySpinner.getAdapter() == null)
                    setUpAdapter();
            }
        });

    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.create_task, container, false); //never true when using a fragment
        createButton = view.findViewById(R.id.create_task_button);
        taskNameBox = view.findViewById(R.id.task_name_box);
        taskDateBox = view.findViewById(R.id.task_date_box);
        taskTimeBox = view.findViewById(R.id.task_time_box);
        taskDescriptionBox = view.findViewById(R.id.task_description_box);
        taskPriorityButton = view.findViewById(R.id.task_priority_group);
        header = view.findViewById(R.id.create_task_header);
        categorySpinner = view.findViewById(R.id.category_choice_spinner);

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean result;
                if(currentTask != null)
                    result = updateTask(currentTask);
                else
                    result = addNewTask();

                Intent replyIntent = new Intent();
                if(result == true)
                    getActivity().setResult(Activity.RESULT_OK, replyIntent);
                else
                    getActivity().setResult(Activity.RESULT_CANCELED, replyIntent);
                getActivity().finish();
            }
        });

        taskDateBox.setInputType(InputType.TYPE_NULL); //thanks to that keyboard doesn't unnecessarily pop up
        taskDateBox.setFocusable(false);//thanks to that keyboard doesn't unnecessarily pop up
        taskDateBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);
                datePicker = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                taskDateBox.setText(dateIntoString(year,month,dayOfMonth));
                            }
                        },
                        year, month, dayOfMonth);
                datePicker.show();
            }
        });

        taskTimeBox.setInputType(InputType.TYPE_NULL);
        taskTimeBox.setFocusable(false);
        taskTimeBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
                int mins = calendar.get(Calendar.MINUTE);
                timePicker = new TimePickerDialog(getActivity(), 2,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                taskTimeBox.setText(timeIntoString(hourOfDay,minute));
                            }
                        },
                        hourOfDay, mins, true);

                timePicker.show();
            }
        });

        return view;
    }

}
