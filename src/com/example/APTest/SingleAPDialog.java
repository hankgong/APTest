package com.example.APTest;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created with IntelliJ IDEA.
 * User: hgong
 * Date: 24/05/13
 * Time: 2:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class SingleAPDialog  extends Dialog {

    TextView mEncryptCapabilityTextView;
    TextView mStatusTextView;
    EditText mPasswordEditText;

    WifiManager mWifi;
    private String mSsid;
    private String mSecurityCapability;

    private WifiConfiguration mConfig;


    public SingleAPDialog(Context context, int theme) {
        super(context, theme);

        //requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.dialog_ap_config);
        //getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        mEncryptCapabilityTextView = (TextView)findViewById(R.id.encryptTextView);
        mPasswordEditText = (EditText)findViewById(R.id.passwordEditText);
        mStatusTextView = (TextView)findViewById(R.id.statusTextView);

        mWifi = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);

        final Timer closeTimer = new Timer();
        closeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                dismiss();
            }
        }, 60000);

        setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                closeTimer.cancel();
            }
        });
    }

    private void setupUI(){
        mConfig = isExists(this.mSsid);

        System.out.println("Wifi" + mConfig);

        setTitle(this.mSsid);

        if (mConfig != null) {
            Log.i("WiFi", mConfig.toString());
            mPasswordEditText.setText(mConfig.preSharedKey);
        }

        if (mConfig != null) {
            mStatusTextView.setText("Already saved WiFi configuration.");
        } else {
            mStatusTextView.setText("New configuration.");
        }
    }

    //return the config saved locally if it has, otherwise return null
    private WifiConfiguration isExists(String ssid) {
        List<WifiConfiguration> existingConfigs = mWifi.getConfiguredNetworks();
        for (WifiConfiguration existingConfig : existingConfigs) {
            System.out.println("WiFi " + existingConfig.SSID);
            if (existingConfig.SSID.equals("\""+ssid+"\"")) {
                return existingConfig;
            }
        }
        return null;
    }

    public SingleAPDialog(Context context) {
        this(context, 0);
    }

    public void setSecurityCapability(String capability) {
        this.mSecurityCapability = capability.replace("[ESS]", "").replace("[", "").replace("]", "");
        mEncryptCapabilityTextView.setText(this.mSecurityCapability);
    }

    public void setApSsid(String ssid) {
        this.mSsid = ssid;

        setupUI();
    }
}
