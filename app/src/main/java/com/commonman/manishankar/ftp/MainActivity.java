package com.commonman.manishankar.ftp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button btserver;
    Button btclient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btserver=(Button)findViewById(R.id.server);
        btclient=(Button)findViewById(R.id.client);
        btserver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
/*
                MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ApManager apManager=new ApManager();
                apManager.isApOn(getApplicationContext());
                apManager.configApState(getApplicationContext());
            }
        });
  */              Intent i=new Intent(getApplicationContext(),server.class);
                startActivity(i);
            }
        });

        btclient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(),client.class);
                startActivity(i);
            }
        });
    }
}
