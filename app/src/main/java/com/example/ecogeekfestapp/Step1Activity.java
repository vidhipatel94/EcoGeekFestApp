package com.example.ecogeekfestapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.ecogeekfestapp.apis.APIHelper;
import com.example.ecogeekfestapp.apis.WeatherAPIs;
import com.example.ecogeekfestapp.model.WeatherReport;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Step1Activity extends AppCompatActivity {

    private LocationManager locationManager;

//    private final LocationListener mLocationListener = new LocationListener() {
//        @Override
//        public void onLocationChanged(final Location location) {
//            //your code here
//            Log.d("--------", "onLocationChanged: "+location.toString());
//            Log.d("----", "onLocationChanged: "+location.getLatitude());
//            Log.d("-----", "onLocationChanged: "+location.getLongitude());
//
//
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_1);

        getLocationPermission();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            getLocation();
        }
    }

    private void getLocationPermission() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        boolean isNetworkEnabled = locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (isGPSEnabled && isNetworkEnabled) {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
            } else {
                getLocation();
            }
        }
//        else {
//            Log.d("-------", "getLocationPermission: not found");
//        }
    }

    private void getLocation() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(provider);

        if (location!=null) {
            Log.d("----", "getLocation: " + location.getLatitude());
            Log.d("-----", "getLocation: " + location.getLongitude());
            APIHelper.getWeatherAPIs().getWeatherReport(location.getLatitude(),location.getLongitude())
                    .enqueue(new Callback<WeatherReport>() {
                        @Override
                        public void onResponse(Call<WeatherReport> call, Response<WeatherReport> response) {
                            if (response.isSuccessful() && response.body()!=null) {
                                WeatherReport report = response.body();
                                float temp = report.getMain().getTemp();
                                float humidity = report.getMain().getHumidity();

                            }
                        }

                        @Override
                        public void onFailure(Call<WeatherReport> call, Throwable t) {
                            Toast.makeText(Step1Activity.this,"Fail to get temperature and humidity",
                                    Toast.LENGTH_LONG).show();
                            Log.d("------", "onFailure: error");
                            t.printStackTrace();
                        }
                    });
        }
//        else {
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60 * 1000,
//                100f, mLocationListener);
    }

//    private void getLocation() {
//            location = locationManager
//                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//
//            if (location != null) {
//                latitude = location.getLatitude();
//                longitude = location.getLongitude();
//            }
//        }
//    }
}
