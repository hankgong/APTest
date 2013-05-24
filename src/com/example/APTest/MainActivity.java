package com.example.APTest;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
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

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }


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


        sendEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Email email  = new Email();

                try {
                    //email.addAttachment(;
                    Log.i("email", "sending email");
                    Log.i("email", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath());
                    email.addAttachment(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "/Chapter11.pdf");
                    email.addAttachment(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "/buchanan.pdf");
                    email.send();
                } catch (Exception e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }

            }
        });


    }
}
