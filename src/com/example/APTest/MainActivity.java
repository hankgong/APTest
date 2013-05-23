package com.example.APTest;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);


        Button testApButton = (Button)findViewById(R.id.testApButton);
        Button sendEmailButton = (Button)findViewById(R.id.sendEmailButton);

        EditText srcAddrEditText = (EditText) findViewById(R.id.srcAddrEditText);
        EditText dstAddrEditText = (EditText) findViewById(R.id.dstAddrEditText);

        testApButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WirelessDialog wlDialog = new WirelessDialog(MainActivity.this);
                wlDialog.show();
            }
        });
    }
}
