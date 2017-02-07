package com.luiscbr.personal.panicbuttonandroidapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements LocationListener {
    private static final long TEN_SECONDS = 10 * 1000;
    private static final long FIVE_METRES = 5;
    private static final int PERMISSIONS_REQUEST_FINE_LOCATION = 1;
    private LocationManager locationManager;
    private String provider;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button mainButton = (Button) findViewById(R.id.mainButton);
        mainButton.setEnabled(false);
        mainButton.setVisibility(View.INVISIBLE);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        defineLocationProvider();

        mainButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mainButton.setText("Sending alert...");
                // Todo: Define how to send the location to the server
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_FINE_LOCATION);
            return;
        }
        locationManager.requestLocationUpdates(provider, TEN_SECONDS, FIVE_METRES, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationManager.removeUpdates(this);
        }

    }

    @Override
    public void onLocationChanged(Location location) {
        TextView waitingText = (TextView) findViewById(R.id.waitingText);
        waitingText.setVisibility(View.INVISIBLE);

        Button mainButton = (Button) findViewById(R.id.mainButton);
        mainButton.setEnabled(true);
        mainButton.setVisibility(View.VISIBLE);

        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        TextView latitudeText = (TextView) findViewById(R.id.latitudeText);
        latitudeText.setText(String.valueOf(latitude));
        TextView longitudeText = (TextView) findViewById(R.id.longitudeText);
        longitudeText.setText(String.valueOf(longitude));

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO: Discover when is this method triggered
    }

    @Override
    public void onProviderEnabled(String provider) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_FINE_LOCATION);
            return;
        }
        locationManager.requestLocationUpdates(provider, TEN_SECONDS, FIVE_METRES, this);
    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO: Discover when is this method triggered
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    defineLocationProvider();
                } else {
                    Toast.makeText(this, "Permission denied to your location. App Closed", Toast.LENGTH_LONG).show();
                    finish();
                }
                return;
            }
        }
    }

    public void defineLocationProvider(){
        Criteria criteria = new Criteria();
        criteria.setCostAllowed(false);
        criteria.setAltitudeRequired(false);
        criteria.setAccuracy(Criteria.ACCURACY_FINE);

        provider = locationManager.getBestProvider(criteria, true);
    }
}
