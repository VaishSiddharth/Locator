package com.example.siddharthvaish.locator;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Path;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    int flag=1;
    int f=0;
    String a11=null;
    String c11=null;
    String d11=null;
    String e11=null;
    int ph12=0;
    String ph11=null;
    String resulttemp = null;
    private GoogleMap mMap;
    private LocationManager manager;
    private LocationListener locationListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Toast toast=new Toast(getApplicationContext());
        toast.setGravity(Gravity.BOTTOM ,0,0);
        toast.makeText(MapsActivity.this,"Finding your location",toast.LENGTH_SHORT).show();
        Bundle bundle=getIntent().getExtras();
        ph11=bundle.getString("PH");
        a11=bundle.getString("A");
        c11=bundle.getString("C");
        d11=bundle.getString("D");
        e11=bundle.getString("E");
        /*Toast toast=new Toast(getApplicationContext());
        toast.setGravity(Gravity.BOTTOM ,0,0);
        toast.makeText(MapsActivity.this,ph11,toast.LENGTH_LONG).show();*/



        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //get the location service
        manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        //request the location update thru location manager
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
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //get the latitude and longitude from the location
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                //get the location name from latitude and longitude
                Geocoder geocoder = new Geocoder(getApplicationContext());
                String result = null;
                String resulttemp1 = null;
                try {
                    List<Address> addresses =
                            geocoder.getFromLocation(latitude, longitude,1);
                    resulttemp1=addresses.get(0).getAddressLine(0);
                    result = addresses.get(0).getSubLocality();
                    LatLng latLng = new LatLng(latitude, longitude);
                    if(result.equalsIgnoreCase(resulttemp)==false) {
                        mMap.addMarker(new MarkerOptions().position(latLng).title(result));
                        Toast toast=new Toast(getApplicationContext());
                        toast.setGravity(Gravity.BOTTOM ,0,0);
                        toast.makeText(MapsActivity.this,"Found",toast.LENGTH_LONG).show();
                    }
                    mMap.addMarker(new MarkerOptions().position(latLng).title(resulttemp1).icon(BitmapDescriptorFactory.fromResource(R.drawable.circle_blue)));
                    mMap.setMaxZoomPreference(500);
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    if(flag==1)
                    mMap.moveCamera(CameraUpdateFactory.zoomTo(13.8f));
                    if((result.equalsIgnoreCase(a11)==true)&&(flag==1)&&(result.equalsIgnoreCase(resulttemp1)==false)&&(a11!=null)) {
                        Toast toast=new Toast(getApplicationContext());
                        toast.setGravity(Gravity.BOTTOM ,0,0);
                        toast.makeText(MapsActivity.this,"Sending message",toast.LENGTH_LONG).show();
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(ph11, null, resulttemp1, null, null);
                        flag=0;
                        f=1;
                    }
                    else if ((result.equalsIgnoreCase(c11)==true)&& (flag==1)&&(result.equalsIgnoreCase(resulttemp1)==false)&&(c11!=null)){
                        Toast toast=new Toast(getApplicationContext());
                        toast.setGravity(Gravity.BOTTOM ,0,0);
                        toast.makeText(MapsActivity.this,"Sending message",toast.LENGTH_LONG).show();
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(ph11, null, resulttemp1, null, null);
                        flag=0;
                        f=1;
                    }
                    else if ((result.equalsIgnoreCase(d11)==true)&& (flag==1)&&(result.equalsIgnoreCase(resulttemp1)==false)&&(d11!=null)){
                        Toast toast=new Toast(getApplicationContext());
                        toast.setGravity(Gravity.BOTTOM ,0,0);
                        toast.makeText(MapsActivity.this,"Sending message",toast.LENGTH_LONG).show();
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(ph11, null, resulttemp1, null, null);
                        flag=0;
                        f=1;
                    }
                    else if ((result.equalsIgnoreCase(e11)==true)&& (flag==1)&&(result.equalsIgnoreCase(resulttemp1)==false)&&(e11!=null)){
                        Toast toast=new Toast(getApplicationContext());
                        toast.setGravity(Gravity.BOTTOM ,0,0);
                        toast.makeText(MapsActivity.this,"Sending message",toast.LENGTH_LONG).show();
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(ph11, null, resulttemp1, null, null);
                        flag=0;
                        f=1;
                    }
                    else if((result.equalsIgnoreCase(a11)==false)&&(result.equalsIgnoreCase(c11)==false)&&(result.equalsIgnoreCase(d11)==false)&&(result.equalsIgnoreCase(e11)==false)&&(f==1))
                    {
                        Toast toast=new Toast(getApplicationContext());
                        toast.setGravity(Gravity.BOTTOM ,0,0);
                        toast.makeText(MapsActivity.this,"Sending message",toast.LENGTH_LONG).show();
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(ph11, null, "Location left "+resulttemp1, null, null);
                        f=0;
                        flag=1;
                    }
                    resulttemp = addresses.get(0).getSubLocality();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }

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
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
   @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    @Override
    protected void onPause() {
        super.onPause();
        manager.removeUpdates(locationListener);
        Log.i("onPause...","paused");
    }
}
