package pl.edu.pb.todoapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class TaskListActivity extends SingleFragmentActivity {
    private ClosesedCity closesedCity;
    private TextView messageBox;

    private void findLocation()
    {
        //TODO: use here GPS or sth like that
        String query = "53.08,23.08";
        WeatherService weatherService = WeatherApiInstance.getInstance().create(WeatherService.class);
        Call<List<ClosesedCity>> weatherApiCall = weatherService.findClosesedCities(query);

        weatherApiCall.enqueue(new Callback<List<ClosesedCity>>() {
            @Override
            public void onResponse(Call<List<ClosesedCity>> call, Response<List<ClosesedCity>> response) {
                closesedCity = response.body().get(0);
                messageBox.setText(closesedCity.city);
            }

            @Override
            public void onFailure(Call<List<ClosesedCity>> call, Throwable t) {

            }
        });
    }

    private void showMessage()
    {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startUp();
        setContentView(R.layout.task_list_activity);
        messageBox = findViewById(R.id.message_for_the_day);
        findLocation();
    }

    @Override
    protected Fragment createFragment() {
        return new TaskListFragment();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}