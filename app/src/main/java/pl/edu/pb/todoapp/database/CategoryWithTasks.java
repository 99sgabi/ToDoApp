package pl.edu.pb.todoapp.database;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class CategoryWithTasks {
    @Embedded public Category category;
    @Relation(
            parentColumn = "id",
            entityColumn = "categoryId"
    )
    public List<Task> tasks;
}
