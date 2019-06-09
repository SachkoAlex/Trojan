package com.example.virus.Services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;
import android.os.StrictMode;

import com.example.virus.Receivers.Alarm;

import java.io.IOException;
import java.io.PrintWriter;

public class MainService extends Service {

    public MainService() {

    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public void onCreate()
    {
        Alarm.set(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

            getLastLocation(this);

        return Service.START_STICKY;
    }


    public Location getLastLocation(Context context) throws SecurityException {

            LocationManager lManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

            Location locationGPS = lManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location locationNet = lManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            long GPSLocationTime = 0;
            if (null != locationGPS) {
                GPSLocationTime = locationGPS.getTime();
            }

            long NetLocationTime = 0;
            if (null != locationNet) {
                NetLocationTime = locationNet.getTime();
            }

            Location loc;
            if (0 < GPSLocationTime - NetLocationTime) {
                loc = locationGPS;
            } else {
                loc = locationNet;
            }

        String locationFile = this.getApplicationInfo().dataDir + "/location";
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            try {

                PrintWriter pw = new PrintWriter(locationFile);
                pw.write(loc.getLatitude() + " " + loc.getLongitude());
                pw.close();

            } catch (IOException e) {
            }
            if (loc != null) {
                return loc;
            } else {
                return null;
            }


    }

    private boolean checkPermission()
    {
        String permission = android.Manifest.permission.ACCESS_FINE_LOCATION;
        int res = this.getBaseContext().checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }
}
