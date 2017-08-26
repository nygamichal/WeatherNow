package pl.nygamichal.weathernow;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.nygamichal.weathernow.api.Weather;
import pl.nygamichal.weathernow.api.WeatherResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    public static final String TAG = MainActivity.class.getCanonicalName();

    @BindView(R.id.imageView)
    ImageView imageView;
    @BindView(R.id.textView)
    TextView textView;
    @BindView(R.id.mapsview)
    MapView mapsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mapsView.getMapAsync(this);
        mapsView.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapsView.onResume();
        WeatherNowApp.weatherApi.getWeather(WeatherNowApp.API_KEY,"Krakow,pl").enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                Log.d(TAG, "onResponse: ");
                if(response.isSuccessful())
                {
                    if(response.body() != null)
                    {
                        if(response.body().weather != null && response.body().weather.size() > 0)
                        {
                            Weather weather = response.body().weather.get(0);
                            if(textView != null)
                            {
                                textView.setText("CUrrent weather is " + weather.description);
                            }
                            if (imageView != null)
                            {
                                Picasso.with(MainActivity.this).load("http://openweathermap.org/img/w/" + weather.icon + ".png").into(imageView);
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Log.d(TAG, "onFailure: ");
            }
        });
    }

    @Override
    protected void onPause() {
        mapsView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mapsView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: ");
    }
}
