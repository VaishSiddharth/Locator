package com.example.siddharthvaish.locator;

import android.support.v7.app.AppCompatActivity;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class PushLocationsToDBActivity extends AppCompatActivity {
    private static final String TAG =PushLocationsToDBActivity.class.getSimpleName() ;
    Button pushdata;
    String URL="https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=26.2502016,78.169562&radius=100000&type=police&placeId&key=AIzaSyA_2qA3DMJJfXEmtXaq8SMpo5TsVuhJ0Zc";
    List<ModelPolice> modelPoliceList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push_locations_to_db);
        pushdata=findViewById(R.id.pushdata);
        pushdata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendDataToDB(URL);
            }
        });
    }
    public void sendDataToDB(String URL)
    {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest= new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG,"Inside fun "+response);
                try {
                    final JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("results");
                    for(int i=0;i<jsonArray.length();i++)
                    {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        JSONObject jsonlocation=jsonObject1.getJSONObject("geometry").getJSONObject("location");
                        String latitude= jsonlocation.getString("lat");
                        String longitude= jsonlocation.getString("lng");
                        String name=jsonObject1.getString("name");
                        String place_id=jsonObject1.getString("place_id");
                        ModelPolice modelPolice =new ModelPolice();
                        modelPolice.setName(name);
                        modelPolice.setPlace_id(place_id);
                        modelPolice.setLatitude(latitude);
                        modelPolice.setLongitude(longitude);
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Police");
                        reference.push().setValue(modelPolice);
                        //Log.e(TAG,name+place_id+latitude+longitude);


                    }
                    try {
                        if (jsonObject.has("next_page_token")) {
                            String nextPageToken = jsonObject.getString("next_page_token");
                            Log.e(TAG, nextPageToken);
                            sendDataToDB("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=26.2502016,78.169562&radius=100000&type=police&key=AIzaSyA_2qA3DMJJfXEmtXaq8SMpo5TsVuhJ0Zc&pagetoken="+nextPageToken);
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
