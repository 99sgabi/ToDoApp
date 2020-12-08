package pl.edu.pb.todoapp;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "categories")
public class Category {
    @PrimaryKey(autoGenerate = true)
    private Integer id;
    private String name;
    private String shortDescription;

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
}