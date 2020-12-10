package pl.edu.pb.todoapp;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WeatherList {
    @SerializedName("consolidated_weather")
    public List<Weather> weatherList;
}
