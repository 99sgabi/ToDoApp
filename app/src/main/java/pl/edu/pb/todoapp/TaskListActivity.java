package pl.edu.pb.todoapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class TaskListActivity extends SingleFragmentActivity {
    private ClosesedCity closesedCity;
    private TextView messageBox;
    private FusedLocationProviderClient fusedLocationProviderClient;

    private void findLocation()
    {
        //checking permissions
        if(ActivityCompat.checkSelfPermission(TaskListActivity.this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            //when permission is granted
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    //initialize location
                    Location location = task.getResult();
                    if(location != null)
                    {
                        // TODO: make sure String is correct . i , zwlaszcza
                        double longitude = location.getLongitude();
                        double latitude = location.getLatitude();
                        String query = Double.toString(latitude) + "," + Double.toString(longitude);
                        findClosesedCity(query);
                    }
                }
            });
        }
        else
        {
            ActivityCompat.requestPermissions(TaskListActivity.this , new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
    }

    private void findClosesedCity(String query)
    {
        //String query = "53.08,23.08";
        WeatherService weatherService = WeatherApiInstance.getInstance().create(WeatherService.class);
        Call<List<ClosesedCity>> weatherApiCall = weatherService.findClosesedCities(query);

        weatherApiCall.enqueue(new Callback<List<ClosesedCity>>() {
            @Override
            public void onResponse(Call<List<ClosesedCity>> call, Response<List<ClosesedCity>> response) {
                closesedCity = response.body().get(0);
                    showMessage();
            }

            @Override
            public void onFailure(Call<List<ClosesedCity>> call, Throwable t) {

            }
        });
    }

    private void generateMessage(List<Weather> weatherList)
    {
        ArrayList<String> category_unmotivated = new ArrayList<String>();
        ArrayList<String> category_motivated = new ArrayList<String>();
        ArrayList<String> category_lightly_motivated = new ArrayList<String>();
        category_unmotivated.add("sl");
        category_unmotivated.add("h");
        category_unmotivated.add("t");
        category_unmotivated.add("hc");
        category_lightly_motivated.add("hr");
        category_lightly_motivated.add("lr");
        category_lightly_motivated.add("s");
        category_motivated.add("lc");
        category_motivated.add("sn");
        category_motivated.add("c");

        int categoryUnmotivatedCount = 0;
        int categoryMotivatedCount = 0;
        int categoryLightlymotivatedCount = 0;

        for(Weather weather: weatherList)
        {
            if(category_motivated.contains(weather.weatherState))
            {
                categoryMotivatedCount++;
            }
            else if(category_unmotivated.contains(weather.weatherState))
            {
                categoryUnmotivatedCount++;
            }
            else
            {
                categoryLightlymotivatedCount++;
            }
        }

        int maxValue = Math.max(Math.max(categoryLightlymotivatedCount,categoryMotivatedCount),categoryUnmotivatedCount);
        if(categoryLightlymotivatedCount == maxValue)
            messageBox.setText(getString(R.string.lightly_motivated_message));
        else if(categoryMotivatedCount == maxValue)
            messageBox.setText(getString(R.string.motivated_message));
        else
            messageBox.setText(getString(R.string.unmotivated_message));
    }

    private void showMessage()
    {
        WeatherService weatherService = WeatherApiInstance.getInstance().create(WeatherService.class);
        Call<WeatherList> weatherApiCall = weatherService.findWeather(closesedCity.woeid);

        weatherApiCall.enqueue(new Callback<WeatherList>() {
            @Override
            public void onResponse(Call<WeatherList> call, Response<WeatherList> response) {
                generateMessage(response.body().weatherList);
            }

            @Override
            public void onFailure(Call<WeatherList> call, Throwable t) {

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startUp();
        setContentView(R.layout.task_list_activity);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
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