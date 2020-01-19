package com.example.finalproject;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class Alert extends AppCompatActivity {

    TextView text1;
    Button button1;
    private static final String FILE_NAME = "final.txt";
    private String phoneNumber;
    private String emergencyMsg;

    private LocationManager locationManager;
    private LocationListener listener;
    private  double lat,lon;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);
        text1 = findViewById(R.id.alert);
        button1 = findViewById(R.id.countBtn);
        getNum();
        getGps();
        SmsManager smsManager = SmsManager.getDefault();
        //text1.setText("sending");
        //text1.append("\n " + lon + " " + lat);
        emergencyMsg = "EMERGENCY ALERT:\n Jane Doe needs your help urgently at http://maps.google.com/?q="+lat+","+lon;
        smsManager.sendTextMessage(phoneNumber, null, emergencyMsg, null, null);
        smsManager.sendTextMessage("97377450", null, emergencyMsg, null, null);
        smsManager.sendTextMessage("98557100", null, emergencyMsg, null, null);

    }

    public void toland(View v){
        Intent intent = new Intent(this, last.class);
        startActivity(intent);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void getGps() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);


        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //lon = location.getLongitude();
                //lat = location.getLatitude();
                text1.append("\n " + location.getLongitude() + " " + location.getLatitude());
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };

        updateGPS();

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void updateGPS() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}
                        , 10);
            }
            return;
        }
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates("gps", 5000, 0, listener);
        Location l =locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        lat = l.getLatitude();
        lon = l.getLongitude();
    }


    private void getNum() {
        FileInputStream fis = null;

        try {
            fis = openFileInput(FILE_NAME);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String text;

            while((text = br.readLine()) != null){
                sb.append(text).append("\n");

            }

            phoneNumber =sb.toString();


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(fis != null){
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
