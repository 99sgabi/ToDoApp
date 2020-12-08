package pl.edu.pb.todoapp;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.Date;
import java.util.List;

public class TaskViewModel extends AndroidViewModel {
    private TaskRepository taskRepository;
    private LiveData<List<Task>> tasks;

    public TaskViewModel(@NonNull Application application)
    {
        super(application);
        taskRepository = new TaskRepository(application);
        tasks = taskRepository.loadAllTasks();
    }

    public LiveData<Task> findTaskById(int id)
    {
        return taskRepository.findTaskById(id);
    }

    public LiveData<List<Task>> findAllTasks()
    {
        return tasks;
    }

    public LiveData<List<Task>> findTasks(String name)
    {
        return taskRepository.loadTasks(name);
    }

    public LiveData<List<Task>> findTasks(Date startDate, Date endDate)
    {
        return taskRepository.loadTasks(startDate, endDate);
    }

    public void insert(Task task)
    {
        taskRepository.insert(task);
    }

    public void update(Task task)
    {
        taskRepository.update(task);
    }

    public void delete(Task task)
    {
        taskRepository.delete(task);
    }

}
