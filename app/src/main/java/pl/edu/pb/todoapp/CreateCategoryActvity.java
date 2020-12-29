package pl.edu.pb.todoapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CreateCategoryActvity extends AppCompatActivity {

    Button activityButton;
    Button loadButton;
    Button capturePictureButton;
    EditText nameEditText;
    EditText descriptionEditText;
    ImageView categoryIcon;
    CategoryViewModel categoryViewModel;
    Category currentCategory;
    boolean categoryExists;
    public static int REQUEST_LOAD_PICTURE = 0;
    public static final int REQUEST_CAPTURE_PICTURE = 1;
    public static final int REQUEST_EXTERNAL_STORAGE_PERMISSIONS = 122;
    public static final int REQUEST_CAMERA_PERMISSIONS = 124;

    public void checkStoragePermissions()
    {
        if (ActivityCompat.checkSelfPermission(CreateCategoryActvity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            loadButton.setEnabled(false);
            ActivityCompat.requestPermissions(
                    CreateCategoryActvity.this,
                    new String[] {
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    },
                    REQUEST_EXTERNAL_STORAGE_PERMISSIONS
            );
        }
    }

    public void checkCameraPermissions()
    {
        if (ActivityCompat.checkSelfPermission(CreateCategoryActvity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            capturePictureButton.setEnabled(false);
            ActivityCompat.requestPermissions(
                    CreateCategoryActvity.this,
                    new String[] {
                            Manifest.permission.CAMERA
                    },
                    REQUEST_CAMERA_PERMISSIONS
            );
        }
    }


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
        categoryViewModel.insert(prepareCategory(currentCategory));
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
            Bitmap picture = BitmapFactory.decodeFile(currentCategory.getPhotoPath());
            categoryIcon.setImageBitmap(picture);
            activityButton.setText(R.string.edit_category_button);
            categoryExists = true;
        }
        else
            currentCategory = new Category();
    }

    private void setActivityButtonListener()
    {
        activityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean result;
                if(categoryExists)
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

    private void setLoadButtonListener()
    {
        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_LOAD_PICTURE);
            }
        });
    }

    private File preparePictureFile()
    {
        String timeStamp = Long.toString(System.currentTimeMillis());
        String fileName = "PICTURE_" + timeStamp + "_";
        //TODO: poprawić to bo to jest depricated
        File storageDir = getFilesDir();//getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File picture = null;
        try {
            picture = File.createTempFile(fileName, ".jpg", storageDir);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Save a file: path for use with ACTION_VIEW intents
        currentCategory.setPhotoPath(picture.getAbsolutePath());
        return picture;
    }

    private void invokeTakePictureIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            File pictureFile = null;
            pictureFile = preparePictureFile();

            // Continue only if the File was successfully created
            if (pictureFile != null) {
                Uri pictureUri = FileProvider.getUriForFile(this, "pl.edu.pb.todoapp", pictureFile);
                //currentCategory.setPhotoPath(pictureUri.toString());
                intent.putExtra(MediaStore.EXTRA_OUTPUT, pictureUri);
                startActivityForResult(intent, REQUEST_CAPTURE_PICTURE);
            }
        }
    }

    private void galleryAddPic() {
        //TODO: zrobić żeby działąło
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentCategory.getPhotoPath());
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private void setCapturePictureButtonListener()
    {
        capturePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                invokeTakePictureIntent();
            }
        });
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
                currentCategory = category;
                setFields();
            }
        });

        activityButton = findViewById(R.id.create_category_button);
        nameEditText = findViewById(R.id.category_name);
        descriptionEditText = findViewById(R.id.category_short_description);
        categoryViewModel = ViewModelProviders.of(this).get(CategoryViewModel.class);
        activityButton.setText(R.string.create_category_button);
        loadButton = findViewById(R.id.load_picture_button);
        capturePictureButton = findViewById(R.id.take_picture_button);
        categoryIcon = findViewById(R.id.category_icon_create);
        checkStoragePermissions();
        checkCameraPermissions();

        setActivityButtonListener();
        setLoadButtonListener();
        setCapturePictureButtonListener();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSIONS:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    capturePictureButton.setEnabled(true);
                }
                else
                {
                    capturePictureButton.setEnabled(false);
                    capturePictureButton.setBackgroundColor(
                            getResources().getColor(R.color.buttonDisabledBackground));
                    capturePictureButton.setTextColor(
                            getResources().getColor(R.color.buttonDisabledText));
                    Snackbar.make(findViewById(R.id.main_layout),
                            getString(R.string.unable_to_capture_picture),
                            Snackbar.LENGTH_LONG).show();
                }
                return;
            case REQUEST_EXTERNAL_STORAGE_PERMISSIONS:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    loadButton.setEnabled(true);
                }
                else
                {
                    loadButton.setEnabled(false);
                    loadButton.setBackgroundColor(
                            getResources().getColor(R.color.buttonDisabledBackground));
                    loadButton.setTextColor(
                            getResources().getColor(R.color.buttonDisabledText));
                    Snackbar.make(findViewById(R.id.main_layout),
                        getString(R.string.unable_to_load_picture),
                        Snackbar.LENGTH_LONG).show();
                }
                return;
        }
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == REQUEST_LOAD_PICTURE && resultCode == RESULT_OK)
        {
            Uri pictureUri = data.getData();//this one generates a correct path i guess
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(pictureUri,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String path = cursor.getString(columnIndex);
            cursor.close();
            /*File file = new File(path);
            if (file.exists()) {
                categoryIcon.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
                currentCategory.setPhotoPath(path);

            }*/

            categoryIcon.setImageBitmap(BitmapFactory.decodeFile(path));
            currentCategory.setPhotoPath(path);
        }
        if(requestCode == REQUEST_CAPTURE_PICTURE && resultCode == RESULT_OK)
        {
            Bitmap picture = BitmapFactory.decodeFile(currentCategory.getPhotoPath());
            categoryIcon.setImageBitmap(picture);
            galleryAddPic();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}