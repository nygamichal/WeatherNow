package pl.nygamichal.weathernow;

import android.app.Application;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Admin on 08.08.2017.
 */
//class stanowiaca glowny element dla calej applikacji
public class WeatherNowApp extends Application {
    private static final String BASE_URL = "http://api.openweathermap.org/data/2.5/";
    private static final String API_KEY = "df575cf63177bd4fd5e64946b33b89b1";

    Retrofit retrofit;
    public static WeatherApi weatherApi;

    @Override
    public void onCreate() {
        super.onCreate();
        //zapytania przy uzyciu retrofita
        retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        weatherApi = retrofit.create(WeatherApi.class);
    }
}
