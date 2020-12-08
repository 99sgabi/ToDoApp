package pl.edu.pb.todoapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;


public class CreateTaskActivity extends SingleFragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startUp();
    }

    public Fragment createFragment()
    {
        return new CreateTaskFragment();
    }
}