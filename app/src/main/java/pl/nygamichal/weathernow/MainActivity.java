package pl.nygamichal.weathernow;

import android.Manifest;
import android.content.pm.PackageManager;
import android.icu.util.TimeUnit;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.util.TimeUtils;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
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

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    public static final String TAG = MainActivity.class.getCanonicalName();

    @BindView(R.id.imageView)
    ImageView imageView;
    @BindView(R.id.textView)
    TextView textView;
    @BindView(R.id.mapsview)
    MapView mapsView;
    @BindView(R.id.swipeRefresh)
    SwipeRefreshLayout swipeRefresh;
    GoogleApiClient googleApiClient;
    GoogleMap googleMaps;
    LocationRequest locationRequest = com.google.android.gms.location.LocationRequest.create()
            .setInterval(500)
            .setFastestInterval(300)
            .setMaxWaitTime(1)
            .setPriority(com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        googleApiClient = new GoogleApiClient.Builder(MainActivity.this).addApi(LocationServices.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
        swipeRefresh.setEnabled(false);
        mapsView.getMapAsync(this);
        mapsView.onCreate(savedInstanceState);
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 123);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult: ");
        boolean isDeny = false;
        for (int result : grantResults) {
            if (result < 0) {
                isDeny = true;
            }
        }
        if (isDeny) {
            Toast.makeText(this, "Hey, we need this permision!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapsView.onResume();
        //getWeatherLocation("Krakow,pl");
    }

    private void getWeatherLocation(Location location) {
        if (swipeRefresh != null && swipeRefresh.isRefreshing())//jezeli nie jest dostpeny to pokazuje sie spiner u gory
        {
            swipeRefresh.setRefreshing(true);
        }
        WeatherNowApp.weatherApi.getWeather(WeatherNowApp.API_KEY, location.getLatitude(),location.getLongitude()).enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                Log.d(TAG, "onResponse: ");
                if (swipeRefresh != null)//jezeli nie jest dostpeny to pokazuje sie spiner u gory
                {
                    swipeRefresh.setRefreshing(false);
                }
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        if (response.body().weather != null && response.body().weather.size() > 0) {
                            Weather weather = response.body().weather.get(0);
                            if (textView != null) {
                                textView.setText("CUrrent weather is " + weather.description);
                            }
                            if (imageView != null) {
                                Picasso.with(MainActivity.this).load("http://openweathermap.org/img/w/" + weather.icon + ".png").into(imageView);
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Log.d(TAG, "onFailure: ");

                if (swipeRefresh != null)//jezeli nie jest dostpeny to pokazuje sie spiner u gory
                {
                    swipeRefresh.setRefreshing(false);
                }
                Toast.makeText(MainActivity.this, "Network problem", Toast.LENGTH_SHORT).show();

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
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, MainActivity.this);
        googleApiClient.disconnect();
        super.onDestroy();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: ");
        MainActivity.this.googleMaps = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        googleMap.setMyLocationEnabled(true);
        googleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, (LocationListener) MainActivity.this);
        Log.d(TAG, "onConnected: ");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended: ");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed: ");
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged: ");
        getWeatherLocation(location);
    }

}
