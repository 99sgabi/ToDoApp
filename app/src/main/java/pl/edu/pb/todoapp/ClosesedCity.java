package pl.edu.pb.todoapp;

import com.google.gson.annotations.SerializedName;

public class ClosesedCity {
    @SerializedName("title")
    public String city;

    @SerializedName("woeid")
    public long woeid;
}
