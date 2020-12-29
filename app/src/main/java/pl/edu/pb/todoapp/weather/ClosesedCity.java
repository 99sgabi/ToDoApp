package pl.edu.pb.todoapp.weather;

import com.google.gson.annotations.SerializedName;

public class ClosesedCity {
    @SerializedName("title")
    public String city;

    @SerializedName("woeid")
    public long woeid;
}
