package com.example.APTest;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.*;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.view.*;
import android.widget.*;
import junit.framework.TestResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created with IntelliJ IDEA.
 * User: hgong
 * Date: 21/05/13
 * Time: 1:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class WirelessDialog extends Dialog {

    WifiManager mWifi;
    ListView mListView;

    //Note that two lists here are different
    //mScanResults is the returned scanned aps after applying ap discovery
    //mWifiConfigs is the saved ap configs
    //todo: these two need to cooperate
    List<ScanResult> mScanResults = new ArrayList<ScanResult>();
    List<WifiConfiguration> mWifiConfigs = new ArrayList<WifiConfiguration>();


    WiFiAPAdapter mWifiAdapter;

    public WirelessDialog(Context context) {
        this(context, 0);
    }

    public WirelessDialog(Context context, int theme) {
        super(context, theme);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.dialog_wireless_interface);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

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

        mWifi = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);

        if (mWifi.isWifiEnabled() == false) {
            Toast.makeText(context.getApplicationContext(), "WiFi is disabled.. making it enabled", Toast.LENGTH_LONG).show();
            mWifi.setWifiEnabled(true);
        }


        //mScanResults.add(new ScanResult());

        mListView = (ListView)findViewById(R.id.listView);
        mWifiAdapter = new WiFiAPAdapter(getContext(), mScanResults);
        mListView.setAdapter(mWifiAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                TextView ssidTextView = (TextView)view.findViewById(R.id.list2colsleft);
                TextView capabilityTextView = (TextView)view.findViewById(R.id.list2colsright);
                String capability = capabilityTextView.getText().toString();

                SingleAPDialog selectedAPDialog = new SingleAPDialog(getContext());

                //put contents into the dialog
                System.out.println("WiFi1 " + ssidTextView.getText().toString());
                selectedAPDialog.setApSsid(ssidTextView.getText().toString());
                selectedAPDialog.setSecurityCapability(capability);

                selectedAPDialog.show();


            }
        });



        getContext().registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                //pay attention to here, this will not work because the address of mScanResults will be linked to
                //something else, then the adapter will not work
                mScanResults.clear();
                for (ScanResult sr: mWifi.getScanResults()) {
                    mScanResults.add(sr);
                }

                mWifiAdapter.notifyDataSetChanged();
            }
        }, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        mWifi.startScan();
    }

    private Dialog createDialog() {
        Dialog retDialog = new AlertDialog.Builder(getContext()).setIcon(android.R.drawable.btn_star)
                .setTitle("Configration of one AP").setView(new EditText(getContext())).create();
        return retDialog;
    }


    public class WiFiAPAdapter extends ArrayAdapter<ScanResult> {

        private List<ScanResult> mAPs;
        private int mSelectedPos = -1;

        private LayoutInflater mInflater;

        public WiFiAPAdapter(Context context,  List<ScanResult> objects) {
            super(context, R.layout.list_twocols, objects);
            this.mAPs = objects;
            mInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View retView = mInflater.inflate(R.layout.list_twocols, parent, false);

            //return super.getView(position, convertView, parent);
            ScanResult sr = mAPs.get(position);
            if (sr != null) {
                TextView apEssid = (TextView) retView.findViewById(R.id.list2colsleft);
                TextView encryptProtocol = (TextView) retView.findViewById(R.id.list2colsright);

                if (apEssid !=null) {
                    apEssid.setText(sr.SSID);
                }

                if (encryptProtocol != null) {
                    encryptProtocol.setText(sr.capabilities);
                }
            }

            return retView;
        }
    }
}
