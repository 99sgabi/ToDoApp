package pl.edu.pb.todoapp.database;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class CategoryViewModel extends AndroidViewModel {
    private CategoryRepository categoryRepository;
    private LiveData<List<Category>> categories;

    public CategoryViewModel(@NonNull Application application)
    {
        super(application);
        categoryRepository = new CategoryRepository(application);
        categories = categoryRepository.loadAllCategories();
    }

    public LiveData<List<Category>> findAllCategories()
    {
        return categories;
    }

    public LiveData<List<Category>> findCategories(String name)
    {
        return categoryRepository.loadCategories(name);
    }

    public LiveData<List<Category>> findCategoriesByDescription(String partOfDescription)
    {
        return categoryRepository.loadCategoriesByDescription(partOfDescription);
    }

    public void insert(Category category)
    {
        categoryRepository.insert(category);
    }

    public void update(Category category)
    {
        categoryRepository.update(category);
    }

    public void delete(Category category)
    {
        categoryRepository.delete(category);
    }

    public LiveData<Category> loadCategoryById(int id)
    {
        return categoryRepository.loadCategoryById(id);
    }
}
