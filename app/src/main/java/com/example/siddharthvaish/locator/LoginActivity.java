package com.example.siddharthvaish.locator;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import static com.example.siddharthvaish.locator.Permissions.REQUEST_CODE_REQUIRED_PERMISSIONS;
import static com.example.siddharthvaish.locator.Permissions.REQUIRED_PERMISSIONS;

public class LoginActivity extends AppCompatActivity {

    EditText username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        PreferenceManager.init(this);
        username = findViewById(R.id.useremailid);
        if (!PreferenceManager.getStringValue(PreferenceManager.USER_ID).isEmpty()) {
            startActivity(new Intent(this, LaunchActivity.class));

        }
        if (!Permissions.hasPermissions(this, REQUIRED_PERMISSIONS)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_REQUIRED_PERMISSIONS);
            }
        }
    }

    public void onLoginButtonClick(View view) {
        PreferenceManager.setStringValue(PreferenceManager.USER_ID, username.getText().toString());
        startActivity(new Intent(this, LaunchActivity.class));
    }
}
