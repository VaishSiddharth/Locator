package com.example.siddharthvaish.locator;

import android.Manifest;
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

import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import io.nlopez.smartlocation.OnGeofencingTransitionListener;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.geofencing.model.GeofenceModel;
import io.nlopez.smartlocation.geofencing.utils.TransitionGeofence;


public class LocationUpdateService extends Service
{
    private static final String TAG = "BOOMBOOMTESTGPS";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 10f;
    String cityLabel;
    List<ModelLocation> locationList;
    List<GeofenceModel> geofenceModelList;

    private class LocationListener implements android.location.LocationListener
    {
        Location mLastLocation;

        public LocationListener(String provider)
        {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location)
        {
            Log.e(TAG, "onLocationChanged: " + location);
            mLastLocation.set(location);
            //insideLocation(location);
            //geofencing();
        }

        @Override
        public void onProviderDisabled(String provider)
        {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider)
        {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
            Log.e(TAG, "onStatusChanged: " + provider);
        }
    }
    public void getLocationsFromDB()
    {
        Log.e(TAG,"in db fun");
        locationList=new ArrayList<ModelLocation>();
        geofenceModelList=new ArrayList<GeofenceModel>();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Location");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    ModelLocation modelLocation=snapshot.getValue(ModelLocation.class);
                    if(modelLocation!=null)
                    {
                        locationList.add(modelLocation);
                        GeofenceModel geofenceModel = new GeofenceModel.Builder("idNo"+geofenceModelList.size())
                                .setTransition(Geofence.GEOFENCE_TRANSITION_ENTER|Geofence.GEOFENCE_TRANSITION_DWELL|Geofence.GEOFENCE_TRANSITION_EXIT)
                                .setLatitude(Double.parseDouble(modelLocation.getLatitude()))
                                .setLongitude(Double.parseDouble(modelLocation.getLongitude()))
                                .setRadius(Float.parseFloat(modelLocation.getArea()))
                                .build();
                        geofenceModelList.add(geofenceModel);
                    }
                }
                geofencing();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void insideLocation(Location location)
    {
        for (int i=0;i<locationList.size();i++)
        {
            float[] results = new float[1];
            Location.distanceBetween(location.getLatitude(),location.getLongitude(),Double.parseDouble(locationList.get(i).getLatitude()),Double.parseDouble(locationList.get(i).getLongitude()),results);
            float distanceInMeters = results[0];
            //Log.e(TAG,String.valueOf(locationList.get(i).getArea()));
            if(distanceInMeters<=Float.parseFloat(locationList.get(i).getArea()))
            {
                Toast.makeText(getApplicationContext(),"Inside location",Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(getApplicationContext(),"Not Inside location",Toast.LENGTH_LONG).show();
            }

        }
    }
    
    LocationListener[] mLocationListeners = new LocationListener[] {
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };
    public void geofencing()
    {
        Log.e(TAG,"geofence fun");
        /*for (int i=0;i<geofenceModelList.size();i++)
        {
            SmartLocation.with(getApplicationContext()).geofencing()
                    .add(geofenceModelList.get(i));
        }*/
        SmartLocation.with(getApplicationContext()).geofencing()
                .add(geofenceModelList.get(0))
                .add(geofenceModelList.get(1))
                .add(geofenceModelList.get(2))
                .start(new OnGeofencingTransitionListener() {
                    @Override
                    public void onGeofenceTransition(TransitionGeofence transitionGeofence) {
                        Log.e(TAG,"geofencing started");
                        if(transitionGeofence.getTransitionType()==Geofence.GEOFENCE_TRANSITION_ENTER)
                        {
                            Log.e(TAG,"Enter");
                            Toast.makeText(getApplicationContext(),"Entered Location",Toast.LENGTH_LONG).show();
                        }
                        else if(transitionGeofence.getTransitionType()==Geofence.GEOFENCE_TRANSITION_EXIT)
                        {
                            Log.e(TAG,"Exit");
                            Toast.makeText(getApplicationContext(),"Exited Location",Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            Log.e(TAG,"Dwell");
                            Toast.makeText(getApplicationContext(),"Everything Else",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    public IBinder onBind(Intent arg0)
    {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate()
    {
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
    public void onDestroy()
    {
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
    }

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }
    
    
    
}