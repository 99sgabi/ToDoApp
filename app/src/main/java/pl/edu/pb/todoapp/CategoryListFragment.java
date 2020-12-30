package pl.edu.pb.todoapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import pl.edu.pb.todoapp.database.Category;
import pl.edu.pb.todoapp.database.CategoryViewModel;

public class CategoryListFragment extends Fragment {
    private RecyclerView recyclerView;
    private CategoryAdapter adapter;
    private FloatingActionButton addButton;
    private CategoryViewModel categoryViewModel;
    public static final int REQUEST_CODE_CATEGORY_EDIT = 10;
    public static final int REQUEST_CODE_CATEGORY_CREATE = 11;
    public static final String KEY_CATEGORY_ID = "Category_Id";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        categoryViewModel = ViewModelProviders.of(this).get(CategoryViewModel.class);
        categoryViewModel.findAllCategories().observe(this, new Observer<List<Category>>() {
            @Override
            public void onChanged(List<Category> categories) {
                adapter.setCategories(categories);
            }
        });
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.rv_list, container,false);
        recyclerView = view.findViewById(R.id.task_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        addButton = view.findViewById(R.id.floating_add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CreateCategoryActvity.class);
                startActivityForResult(intent,REQUEST_CODE_CATEGORY_CREATE);
            }
        });

        if(adapter == null)
        {
            adapter = new CategoryAdapter();
            recyclerView.setAdapter(adapter);
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(adapter == null)
        {
            adapter = new CategoryAdapter();
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.categories_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.show_tasks:
                //go back to previous activity
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_CATEGORY_CREATE)
        {
            if(resultCode == Activity.RESULT_OK) {
                Snackbar.make(getActivity().findViewById(R.id.coordinator_layout),
                        getString(R.string.category_created),
                        Snackbar.LENGTH_LONG).show();
            }
            else
                Snackbar.make(getActivity().findViewById(R.id.coordinator_layout),
                        getString(R.string.empty_category_cancelled),
                        Snackbar.LENGTH_LONG).show();
        }
        else if(requestCode == REQUEST_CODE_CATEGORY_EDIT)
        {
            if(resultCode == Activity.RESULT_OK) {
                Snackbar.make(getActivity().findViewById(R.id.coordinator_layout),
                        getString(R.string.category_edit_was_successful),
                        Snackbar.LENGTH_LONG).show();
            }
            else
                Snackbar.make(getActivity().findViewById(R.id.coordinator_layout),
                        getString(R.string.category_edit_was_unsuccessful),
                        Snackbar.LENGTH_LONG).show();
        }
    }

    private void createPopUpMenu(View v, Category category)
    {
        PopupMenu popup = new PopupMenu(getActivity(), v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.single_task_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch(item.getItemId())
                {
                    case R.id.menu_delete_task:
                        showDeleteAlert(category);
                        return true;
                    case R.id.menu_edit_task:
                        Intent editIntent = new Intent(getActivity(), CreateCategoryActvity.class);
                        editIntent.putExtra(KEY_CATEGORY_ID, category.getId());
                        startActivityForResult(editIntent, REQUEST_CODE_CATEGORY_EDIT);
                        return true;
                    default:
                        return false;
                }
            }
        });
        popup.show();
    }

    private void showDeleteAlert(Category category)
    {
        //shows alert message to delete the category
        new AlertDialog.Builder(getContext())
                .setTitle(getString(R.string.delete_category_title))
                .setMessage(getString(R.string.delete_category_confirmation))
                .setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        categoryViewModel.delete(category);
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { }
                }).show();
    }

    private class CategoryHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnClickListener
    {
        private Category category;
        private TextView nameTextView;
        private TextView descriptionTextView;
        private ImageView icon;

        public CategoryHolder(LayoutInflater inflater, ViewGroup parent)
        {
            super(inflater.inflate(R.layout.list_item_category, parent, false));
            itemView.setOnLongClickListener(this);
            itemView.setOnClickListener(this);

            nameTextView = itemView.findViewById(R.id.category_item_name);
            descriptionTextView = itemView.findViewById(R.id.category_item_short_description);
            icon = itemView.findViewById(R.id.category_image);
        }

        public void bind(Category category)
        {
            this.category = category;
            nameTextView.setText(category.getName());
            descriptionTextView.setText(category.getShortDescription());
            if(category.getPhotoPath() != null)
            {
                Bitmap picture = BitmapFactory.decodeFile(category.getPhotoPath());
                icon.setImageBitmap(picture);
                /*Bitmap picture;
                try {
                    Uri pictureUri = Uri.parse(category.getPhotoPath());
                    picture = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(pictureUri));
                    icon.setImageBitmap(picture);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }*/
                /*Uri pictureUri = Uri.parse(category.getPhotoPath());
                icon.setImageURI(pictureUri);*/
            }
        }

        @Override
        public boolean onLongClick(View v) {
            createPopUpMenu(v, category);
            return false;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(),CategoryTasksListActivity.class);
            intent.putExtra(CategoryTasksListActivity.KEY_CATEGORY_ID,category.getId());
            startActivity(intent);
        }
    }

    private class CategoryAdapter extends RecyclerView.Adapter<CategoryHolder>
    {
        private List<Category> categories;

        @NonNull
        @Override
        public CategoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new CategoryHolder(layoutInflater,parent);
        }

        @Override
        public void onBindViewHolder(@NonNull CategoryHolder holder, int position) {
            holder.bind(categories.get(position));
        }

        @Override
        public int getItemCount() {
            if(categories == null) return 0;
            return categories.size();
        }

        public void setCategories(List<Category> categories)
        {
            this.categories = categories;
            notifyDataSetChanged();
        }

    }
}
