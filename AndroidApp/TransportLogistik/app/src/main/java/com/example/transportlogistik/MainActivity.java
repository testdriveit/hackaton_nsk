package com.example.transportlogistik;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener{
    //views
    private Button buttonStartTracking;
    private Button buttonStopTracking;

    //Клиент GoogleApi
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;

    //TrackingLogger
    private TrackingLogger trackingLogger;

    //Название файла с параметрами
    private final String APP_PREFERENCES = "logistik";
    private final String VEHICLE_ID = "vehicleId";
    //Работа с сохранением настроек, данных
    private SharedPreferences sharedPreferences;

    private int vehicleId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Random rnd = new Random();

        int randomNum = rnd.nextInt( 1000 + 1);

        sharedPreferences = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor ed = sharedPreferences.edit();
        vehicleId = sharedPreferences.getInt(VEHICLE_ID, randomNum);
        ed.putInt(VEHICLE_ID, vehicleId);
        ed.apply();

        trackingLogger =  new TrackingLogger(vehicleId);

        findAllViews();
        buildGoogleApiClient();
        initLocationRequest();

        googleApiClient.connect();
    }

    private void findAllViews(){
        buttonStartTracking = (Button) findViewById(R.id.buttonStartTracking);
        buttonStopTracking = (Button) findViewById(R.id.buttonStopTracking);

        buttonStartTracking.setOnClickListener(this);
        buttonStopTracking.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buttonStartTracking:
                startLocationUpdate();
                Toast.makeText(this, "Трекинг запущен", Toast.LENGTH_SHORT).show();
                break;
            case R.id.buttonStopTracking:
                stopLocationUpdates();
                Toast.makeText(this, "Трекинг остановлен", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        trackingLogger.Log(Double.toString(location.getLatitude()),
                Double.toString(location.getLongitude()));
    }

    private synchronized void buildGoogleApiClient(){
        googleApiClient = new GoogleApiClient.Builder(this)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
    }

    private void startLocationUpdate() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    private void initLocationRequest() {
        locationRequest = new LocationRequest();
        setLocationRequestIntervals(5000, 2000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void setLocationRequestIntervals(long interval, long fastestInterval){
        locationRequest.setInterval(interval);
        locationRequest.setFastestInterval(fastestInterval);
    }

    private void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        stopLocationUpdates();
        googleApiClient.disconnect();
    }
}
