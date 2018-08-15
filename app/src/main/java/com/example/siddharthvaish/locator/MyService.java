package com.example.siddharthvaish.locator;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;

public class MyService extends Service
{
    private static final String TAG = "BOOMBOOMTESTGPS";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 10f;
    int flag=1;
    int f=0;
    String a11=null;
    String c11=null;
    String d11=null;
    String e11=null;
    int ph12=0;
    String ph11=null;
    String resulttemp = null;

    private class LocationListener implements android.location.LocationListener{
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
            Toast toast=new Toast(getApplicationContext());
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            Geocoder geocoder = new Geocoder(getApplicationContext());
            String result = null;
            String resulttemp1 = null;

            List<Address> addresses =
                    null;
            try {
                addresses = geocoder.getFromLocation(latitude, longitude,1);
                resulttemp1=addresses.get(0).getAddressLine(0);
                result = addresses.get(0).getSubLocality();
                LatLng latLng = new LatLng(latitude, longitude);
                if((result.equalsIgnoreCase(a11)==true)&&(flag==1)&&(result.equalsIgnoreCase(resulttemp1)==false)&&(a11!=null)) {
                    //Toast toast=new Toast(getApplicationContext());
                    toast.setGravity(Gravity.BOTTOM ,0,0);
                    toast.makeText(MyService.this,"Sending message",toast.LENGTH_LONG).show();
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(ph11, null, resulttemp1+" https://www.google.com/maps/search/?api=1&query="+latitude+","+longitude, null, null);
                    flag=0;
                    f=1;
                }
                else if ((result.equalsIgnoreCase(c11)==true)&& (flag==1)&&(result.equalsIgnoreCase(resulttemp1)==false)&&(c11!=null)){
                    //Toast toast=new Toast(getApplicationContext());
                    toast.setGravity(Gravity.BOTTOM ,0,0);
                    toast.makeText(MyService.this,"Sending message",toast.LENGTH_LONG).show();
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(ph11, null, resulttemp1+" https://www.google.com/maps/search/?api=1&query="+latitude+","+longitude, null, null);
                    flag=0;
                    f=1;
                }
                else if ((result.equalsIgnoreCase(d11)==true)&& (flag==1)&&(result.equalsIgnoreCase(resulttemp1)==false)&&(d11!=null)){
                    //Toast toast=new Toast(getApplicationContext());
                    toast.setGravity(Gravity.BOTTOM ,0,0);
                    toast.makeText(MyService.this,"Sending message",toast.LENGTH_LONG).show();
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(ph11, null, resulttemp1+" https://www.google.com/maps/search/?api=1&query="+latitude+","+longitude, null, null);
                    flag=0;
                    f=1;
                }
                else if ((result.equalsIgnoreCase(e11)==true)&& (flag==1)&&(result.equalsIgnoreCase(resulttemp1)==false)&&(e11!=null)){
                    //Toast toast=new Toast(getApplicationContext());
                    toast.setGravity(Gravity.BOTTOM ,0,0);
                    toast.makeText(MyService.this,"Sending message",toast.LENGTH_LONG).show();
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(ph11, null, resulttemp1+" https://www.google.com/maps/search/?api=1&query="+latitude+","+longitude, null, null);
                    flag=0;
                    f=1;
                }
                else if((result.equalsIgnoreCase(a11)==false)&&(result.equalsIgnoreCase(c11)==false)&&(result.equalsIgnoreCase(d11)==false)&&(result.equalsIgnoreCase(e11)==false)&&(f==1))
                {
                    //Toast toast=new Toast(getApplicationContext());
                    toast.setGravity(Gravity.BOTTOM ,0,0);
                    toast.makeText(MyService.this,"Sending message",toast.LENGTH_LONG).show();
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(ph11, null, "LOCATION LEFT "+resulttemp1+" https://www.google.com/maps/search/?api=1&query="+latitude+","+longitude, null, null);
                    f=0;
                    flag=1;
                }
                resulttemp = addresses.get(0).getSubLocality();
            } catch (IOException e) {
                e.printStackTrace();
            }

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
    LocationListener[] mLocationListeners = new LocationListener[] {
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };
    @Override
    public IBinder onBind(Intent arg0)
    {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Toast toast=new Toast(getApplicationContext());
        toast.setGravity(Gravity.BOTTOM ,0,0);
        toast.makeText(MyService.this,"Tracking Started",toast.LENGTH_LONG).show();
        //Bundle bundle=getIntent().getExtras();
        ph11=(String) intent.getExtras().get("PH");
        a11=(String) intent.getExtras().get("A");
        c11=(String) intent.getExtras().get("C");
        d11=(String) intent.getExtras().get("D");
        e11=(String) intent.getExtras().get("E");
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }
    @Override
    public void onCreate()
    {
        buildNotification();
        Toast toast=new Toast(getApplicationContext());
        toast.setGravity(Gravity.BOTTOM ,0,0);
        toast.makeText(MyService.this,"Finding your location",toast.LENGTH_SHORT).show();
        Log.e(TAG, "onCreate");
        initializeLocationManager();
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
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
    private void buildNotification() {
        String stop = "stop";
        registerReceiver(stopReceiver, new IntentFilter(stop));
        PendingIntent broadcastIntent = PendingIntent.getBroadcast(
                this, 0, new Intent(stop), PendingIntent.FLAG_UPDATE_CURRENT);
        // Create the persistent notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.notification_text))
                .setOngoing(true)
                .setContentIntent(broadcastIntent)
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(),
                        R.mipmap.ic_launcher))
                .setSmallIcon(R.drawable.ic_stat_name);
        startForeground(1, builder.build());
    }
    protected BroadcastReceiver stopReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "received stop broadcast");
            // Stop the service when the notification is tapped
            unregisterReceiver(stopReceiver);
            stopSelf();
        }
    };
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