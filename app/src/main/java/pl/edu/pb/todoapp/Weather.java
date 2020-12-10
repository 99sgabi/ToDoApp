package pl.edu.pb.todoapp;

import com.google.gson.annotations.SerializedName;

public class Weather {
    @SerializedName("weather_state_abbr")
    public String weatherState;

    @SerializedName("woeid")
    public long woeid;
}
