package pl.edu.pb.todoapp.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "categories")
public class Category {
    @PrimaryKey(autoGenerate = true)
    private Integer id;
    private String name;
    private String shortDescription;

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    private String photoPath;

    public Integer getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public String getShortDescription()
    {
        return shortDescription;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setShortDescription(String shortDescription)
    {
        this.shortDescription = shortDescription;
    }

    @Override
    public String toString() {
        return name;
    }
}