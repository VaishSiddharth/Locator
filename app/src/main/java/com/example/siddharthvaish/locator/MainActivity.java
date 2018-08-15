package com.example.siddharthvaish.locator;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.Toast;

import javax.xml.transform.Result;

public class MainActivity extends AppCompatActivity {
    String poli,ambu,phph,popo,amam;
    EditText po;
    EditText am;
    public Button button4;
    public void init1(){
        button4=(Button)findViewById(R.id.button4);
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent set1=new Intent(MainActivity.this,MainActivity.class);
                startActivity(set1);
            }
        });

    }

    public Button button5;
    public void init2(){
        button5=(Button)findViewById(R.id.button5);
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent set2=new Intent(MainActivity.this,MapActivity.class);
                startActivity(set2);
            }
        });

    }
    public Button button6;
    public void init3(){
        button6=(Button)findViewById(R.id.button6);
        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent set3=new Intent(MainActivity.this,SettingActivity.class);
                startActivity(set3);
            }
        });

    }
    public Button button8;
    public void init6(){
        button8=(Button)findViewById(R.id.button8);
        button8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                poli=po.getText().toString();
                SharedPreferences sharedPrefpo=getSharedPreferences("datapo", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=sharedPrefpo.edit();
                editor.putString("po",po.getText().toString());
                editor.apply();
                poli=sharedPrefpo.getString("po","");
                Intent passdata_intentpo = new Intent(MainActivity.this,MyServicepo.class);
                passdata_intentpo.putExtra("PO",poli);
                startService(passdata_intentpo);
            }
        });

    }
    public Button button12;
    public void init9(){
        button12=(Button)findViewById(R.id.button12);
        button12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast toast=new Toast(getApplicationContext());
                toast.setGravity(Gravity.BOTTOM ,0,0);
                toast.makeText(MainActivity.this,"Saved",toast.LENGTH_SHORT).show();
                poli=po.getText().toString();
                SharedPreferences sharedPrefpo=getSharedPreferences("datapo", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=sharedPrefpo.edit();
                editor.putString("po",po.getText().toString());
                editor.apply();
                poli=sharedPrefpo.getString("po","");
            }
        });

    }
    public Button button9;
    public void init7(){
        button9=(Button)findViewById(R.id.button9);
        button9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ambu=am.getText().toString();
                SharedPreferences sharedPrefam=getSharedPreferences("dataam", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=sharedPrefam.edit();
                editor.putString("am",am.getText().toString());
                editor.apply();
                ambu=sharedPrefam.getString("am","");
                Intent passdata_intentam = new Intent(MainActivity.this,MyServiceam.class);
                passdata_intentam.putExtra("AM",ambu);
                startService(passdata_intentam);
            }
        });

    }
    public Button button7;
    public void init8(){
        button7=(Button)findViewById(R.id.button7);
        button7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast toast=new Toast(getApplicationContext());
                toast.setGravity(Gravity.BOTTOM ,0,0);
                toast.makeText(MainActivity.this,"Saved",toast.LENGTH_SHORT).show();
                ambu=am.getText().toString();
                SharedPreferences sharedPrefam=getSharedPreferences("dataam", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=sharedPrefam.edit();
                editor.putString("am",am.getText().toString());
                editor.apply();
                ambu=sharedPrefam.getString("am","");
            }
        });

    }
    public Button button3;
    public void init10(){
        button3=(Button)findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent press = new Intent(MainActivity.this,trackme.class);
                startActivity(press);
            }
        });

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init1();
        init2();
        init3();
        init6();
        init7();
        init8();
        init9();
        init10();
        Toast toast=new Toast(getApplicationContext());
        toast.setGravity(Gravity.BOTTOM ,0,0);
        toast.makeText(MainActivity.this,"Turn on LOCATION",toast.LENGTH_LONG).show();
        po=(EditText)findViewById(R.id.pol);
        am=(EditText)findViewById(R.id.amb);
        SharedPreferences sharedPrefpo=getSharedPreferences("datapo", Context.MODE_PRIVATE);
        popo=sharedPrefpo.getString("po","");
        po.setText(popo);
        poli=po.getText().toString();
        SharedPreferences sharedPrefam=getSharedPreferences("dataam", Context.MODE_PRIVATE);
        amam=sharedPrefam.getString("am","");
        am.setText(amam);
        ambu=am.getText().toString();
        /*Intent passdata_intentpo = new Intent(MainActivity.this,police.class);
        passdata_intentpo.putExtra("PO",poli);
        startActivity(passdata_intentpo);
        Intent passdata_intentam = new Intent(MainActivity.this,Ambulance.class);
        passdata_intentam.putExtra("AM",ambu);
        startActivity(passdata_intentam);*/

    }
}
