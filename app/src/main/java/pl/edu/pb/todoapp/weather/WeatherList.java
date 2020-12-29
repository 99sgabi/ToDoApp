package pl.edu.pb.todoapp.weather;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import pl.edu.pb.todoapp.weather.Weather;

public class WeatherList {
    @SerializedName("consolidated_weather")
    public List<Weather> weatherList;
}
