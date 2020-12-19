package pl.edu.pb.todoapp;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.Date;
import java.util.List;

@Dao
public interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insert(Task task);

    @Update
    public void update(Task task);

    @Delete
    public void delete(Task task);

    @Query("SELECT * FROM tasks ORDER BY date")
    public LiveData<List<Task>> loadTasks();

    @Query("SELECT * FROM tasks ORDER BY priority DESC")
    public LiveData<List<Task>> loadTasksOrderByPriority();

    @Query("SELECT * FROM tasks WHERE :currentDate <= date ORDER BY date")
    public LiveData<List<Task>> loadTasks(Date currentDate);

    @Query("SELECT * FROM tasks WHERE :currentDate >= date AND done = 0 ORDER BY date DESC")
    public LiveData<List<Task>> loadMissedTasks(Date currentDate);

    @Query("SELECT * FROM tasks WHERE name LIKE :likeName ORDER BY date")
    public LiveData<List<Task>> loadTasks(String likeName);

    @Query("SELECT * FROM tasks WHERE id = :id")
    public LiveData<Task> loadTask(int id);

    @Query("SELECT tasks.* FROM tasks, categories " +
            "WHERE tasks.categoryId = categories.id AND (tasks.name LIKE :likeName OR categories.name LIKE :likeName)")
    public LiveData<List<Task>> loadTasksIncludingCategories(String likeName);

    @Query("SELECT * FROM tasks WHERE rowid = :rowId")
    public LiveData<Task> getLastTask(int rowId);

}
