package pl.edu.pb.todoapp.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Task.class, Category.class}, version = 2)
@TypeConverters({ Task.Converter.class })
public abstract class TaskDatabase extends RoomDatabase {
    public abstract TaskDao taskDao();
    public abstract CategoryDao categoryDao();

    private static volatile TaskDatabase DATABASE_INSTANCE;
    public static final int NUMBER_OF_THREADS = 8;
    static final ExecutorService databaseWriterExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static TaskDatabase getDatabaseInstance(final Context context)
    {
        if(DATABASE_INSTANCE == null)
            synchronized (TaskDatabase.class)
            {
                if(DATABASE_INSTANCE == null)
                {
                    DATABASE_INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            TaskDatabase.class, "to_do_db").addCallback(initialDataCallBack).build();
                }
            }
        return DATABASE_INSTANCE;
    }

    private static RoomDatabase.Callback initialDataCallBack = new RoomDatabase.Callback() {
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
            databaseWriterExecutor.execute( () -> {
                CategoryDao categoryDao = DATABASE_INSTANCE.categoryDao();
                if(categoryDao.countDefaultCategories() < 1)
                {
                    Category default_category = new Category();
                    default_category.setName("Default Category");
                    default_category.setShortDescription("This is default category");
                    categoryDao.insert(default_category);
                }
            });
        }
    };
}
