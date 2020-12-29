package pl.edu.pb.todoapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import java.util.List;

import pl.edu.pb.todoapp.database.Category;
import pl.edu.pb.todoapp.database.CategoryViewModel;

public class CategoryTasksListActivity extends SingleFragmentActivity {

    private TextView categoryNameField;
    private TextView categoryDescriptionField;
    private int categoryId;
    public static final String KEY_CATEGORY_ID = "Category_id";
    private CategoryViewModel categoryViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_tasks_list);
        categoryNameField = findViewById(R.id.category_name);
        categoryDescriptionField = findViewById(R.id.category_description);

        Intent intent = getIntent();
        categoryId = intent.getIntExtra(KEY_CATEGORY_ID, -1);
        if(categoryId == -1)
            finish();

        categoryViewModel = ViewModelProviders.of(this).get(CategoryViewModel.class);
        categoryViewModel.loadCategoryById(categoryId).observe(this, new Observer<Category>() {
            @Override
            public void onChanged(Category category) {
                categoryNameField.setText(
                        getResources().getString(
                                R.string.category_list_cat_name,
                                category.getName()
                        )
                );
                categoryDescriptionField.setText(
                        getResources().getString(
                                R.string.category_list_cat_description,
                                category.getShortDescription()
                        )
                );
            }
        });

        startUp();
    }

    @Override
    protected Fragment createFragment() {
        TaskListFragment taskListFragment = new TaskListFragment();
        taskListFragment.setCategoryId(categoryId);
        return taskListFragment;
    }
}