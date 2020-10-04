package com.example.wififingerprintscanner;

import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.os.Bundle;

public class WiFiScannerDetails extends AppCompatActivity {

    Toolbar mToolbar;
    TextView scanDetailsInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wi_fi_scanner_details);

        mToolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar);
        scanDetailsInfo = (TextView) findViewById(R.id.scanDetails);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            mToolbar.setTitle(bundle.getString("detailsSsid"));
            scanDetailsInfo.setText("RSSI: "+ bundle.getInt("detailsRssi") + "dBm  Freq: " + bundle.getInt("detailsFreq") + "MHz  Channel: " + freqToChannel(bundle.getInt("detailsFreq"))
            + "  MAC: " + bundle.getString("detailsMac") + "  Encryption: " + bundle.getString("detailsEncryption"));
        }

    }
    int freqToChannel(int freq)
    {
        if (freq == 2484)
            return 14;

        if (freq < 2484)
            return (freq - 2407) / 5;

        return freq/5 - 1000;
    }
}