package pl.edu.pb.todoapp;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;

import java.io.Serializable;
import java.util.Date;

import dalvik.annotation.TestTarget;



@Entity(tableName = "tasks",
        foreignKeys = @ForeignKey(entity = Category.class,
                parentColumns = "id",
                childColumns = "categoryId",
                onDelete = ForeignKey.CASCADE))
public class Task implements Serializable{
    @PrimaryKey(autoGenerate = true)
    private Integer id;
    private String name;
    private String description;
    private boolean done;
    private Date date;
    private Priority priority;
    private boolean notifyUser;

    @ColumnInfo(index = true)
    private Integer categoryId;

    public Task()
    {
        priority = Priority.LOW;
        done = false;
    }

    public Integer getId()
    {
        return id;
    }

    public Integer getCategoryId()
    {
        return categoryId;
    }

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }

    public boolean getDone()
    {
        return done;
    }

    public Date getDate()
    {
        return date;
    }

    public Priority getPriority()
    {
        return priority;
    }

    public boolean getNotifyUser()
    {
        return notifyUser;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public void setDate(Date date)
    {
        this.date = date;
    }

    public void setDone(boolean done)
    {
        this.done = done;
    }

    public void setPriority(Priority priority)
    {
        this.priority = priority;
    }

    public void setCategoryId(Integer id)
    {
        this.categoryId = id;
    }

    public void setNotifyUser(boolean n)
    {
        this.notifyUser = n;
    }

    public static class Converter {
        @TypeConverter
        public Priority fromStringToPriority(String priority) {
            if (priority.isEmpty())
                return Priority.LOW;
            if(priority.equals("high"))
                return Priority.HIGH;
            if(priority.equals("medium"))
                return Priority.MEDIUM;
            return Priority.LOW;
        }

        @TypeConverter
        public String fromPriorityToString(Priority priority) {
            if(priority == Priority.HIGH)
                return "high";
            if(priority == Priority.MEDIUM)
                return "medium";
            return "low";
        }

        @TypeConverter
        public Date fromTimestamp(Long value) {
            return value == null ? null : new Date(value);
        }

        @TypeConverter
        public Long dateToTimestamp(Date date) {
            if (date == null) {
                return null;
            } else {
                return date.getTime();
            }
        }
    }
}
