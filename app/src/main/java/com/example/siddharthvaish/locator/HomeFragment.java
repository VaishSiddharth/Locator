package com.example.siddharthvaish.locator;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static java.net.Proxy.Type.HTTP;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    Button button;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 25;
    private static final String TAG = HomeFragment.class.getSimpleName();
    private GoogleMap mMap;
    private SeekBar seekBar;
    private ImageView next;
    Circle circle;
    private float radius;
    private FusedLocationProviderClient fusedLocationClient;
    double latitude;
    double longitude;
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 10f;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    float shortestDistancePolice = 100000000f;
    float shortestDistanceHospital= 100000000f;
    List<ModelPlace> modelPlaceList;
    List<ModelPlace> modelPlaceListHospital;
    TextView namePoliceStation;
    TextView phonePoliceStation;
    TextView nameHospital;
    TextView phoneHospital;
    ImageView emergencyPolice,emergencyHospital;
    String namepolice,namehospital,phoneNumberpolice,phoneNumberHospital;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        initializeLocationManager();
        try {
            if (ContextCompat.checkSelfPermission(getContext(),
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
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

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
            currentLocation(location);
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


    LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getContext().getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    public void currentLocation(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        contactPolice();
        contactHospital();
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

    public void contactPolice() {
        modelPlaceList = new ArrayList<>();
        modelPlaceList.clear();
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Police");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ModelPlace modelPlace = snapshot.getValue(ModelPlace.class);
                    if (modelPlace != null) {
                        float[] results = new float[1];
                        Location.distanceBetween(latitude, longitude, Double.parseDouble(modelPlace.getLatitude()), Double.parseDouble(modelPlace.getLongitude()), results);
                        float distanceInMeters = results[0];
                        //Log.e(TAG,String.valueOf(distanceInMeters));
                        if (shortestDistancePolice > distanceInMeters) {
                            //Log.e(TAG, String.valueOf(distanceInMeters));
                            shortestDistancePolice = distanceInMeters;
                            modelPlaceList.add(modelPlace);
                        }
                    }
                }
                if (modelPlaceList != null && modelPlaceList.size() > 0) {
                    int index = modelPlaceList.size() - 1;
                    while (modelPlaceList.get(index).getName() == null
                            || modelPlaceList.get(index).getPhoneNumber() == null) {
                        if (index > 0)
                            index = index - 1;
                        else
                            break;
                    }
                    namepolice = modelPlaceList.get(index).getName();
                    phoneNumberpolice = modelPlaceList.get(index).getPhoneNumber();
                    namePoliceStation.setText(namepolice);
                    phonePoliceStation.setText(phoneNumberpolice);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void contactHospital() {
        modelPlaceListHospital = new ArrayList<>();
        modelPlaceListHospital.clear();
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Hospital");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ModelPlace modelPlace = snapshot.getValue(ModelPlace.class);
                    if (modelPlace != null) {
                        float[] results = new float[1];
                        Location.distanceBetween(latitude, longitude, Double.parseDouble(modelPlace.getLatitude()), Double.parseDouble(modelPlace.getLongitude()), results);
                        float distanceInMeters = results[0];
                        //Log.e(TAG,String.valueOf(distanceInMeters));
                        if (shortestDistanceHospital > distanceInMeters) {
                            //Log.e(TAG, String.valueOf(distanceInMeters));
                            shortestDistanceHospital = distanceInMeters;
                            modelPlaceListHospital.add(modelPlace);
                        }
                    }
                }
                if (modelPlaceListHospital != null && modelPlaceListHospital.size() > 0) {
                    int index = modelPlaceListHospital.size() - 1;
                    while (modelPlaceListHospital.get(index).getName() == null
                            || modelPlaceListHospital.get(index).getPhoneNumber() == null) {
                        if (index > 0)
                            index = index - 1;
                        else
                            break;
                    }
                    namehospital = modelPlaceListHospital.get(index).getName();
                    phoneNumberHospital = modelPlaceListHospital.get(index).getPhoneNumber();
                    nameHospital.setText(namehospital);
                    phoneHospital.setText(phoneNumberHospital);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        Button startservice = view.findViewById(R.id.startservice);
        Button stopservice = view.findViewById(R.id.stopservice);
        namePoliceStation = view.findViewById(R.id.namePoliceStation);
        phonePoliceStation = view.findViewById(R.id.phonePoliceStation);
        nameHospital = view.findViewById(R.id.nameHospital);
        phoneHospital = view.findViewById(R.id.phoneHospital);
        emergencyHospital=view.findViewById(R.id.emergencyhospital);
        emergencyPolice=view.findViewById(R.id.emergencypolice);
        stopservice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().stopService(new Intent(getContext(), LocationUpdateService.class));
            }
        });
        startservice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Log.e("HomeFragment.class","clicked");
                getActivity().startService(new Intent(getContext(), LocationUpdateService.class));
            }
        });
        emergencyoptions();
        return view;
    }
    public void emergencyoptions()
    {
        emergencyPolice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final SweetAlertDialog pDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE);
                pDialog.setTitleText("Emergency!!");
                pDialog.setContentText("If you click send a message will be send to the Police Station.\n");
                pDialog.setCancelable(true);
                pDialog.setCancelButton("Call", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:" + phoneNumberpolice));
                        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                            startActivity(intent);
                        }
                        pDialog.dismiss();
                    }
                });
                pDialog.setConfirmButton("Send", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage("+919149386335", null, "Emergency at location https://www.google.com/maps/search/?api=1&query="+latitude+","+longitude, null, null);
                        Toast.makeText(getContext(),"Sending SMS",Toast.LENGTH_LONG).show();
                        pDialog.dismiss();
                    }
                });
                pDialog.show();
            }
        });
        emergencyHospital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final SweetAlertDialog pDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE);
                pDialog.setTitleText("Emergency!!");
                pDialog.setContentText("If you click send a message will be send to the Hospital.\n");
                pDialog.setCancelable(true);
                pDialog.setCancelButton("Call", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:" + phoneNumberHospital));
                        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                            startActivity(intent);
                        }
                        pDialog.dismiss();
                    }
                });
                pDialog.setConfirmButton("Send", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage("+919149386335", null, "Ambulance required at location https://www.google.com/maps/search/?api=1&query="+latitude+","+longitude, null, null);
                        Toast.makeText(getContext(),"Sending SMS",Toast.LENGTH_LONG).show();
                        pDialog.dismiss();
                    }
                });
                pDialog.show();
            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
