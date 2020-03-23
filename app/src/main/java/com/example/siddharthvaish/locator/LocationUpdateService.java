package com.example.siddharthvaish.locator;

import android.Manifest;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;


import android.telephony.SmsManager;
import android.util.Log;
import android.widget.TabHost;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.work.Constraints;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;


public class LocationUpdateService extends Service {
    private static final String TAG = "BOOMBOOMTESTGPS";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 10f;
    String cityLabel;
    List<ModelLocation> locationList;
    int locationInsideFlag = 0;
    ModelLocation dwellLocation;
    ModelLocation dwellLocationprev = null;
    String phoneNum = null;

    private class LocationListener implements android.location.LocationListener {
        Location mLastLocation;

        public LocationListener(String provider) {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            Log.e(TAG, "onLocationChanged: " + location);
            mLastLocation.set(location);
            insideLocation(location);
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e(TAG, "onStatusChanged: " + provider);
        }
    }

    public void getLocationsFromDB() {
        Log.e(TAG, "in db fun");
        locationList = new ArrayList<ModelLocation>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Location");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ModelLocation modelLocation = snapshot.getValue(ModelLocation.class);
                    locationList.add(modelLocation);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void insideLocation(Location location) {
        Log.e(TAG, "insideLocation");
        dwellLocation = null;
        for (int i = 0; i < locationList.size(); i++) {
            float[] results = new float[1];
            Location.distanceBetween(location.getLatitude(), location.getLongitude(), Double.parseDouble(locationList.get(i).getLatitude()), Double.parseDouble(locationList.get(i).getLongitude()), results);
            float distanceInMeters = results[0];
            Log.e(TAG, "distanceInMeters outside" + distanceInMeters);
            //Log.e(TAG,String.valueOf(locationList.get(i).getArea()));
            if (distanceInMeters <= Float.parseFloat(locationList.get(i).getArea())) {
                Log.e(TAG, "distanceInMeters inside" + distanceInMeters);
                dwellLocation = locationList.get(i);
                locationInsideFlag = 1;
            }
        }
        sendSMS(location);
    }

    public void sendSMS(final Location location) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("PhoneNumber").child("phNo");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    phoneNum = dataSnapshot.getValue().toString();
                    Log.e(TAG,"Phone Number"+phoneNum);
                    if (phoneNum != null) {
                        if (dwellLocation != null && locationInsideFlag == 1) {
                            Log.e(TAG, "sendSMS inside dwell");
                            if (dwellLocationprev == null) {
                                dwellLocationprev = dwellLocation;
                                Log.e(TAG, "null if" + dwellLocationprev);
                                Toast.makeText(getApplicationContext(), "Inside location", Toast.LENGTH_LONG).show();
                                SmsManager smsManager = SmsManager.getDefault();
                                smsManager.sendTextMessage("+91"+phoneNum, null, "Reached at https://www.google.com/maps/search/?api=1&query=" + location.getLatitude() + "," + location.getLongitude(), null, null);
                            } else if (!(dwellLocation.equals(dwellLocationprev))) {
                                Log.e(TAG, "dwell not same as before" + dwellLocation);
                                dwellLocationprev = dwellLocation;
                                Toast.makeText(getApplicationContext(), "Inside location", Toast.LENGTH_LONG).show();
                                SmsManager smsManager = SmsManager.getDefault();
                                smsManager.sendTextMessage("+91"+phoneNum, null, "Reached at https://www.google.com/maps/search/?api=1&query=" + location.getLatitude() + "," + location.getLongitude(), null, null);
                            } else {
                                Log.e(TAG, "Same dwell" + dwellLocationprev);
                            }
                        } else if (dwellLocation == null && locationInsideFlag == 1) {
                            Log.e(TAG, "exit dwell" + dwellLocationprev);
                            locationInsideFlag = 0;
                            dwellLocationprev = null;
                            Toast.makeText(getApplicationContext(), "Outside location", Toast.LENGTH_LONG).show();
                            SmsManager smsManager = SmsManager.getDefault();
                            smsManager.sendTextMessage("+91"+phoneNum, null, "Left location https://www.google.com/maps/search/?api=1&query=" + location.getLatitude() + "," + location.getLongitude(), null, null);
                        } else {
                            Log.e(TAG, "sendSMS else");

                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate");
        getLocationsFromDB();
        initializeLocationManager();
        try {
            if (ContextCompat.checkSelfPermission(LocationUpdateService.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                mLocationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                        mLocationListeners[1]);
            }
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }
        Constraints constraints = new Constraints.Builder()
                .setRequiresCharging(true)
                .build();

        PeriodicWorkRequest saveRequest = new PeriodicWorkRequest.Builder(UploadWorker.class, 100, TimeUnit.MILLISECONDS)
                .setConstraints(constraints)
                .build();

        WorkManager.getInstance()
                .enqueue(saveRequest);
    }

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }


}