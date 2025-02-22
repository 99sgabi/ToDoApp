package pl.edu.pb.todoapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import lombok.NonNull;
import pl.edu.pb.todoapp.database.CategoryViewModel;
import pl.edu.pb.todoapp.database.CategoryWithTasks;
import pl.edu.pb.todoapp.database.Task;
import pl.edu.pb.todoapp.database.TaskViewModel;

public class TaskListFragment extends Fragment {

    RecyclerView recyclerView;
    TaskAdapter adapter;
    TextView noTasksTextView;
    public static String KEY_EXTRA_TASK_ID = "extraID";
    public static String KEY_TASK = "extraID";
    public int categoryId = -1;//when it starts from category taskListActivity then id > 0 else -1
    private TaskViewModel taskViewModel;
    private CategoryViewModel categoryViewModel;
    private FloatingActionButton addButton;
    private LifecycleOwner lifecycleOwner = this;
    private LiveData<List<Task>> currentData;
    public static final int REQUEST_CODE_TASK_CREATE = 2;
    public static final int REQUEST_CODE_TASK_EDIT = 3;
    private boolean sortedByDate = true;
    private boolean missedTasks;
    private static final String KEY_SORT="sort";
    private static final String KEY_MISSED_TASKS="missedTasks";
    private static final int DELETE_TIME = 3600001;

    public void setCategoryId(int categoryId)
    {
        this.categoryId = categoryId;
    }

    private void checkIfThereAreTasks(int count)
    {
        if (count > 0 || categoryId > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            noTasksTextView.setVisibility(View.GONE);
        }
        else {
            recyclerView.setVisibility(View.GONE);
            noTasksTextView.setVisibility(View.VISIBLE);
        }
    }

    private void deleteObserver()
    {
        if(currentData != null)
        {
            currentData.removeObservers(lifecycleOwner);
        }
    }

    private void loadAllTaskByDate()
    {
        deleteObserver();
        currentData = taskViewModel.findAllTasks();
        currentData.observe(this, new Observer<List<Task>>() {
            @Override
            public void onChanged(List<Task> tasks) {
                adapter.setTasks(tasks);
                checkIfThereAreTasks(tasks.size());
            }
        });
    }

    private void loadAllTaskBYPriority()
    {
        deleteObserver();
        currentData = taskViewModel.findTasksOrderByPriority();
        currentData.observe(this, new Observer<List<Task>>() {
            @Override
            public void onChanged(List<Task> tasks) {
                adapter.setTasks(tasks);
                checkIfThereAreTasks(tasks.size());
            }
        });
    }

    //this is used in categoryView and doesnt need to clean up observers
    private void loadCategoryTasks()
    {
        categoryViewModel = ViewModelProviders.of(this).get(CategoryViewModel.class);
        categoryViewModel.findTasksListForCategory(categoryId).observe(this, new Observer<CategoryWithTasks>() {
            @Override
            public void onChanged(CategoryWithTasks category) {
                adapter.setTasks(category.tasks);
                checkIfThereAreTasks(category.tasks.size());
            }
        });
    }

