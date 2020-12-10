package pl.edu.pb.todoapp;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface WeatherService {
    @GET("location/search/")
    Call<List<ClosesedCity>> findClosesedCities(@Query("lattlong") String query);

    @GET("location/{woeid}")
    Call<List<Weather>> findWeather(@Path("woeid") long woeid);
}
