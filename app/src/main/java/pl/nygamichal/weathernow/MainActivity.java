package pl.nygamichal.weathernow;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import pl.nygamichal.weathernow.api.WeatherResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getCanonicalName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        WeatherNowApp.weatherApi.getWeather(WeatherNowApp.API_KEY,"Krakow,pl").enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                Log.d(TAG, "onResponse: ");
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Log.d(TAG, "onFailure: ");
            }
        });
    }
}
