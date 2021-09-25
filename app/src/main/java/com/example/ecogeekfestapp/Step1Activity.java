package com.example.ecogeekfestapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.ecogeekfestapp.apis.APIHelper;
import com.example.ecogeekfestapp.model.RainReport;
import com.example.ecogeekfestapp.model.WeatherReport;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Step1Activity extends AppCompatActivity {

    private LocationManager locationManager;
    private double lat;
    private double lng;
    private float phValue;
    private float temp;
    private float humidity;

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

    private EditText phValueEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_1);

        getLocationPermission();

        phValueEditText = findViewById(R.id.phValue);

        findViewById(R.id.btnNext).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickNext();
            }
        });
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

        if (location != null) {
            Log.d("----", "getLocation: " + location.getLatitude());
            Log.d("-----", "getLocation: " + location.getLongitude());
            lat = location.getLatitude();
            lng = location.getLongitude();
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

    private void onClickNext() {
        phValue = getPhValue();
        if (phValue >= 0 && phValue <= 14) {
            getOtherData();
        } else {
            Toast.makeText(this, R.string.invalid_ph_value, Toast.LENGTH_LONG).show();
        }
    }

    private float getPhValue() {
        String str = phValueEditText.getText().toString().trim();
        if (!str.isEmpty()) {
            try {
                return Float.parseFloat(str);
            } catch (NumberFormatException ignored) {
            }
        }
        return -1;
    }

    private void getOtherData() {
        APIHelper.getWeatherAPIs().getWeatherReport(lat, lng)
                .enqueue(new Callback<WeatherReport>() {
                    @Override
                    public void onResponse(Call<WeatherReport> call, Response<WeatherReport> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            WeatherReport report = response.body();
                            temp = report.getMain().getTemp();
                            humidity = report.getMain().getHumidity();

                            getRainData();
                        }
                    }

                    @Override
                    public void onFailure(Call<WeatherReport> call, Throwable t) {
                        Toast.makeText(Step1Activity.this, "Fail to get temperature and humidity",
                                Toast.LENGTH_LONG).show();
                        Log.d("------", "onFailure: error");
                        t.printStackTrace();
                    }
                });
    }

    private void getRainData() {
        String country = getResources().getConfiguration().locale.getISO3Country();
        APIHelper.getRainDataAPI().getRainReport(country)
                .enqueue(new Callback<List<RainReport>>() {
                    @Override
                    public void onResponse(Call<List<RainReport>> call, Response<List<RainReport>> response) {
                        double rain = 0;
                        if (response.isSuccessful() && response.body() != null) {
                            List<RainReport> list = response.body();
                            if (!list.isEmpty()) {
                                RainReport rainReport = list.get(0);
                                if (rainReport != null && rainReport.getAnnualData() != null &&
                                        !rainReport.getAnnualData().isEmpty()) {
                                    rain = rainReport.getAnnualData().get(0);
                                }
                            }
                        }
                        callAPItoGetPlantSuggestions(phValue, rain, temp, humidity);
                    }

                    @Override
                    public void onFailure(Call<List<RainReport>> call, Throwable t) {
                        Toast.makeText(Step1Activity.this, "Fail to get rain data",
                                Toast.LENGTH_LONG).show();
                        Log.d("------", "onFailure: error");
                        t.printStackTrace();
                    }
                });
    }

    private void callAPItoGetPlantSuggestions(double phValue, double rain, double temp, double humidity) {
        Log.d("------", "callAPItoGetPlantSuggestions() called with: phValue = [" + phValue + "], rain = [" + rain + "], temp = [" + temp + "], humidity = [" + humidity + "]");
    }
}
