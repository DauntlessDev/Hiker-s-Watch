package com.dauntlessdev.hikerswatvh;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    LocationManager locationManager;
    LocationListener locationListener;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 ){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,0, locationListener);
                }

            }
        }
    }

    @SuppressLint({"MissingPermission", "ObsoleteSdkInt"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                updateLocationInfo(location);

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if(Build.VERSION.SDK_INT < 23){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,0, locationListener);
        }
        else{
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            }else{
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,0, locationListener);
                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                updateLocationInfo(lastKnownLocation);

            }
        }


    }

    public void updateLocationInfo(Location location){

        TextView latitudeText = findViewById(R.id.latTextView);
        TextView longitudeText = findViewById(R.id.longTextView);
        TextView accuracyText = findViewById(R.id.accurTextView);
        TextView altitudeText = findViewById(R.id.altTextView);
        TextView addressText = findViewById(R.id.addressTextView);

        Log.i("Address", location.toString());

        latitudeText.setText("Latitude: " + location.getLatitude());
        longitudeText.setText("Longitude: " + location.getLongitude());
        accuracyText.setText("Accuracy: " + location.getAccuracy());
        altitudeText.setText("Altitude: " + location.getAltitude());

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        String address = "";
        try {
            List<Address> addressList = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            if (addressList != null && addressList.size() > 0){
                if (addressList.get(0).getThoroughfare() != null){
                    address += addressList.get(0).getThoroughfare() + ", ";
                }if (addressList.get(0).getPostalCode() != null){
                    address += addressList.get(0).getPostalCode() + ",\n";
                }if (addressList.get(0).getLocality() != null){
                    address += addressList.get(0).getLocality() + ", ";
                }if (addressList.get(0).getAdminArea() != null){
                    address += addressList.get(0).getAdminArea();
                }
            }

            if(!address.isEmpty()){
                addressText.setText("Address: \n" + address);
            }else{
                addressText.setText("Address: \nCould not find information");
            }

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "Can't Find Info of your Location", Toast.LENGTH_SHORT).show();
        }
    }
}
