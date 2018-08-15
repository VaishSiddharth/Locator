package com.example.siddharthvaish.locator;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SettingActivity extends AppCompatActivity {
    String a,c,d,e,ph,aa,cc,dd,ee,phph;
    EditText a1;
    EditText c1;
    EditText d1;
    EditText e1;
    EditText ph1;
    Button button2;
    Button button10;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ph1=(EditText)findViewById(R.id.ph1);
        a1=(EditText)findViewById(R.id.a1);
        c1=(EditText)findViewById(R.id.c1);
        d1=(EditText)findViewById(R.id.d1);
        e1=(EditText)findViewById(R.id.e1);
        init4();
        init5();
    }
    public void init4()
    {
        button2=(Button)findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ph=ph1.getText().toString();
                a=a1.getText().toString();
                c=c1.getText().toString();
                d=d1.getText().toString();
                e=e1.getText().toString();
                SharedPreferences sharedPref=getSharedPreferences("data", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=sharedPref.edit();
                editor.putString("ph",ph1.getText().toString());
                editor.putString("a",a1.getText().toString());
                editor.putString("c",c1.getText().toString());
                editor.putString("d",d1.getText().toString());
                editor.putString("e",e1.getText().toString());
                editor.apply();
                ph=sharedPref.getString("ph","");
                a=sharedPref.getString("a","");
                c=sharedPref.getString("c","");
                d=sharedPref.getString("d","");
                e=sharedPref.getString("e","");
                Intent passdata_intent = new Intent(SettingActivity.this,MyService.class);
                passdata_intent.putExtra("PH",ph);
                passdata_intent.putExtra("A",a);
                passdata_intent.putExtra("C",c);
                passdata_intent.putExtra("D",d);
                passdata_intent.putExtra("E",e);
                startService(passdata_intent);
            }
        });
    }
    public void init5()
    {
        button10=(Button)findViewById(R.id.button10);
        button10.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                SharedPreferences sharedPref=getSharedPreferences("data", Context.MODE_PRIVATE);
                phph=sharedPref.getString("ph","");
                aa=sharedPref.getString("a","");
                cc=sharedPref.getString("c","");
                dd=sharedPref.getString("d","");
                ee=sharedPref.getString("e","");
                ph1.setText(phph);
                a1.setText(aa);
                c1.setText(cc);
                d1.setText(dd);
                e1.setText(ee);
                /*ph=ph1.getText().toString();
                a=a1.getText().toString();
                c=c1.getText().toString();
                d=d1.getText().toString();
                e=e1.getText().toString();
                Intent passdata_intent = new Intent(SettingActivity.this,MapsActivity.class);
                passdata_intent.putExtra("PH",ph);
                passdata_intent.putExtra("A",a);
                passdata_intent.putExtra("C",c);
                passdata_intent.putExtra("D",d);
                passdata_intent.putExtra("E",e);
                startActivity(passdata_intent);*/
            }
        });

    }
}

