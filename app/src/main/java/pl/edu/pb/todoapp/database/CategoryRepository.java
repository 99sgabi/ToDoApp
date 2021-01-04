package pl.edu.pb.todoapp.database;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class CategoryRepository {
    private CategoryDao categoryDao;
    private LiveData<List<Category>> categories;

    CategoryRepository(Application application)
    {
        TaskDatabase taskDatabase = TaskDatabase.getDatabaseInstance(application);
        categoryDao = taskDatabase.categoryDao();
        categories = categoryDao.loadCategories();
    }

    LiveData<CategoryWithTasks> loadTasksListForCategory(int id)
    {
        return categoryDao.getTasksListForCategory(id);
    }

    LiveData<Category> loadCategoryById(int id)
    {
        return categoryDao.loadCategory(id);
    }

    LiveData<List<Category>> loadAllCategories()
    {
        return categories;
    }

    LiveData<List<Category>> loadCategories(String name)
    {
        return categoryDao.loadCategories(name);
    }

    LiveData<List<Category>> loadCategoriesByDescription(String partOfDescription)
    {
        return categoryDao.loadCategoriesContainsInDescription(partOfDescription);
    }

    void insert(Category category){
        TaskDatabase.databaseWriterExecutor.execute(() -> {
            categoryDao.insert(category);
        });
    }

    void update(Category category){
        TaskDatabase.databaseWriterExecutor.execute(() -> {
            categoryDao.update(category);
        });
    }

    void delete(Category category){
        if(category.getId() == 0) return;
        TaskDatabase.databaseWriterExecutor.execute(() -> {
            categoryDao.updateTasksCategoryToDefault(
                    category.getId());
            categoryDao.delete(category);
        });
    }

    LiveData<Integer> getDefaultCategoryId()
    {
        return categoryDao.getDefaultCategoryId();
    }

}
