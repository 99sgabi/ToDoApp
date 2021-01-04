package pl.edu.pb.todoapp.database;

import androidx.annotation.NonNull;

import pl.edu.pb.todoapp.R;

public enum Priority {
    LOW(R.string.low_priority),
    MEDIUM(R.string.medium_priority),
    HIGH(R.string.high_priority);

    int Rid;

    public int getRId()
    {
        return Rid;
    }

    Priority(int rid) {
        this.Rid = rid;
    }
}
