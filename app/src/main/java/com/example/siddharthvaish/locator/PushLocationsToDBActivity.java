package com.example.siddharthvaish.locator;

import android.os.Handler;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class PushLocationsToDBActivity extends AppCompatActivity {
    private static final String TAG = PushLocationsToDBActivity.class.getSimpleName();
    Button pushdata;
    String defURL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=26.2502016,78.169562&radius=100000&type=police&key=AIzaSyDIqdCLT3Q6zljPKkUKXyx3rJX4Zjk928o";
    String defURLp1 = "https://maps.googleapis.com/maps/api/place/details/json?placeid=";
    String defURLp2 = "&fields=name,formatted_phone_number&key=AIzaSyDIqdCLT3Q6zljPKkUKXyx3rJX4Zjk928o";
    List<ModelPlace> modelPlaceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push_locations_to_db);
        pushdata = findViewById(R.id.pushdata);
        pushdata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //sendDataToDB(defURL);
                //sendDetailsToDB();
            }
        });
    }

    public void sendDetailsToDB() {
        final DatabaseReference myref = FirebaseDatabase.getInstance().getReference().child("Hospital");
        myref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ModelPlace modelPlace = snapshot.getValue(ModelPlace.class);
                    if (modelPlace != null&&snapshot.getKey()!=null) {
                        Log.e(TAG, "modelPlace != null");
                        String placeId = modelPlace.getPlace_id();
                        Log.e(TAG, "The url before calling is " + defURLp1 + placeId + defURLp2);
                        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                        StringRequest stringRequest = new StringRequest(Request.Method.GET, defURLp1 + placeId + defURLp2, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.e(TAG, "Inside fun " + response);
                                try {
                                    final JSONObject jsonObject = new JSONObject(response);
                                    JSONObject jsonresult = jsonObject.getJSONObject("result");
                                    String phoneNumber = jsonresult.getString("formatted_phone_number");
                                    String name = jsonresult.getString("name");
                                    ModelPlace modelPlace = new ModelPlace();
                                    modelPlace.setName(name);
                                    modelPlace.setPlace_id(phoneNumber);
                                    HashMap<String, Object> namePhone = new HashMap<>();
                                    namePhone.put("phoneNumber", phoneNumber);
                                    namePhone.put("name", name);
                                    myref.child(snapshot.getKey()).updateChildren(namePhone);
                                    Log.e(TAG, name + phoneNumber);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                error.printStackTrace();
                            }
                        });
                        int socketTimeout = 30000;
                        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                        stringRequest.setRetryPolicy(policy);
                        requestQueue.add(stringRequest);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void sendDataToDB(String URL) {
        Log.e(TAG, "The url before calling is " + URL);
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Inside fun " + response);
                try {
                    final JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("results");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        JSONObject jsonlocation = jsonObject1.getJSONObject("geometry").getJSONObject("location");
                        String latitude = jsonlocation.getString("lat");
                        String longitude = jsonlocation.getString("lng");
                        String name = jsonObject1.getString("name");
                        String place_id = jsonObject1.getString("place_id");
                        ModelPlace modelPlace = new ModelPlace();
                        modelPlace.setName(name);
                        modelPlace.setPlace_id(place_id);
                        modelPlace.setLatitude(latitude);
                        modelPlace.setLongitude(longitude);
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Police");
                        reference.push().setValue(modelPlace);
                        //Log.e(TAG,name+place_id+latitude+longitude);


                    }
                    try {
                        if (jsonObject.has("next_page_token")) {
                            final String nextPageToken = jsonObject.getString("next_page_token");
                            // Log.e(TAG, defURL+"&pagetoken="+nextPageToken);
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    sendDataToDB(defURL + "&pagetoken=" + nextPageToken);
                                }
                            }, 5000);

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);
    }
}
