package com.example.siddharthvaish.locator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class standby extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_standby);
        Intent serviceIntent = new Intent(this, MyService.class);
        startService(serviceIntent);
    }
}
