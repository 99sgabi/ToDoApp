package pl.edu.pb.todoapp.database;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.Date;
import java.util.List;

public class TaskRepository {
    private TaskDao taskDao;
    private LiveData<List<Task>> tasks;

    TaskRepository(Application application)
    {
        TaskDatabase taskDatabase = TaskDatabase.getDatabaseInstance(application);
        taskDao = taskDatabase.taskDao();
        tasks = taskDao.loadTasks();
    }

    LiveData<List<Task>> findTasksOrderByPriority()
    {
        return taskDao.loadTasksOrderByPriority();
    }

    LiveData<Task> findTaskById(int id)
    {
        return taskDao.loadTask(id);
    }

    LiveData<List<Task>> loadTasksWithCategories(String name)
    {
        return taskDao.loadTasksIncludingCategories(name);
    }

    LiveData<List<Task>> loadAllTasks()
    {
        return tasks;
    }

    LiveData<List<Task>> loadTasks(String name)
    {
        return taskDao.loadTasks(name);
    }

    LiveData<List<Task>> loadTasks(Date currentDate)
    {
        return taskDao.loadTasks(currentDate);
    }

    void insert(Task task){
        TaskDatabase.databaseWriterExecutor.execute(() -> {
             taskDao.insert(task);
        });
    }

    void update(Task task){
        TaskDatabase.databaseWriterExecutor.execute(() -> {
            taskDao.update(task);
        });
    }

    void delete(Task task){
        TaskDatabase.databaseWriterExecutor.execute(() -> {
            taskDao.delete(task);
        });
    }

    LiveData<String> getCategoryName(int taskId)
    {
        return taskDao.getCategoryName(taskId);
    }

    LiveData<Task> loadTask(int id)
    {
        return taskDao.loadTask(id);
    }

    LiveData<List<Task>> loadMissedTasks(long date)
    {
        return taskDao.loadMissedTasks(date);
    }

    LiveData<Task> getLastTask(int rowId)
    {
        return taskDao.getLastTask(rowId);
    }

    public LiveData<List<Task>> loadCategoryTasks(int categoryId) {
        return taskDao.loadCategoryTasks(categoryId);
    }
}
