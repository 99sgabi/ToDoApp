package pl.edu.pb.todoapp;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insert(Category task);

    @Update
    public void update(Category task);

    @Delete
    public void delete(Category task);

    @Query("SELECT * FROM categories ORDER BY name")
    public LiveData<List<Category>> loadCategories();

    @Query("SELECT * FROM categories WHERE shortDescription LIKE :partOfDescription ORDER BY name")
    public LiveData<List<Category>> loadCategoriesContainsInDescription(String partOfDescription);

    @Query("SELECT * FROM categories WHERE name LIKE :likeName ORDER BY name")
    public LiveData<List<Category>> loadCategories(String likeName);

    @Query("SELECT * FROM categories WHERE id = :id")
    public LiveData<Category> loadCategory(int id);

    @Query("SELECT COUNT(*) FROM categories WHERE name LIKE 'Default Category'")
    public int countDefaultCategories();
}
