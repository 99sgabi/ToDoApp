package pl.edu.pb.todoapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class CreateCategoryActvity extends AppCompatActivity {

    Button activityButton;
    EditText nameEditText;
    EditText descriptionEditText;
    CategoryViewModel categoryViewModel;
    Category currentCategory;

    private Category prepareCategory(Category category)
    {
        //add icon
        String name = nameEditText.getText().toString();
        String description = descriptionEditText.getText().toString();

        category.setName(name);
        category.setShortDescription(description);

        return category;
    }

    private boolean checkIfEmpty()
    {
        String name = nameEditText.getText().toString();
        String description = descriptionEditText.getText().toString();

        if(name.isEmpty() || description.isEmpty())
        {
            return false;
        }
        return true;
    }

    private boolean addNewCategory()
    {
        if(!checkIfEmpty()) return false;
        categoryViewModel.insert(prepareCategory(new Category()));
        return true;
    }

    private boolean updateCategory(Category currentCategory)
    {
        if(!checkIfEmpty()) return false;
        categoryViewModel.update(prepareCategory(currentCategory));
        return true;
    }

    private void setFields()
    {
        if(currentCategory != null)
        {
            nameEditText.setText(currentCategory.getName());
            descriptionEditText.setText(currentCategory.getShortDescription());
            //icon
            activityButton.setText(R.string.edit_category_button);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_category_actvity);

        categoryViewModel = ViewModelProviders.of(this).get(CategoryViewModel.class);
        Intent startingIntent = getIntent();
        final int id = startingIntent.getIntExtra(CategoryListFragment.KEY_CATEGORY_ID, -1);
        categoryViewModel.loadCategoryById(id).observe(this, new Observer<Category>() {
            @Override
            public void onChanged(Category category) {
                //TODO: naprawić to2
                currentCategory = category;
                setFields();
            }
        });

        activityButton = findViewById(R.id.create_category_button);
        nameEditText = findViewById(R.id.category_name);
        descriptionEditText = findViewById(R.id.category_short_description);
        categoryViewModel = ViewModelProviders.of(this).get(CategoryViewModel.class);
        activityButton.setText(R.string.create_category_button);

        activityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean result;
                if(currentCategory != null)
                    result = updateCategory(currentCategory);
                else
                    result = addNewCategory();

                Intent replyIntent = new Intent();
                if(result == true)
                    setResult(Activity.RESULT_OK, replyIntent);
                else
                    setResult(Activity.RESULT_CANCELED, replyIntent);
                finish();
            }
        });
    }
}