    private void loadMissedTasks()
    {
        deleteObserver();
        currentData = taskViewModel.findMissedTasks(System.currentTimeMillis());
        currentData.observe(this, new Observer<List<Task>>() {
            @Override
            public void onChanged(List<Task> tasks) {
                adapter.setTasks(tasks);
                checkIfThereAreTasks(tasks.size());
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null){
            sortedByDate = savedInstanceState.getBoolean(KEY_SORT, true);
            missedTasks = savedInstanceState.getBoolean(KEY_MISSED_TASKS, false);
        }
        taskViewModel = ViewModelProviders.of(this).get(TaskViewModel.class);
        taskViewModel.deleteOutdatedTasks(System.currentTimeMillis() - DELETE_TIME);

        if(categoryId != -1)
            loadCategoryTasks();
        else
        {
            if(missedTasks)
                loadMissedTasks();
            if(sortedByDate)
                loadAllTaskByDate();
            else
                loadAllTaskBYPriority();
            setHasOptionsMenu(true);
        }
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.rv_list, container,false);
        recyclerView = view.findViewById(R.id.task_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        noTasksTextView = view.findViewById(R.id.no_tasks_message);
        addButton = view.findViewById(R.id.floating_add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CreateTaskActivity.class);
                startActivityForResult(intent, REQUEST_CODE_TASK_CREATE);
            }
        });
        setAdapter();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@androidx.annotation.NonNull Menu menu, @androidx.annotation.NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.tasks_menu, menu);
        MenuItem sortOption = menu.findItem(R.id.sort_option);
        sortOption.setVisible(!missedTasks);
        MenuItem missedTaskOption = menu.findItem(R.id.missed_tasks);
        if(missedTasks) {
            missedTaskOption.setTitle(getString(R.string.show_all_tasks));
        }

        MenuItem searchItem = menu.findItem(R.id.menu_item_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //TODO: ZAPYTAC O TEN LIFCYCLE I DLACZEGO TAKSIE TO ROBI DZIWNIE
                deleteObserver();
                currentData = taskViewModel.findTasksWithCategories(query);
                currentData.observe(lifecycleOwner, new Observer<List<Task>>() {
                    @Override
                    public void onChanged(List<Task> tasks) {
                        adapter.setTasks(tasks);
                    }
                });
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@androidx.annotation.NonNull MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.switch_to_categories:
                Intent categoryIntent = new Intent(getActivity(), CategoryListActivity.class);
                startActivity(categoryIntent);
                return true;
            case R.id.sort_option:
                if(sortedByDate)
                {
                    loadAllTaskBYPriority();
                    item.setTitle(getString(R.string.sort_date_title));
                }
                else
                {
                    loadAllTaskByDate();
                    item.setTitle(getString(R.string.sort_priority_title));
                }
                sortedByDate = !sortedByDate;
                return true;
            case R.id.clear_search:
                loadAllTaskByDate();
                return true;
            case R.id.missed_tasks:
                if(missedTasks) {
                    loadAllTaskByDate();
                    //item.setTitle(getString(R.string.show_missed_tasks));
                }
                else {
                    loadMissedTasks();
                    //item.setTitle(getString(R.string.show_all_tasks));
                }
                missedTasks = !missedTasks;
                getActivity().invalidateOptionsMenu();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        setAdapter();
    }

    @Override
    public void onSaveInstanceState(@androidx.annotation.NonNull Bundle outState) {
        //super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_SORT, sortedByDate);
        outState.putBoolean(KEY_MISSED_TASKS, missedTasks);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == TaskListFragment.REQUEST_CODE_TASK_CREATE)
        {
            if(resultCode == Activity.RESULT_OK) {
                Snackbar.make(getActivity().findViewById(R.id.coordinator_layout),
                        getString(R.string.task_created),
                        Snackbar.LENGTH_LONG).show();
            }
            else
                Snackbar.make(getActivity().findViewById(R.id.coordinator_layout),
                        getString(R.string.empty_task_cancelled),
                        Snackbar.LENGTH_LONG).show();
        }
        else if(requestCode == TaskListFragment.REQUEST_CODE_TASK_EDIT)
        {
            if(resultCode == Activity.RESULT_OK) {
                Snackbar.make(getActivity().findViewById(R.id.coordinator_layout),
                        getString(R.string.task_edit_was_successful),
                        Snackbar.LENGTH_LONG).show();
            }
            else
                Snackbar.make(getActivity().findViewById(R.id.coordinator_layout),
                        getString(R.string.task_edit_was_unsuccessful),
                        Snackbar.LENGTH_LONG).show();
        }
    }

    private void setAdapter()
    {
        if(adapter == null)
        {
            adapter = new TaskAdapter(taskViewModel.findAllTasks().getValue());
            recyclerView.setAdapter(adapter);
        }
    }

    private void showDeleteAlert(Task t)
    {
        //shows alert message to delete the task
        new AlertDialog.Builder(getContext())
                .setTitle(getString(R.string.delete_task_title))
                .setMessage(getString(R.string.delete_task_confirmation))
                .setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        TaskDetailsFragment.cancelNotification(getActivity().getApplicationContext(), t);
                        taskViewModel.delete(t);
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { }
                }).show();
    }

    private void createPopUpMenu(View v, Task t)
    {
        PopupMenu popup = new PopupMenu(getActivity(), v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.single_task_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch(item.getItemId())
                {
                    case R.id.menu_delete_task:
                        showDeleteAlert(t);
                        return true;
                    case R.id.menu_edit_task:
                        Intent editIntent = new Intent(getActivity(), CreateTaskActivity.class);
                        editIntent.putExtra(KEY_EXTRA_TASK_ID, t.getId());
                        startActivityForResult(editIntent, REQUEST_CODE_TASK_EDIT);
                        return true;
                    default:
                        return false;
                }
            }
        });
        popup.show();
    }

    private class TaskHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener
    {
        TextView task_name;
        TextView task_date;
        Task task;
        ImageButton checkbox;
        TextView task_priority;

        public TaskHolder (LayoutInflater inflater, ViewGroup parent)
        {
            super(inflater.inflate(R.layout.list_item_task, parent, false));
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

            task_name = itemView.findViewById(R.id.task_item_name);
            task_date = itemView.findViewById(R.id.task_item_date);
            checkbox = itemView.findViewById(R.id.task_is_done);
            task_priority = itemView.findViewById(R.id.task_priority);

            checkbox.setOnClickListener(new View.OnClickListener() {
                @Override
                 public void onClick(View v) {
                    if(task.getDone()){
                        task.setDone(false);
                        checkbox.setImageResource(R.drawable.ic_checkbox_unchecked);
                    }
                    else{
                        task.setDone(true);
                        checkbox.setImageResource(R.drawable.ic_checkbox_checked);
                    }
                    taskViewModel.update(task);
                 }
            });
        }

        private String getTasksDataString()
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

        public void bind(Task task)
        {
            this.task = task;
            task_name.setText(task.getName());
            task_date.setText(getTasksDataString());
            task_priority.setText(
                    getString(task.getPriority().getRId()));
            if(task.getDone())
                checkbox.setImageResource(R.drawable.ic_checkbox_checked);
            else
                checkbox.setImageResource(R.drawable.ic_checkbox_unchecked);

        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), TaskDetailsActivity.class);
            intent.putExtra(KEY_EXTRA_TASK_ID, task.getId());
            /*Bundle bundle = new Bundle();
            bundle.putSerializable(KEY_TASK,task);
            intent.putExtra(KEY_TASK,bundle);*/
            startActivity(intent);
        }

        @Override
        public boolean onLongClick(View v) {
            createPopUpMenu(v, task);
            return true;
        }
    }

    private class TaskAdapter extends RecyclerView.Adapter<TaskHolder>
    {
        private List<Task> tasks;

        public TaskAdapter(List<Task> tasks)
        {
            this.tasks = tasks;
        }

        @NonNull
        @Override
        public TaskHolder onCreateViewHolder(@NonNull ViewGroup parent , int viewType)
        {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new TaskHolder(layoutInflater,parent);
        }

        @Override
        public void onBindViewHolder(@NonNull TaskHolder holder, int position)
        {
            Task task = tasks.get(position);
            holder.bind(task);
        }

        public void setTasks(List<Task> tasks) {
            this.tasks = tasks;
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount()
        {
            if(tasks == null) return 0;
            return tasks.size();
        }
    }

}
