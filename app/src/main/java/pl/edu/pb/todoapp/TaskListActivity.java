package pl.edu.pb.todoapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import pl.edu.pb.todoapp.weather.ClosesedCity;
import pl.edu.pb.todoapp.weather.Weather;
import pl.edu.pb.todoapp.weather.WeatherApiInstance;
import pl.edu.pb.todoapp.weather.WeatherList;
import pl.edu.pb.todoapp.weather.WeatherService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TaskListActivity extends SingleFragmentActivity implements SensorEventListener{
    private ClosesedCity closesedCity;
    private TextView temperatureMessageBox;
    private TextView locationMessageBox;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private SensorManager sensorManager;
    private Sensor temperatureSensor;
    public static String CHANNEL_NOTIFICATION_ID = "channel notification id";

    private void createNotificationChannel() {
    // Create the NotificationChannel, but only on API 26+ because
    // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.notification_channel_name);
            String description = getString(R.string.notification_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_NOTIFICATION_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    private void findLocation()
    {
        //checking permissions
        if(ActivityCompat.checkSelfPermission(
                TaskListActivity.this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            //when permission is granted
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    //initialize location
                    Location location = task.getResult();
                    if(location != null)
                    {
                        double longitude = location.getLongitude();
                        double latitude = location.getLatitude();
                        String query = String.format(Locale.US, "%.4f", latitude) + "," + String.format(Locale.US, "%.4f", longitude);
                        //String query = Double.toString(latitude) + "," + Double.toString(longitude);
                        findClosesedCity(query);
                    }
                }
            });
        }
        else
        {
            ActivityCompat.requestPermissions(TaskListActivity.this ,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    44);
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
            locationMessageBox.setText(getString(R.string.lightly_motivated_message));
        else if(categoryMotivatedCount == maxValue)
            locationMessageBox.setText(getString(R.string.motivated_message));
        else
            locationMessageBox.setText(getString(R.string.unmotivated_message));
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
        createNotificationChannel();
        setContentView(R.layout.task_list_activity);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationMessageBox = findViewById(R.id.message_for_the_day);
        temperatureMessageBox = findViewById(R.id.message_for_the_day_temp);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        temperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);

        findLocation();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(temperatureSensor != null)
        {
            sensorManager.registerListener(this, temperatureSensor,
                        SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(temperatureSensor != null)
        {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    protected Fragment createFragment() {
        return new TaskListFragment();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int sensorType = event.sensor.getType();
        float currentTemperature = event.values[0];
        switch (sensorType)
        {
            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                if(currentTemperature > 15.0 && currentTemperature < 22.5)
                    temperatureMessageBox.setText(R.string.message_temperature_warm);
                else
                    temperatureMessageBox.setText(R.string.message_temperature_rest);
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}