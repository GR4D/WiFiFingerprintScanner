package com.example.wififingerprintscanner;

import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.os.Bundle;

public class WiFiScannerDetails extends AppCompatActivity {

    Toolbar detailsToolbar;
    TextView scanDetailsInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wi_fi_scanner_details);

        detailsToolbar = findViewById(R.id.toolbarDetails);
        scanDetailsInfo = findViewById(R.id.scanDetails);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            detailsToolbar.setTitle("Showing details for " + bundle.getString("detailsSsid"));
            scanDetailsInfo.setText("RSSI: "+ bundle.getInt("detailsRssi") + "dBm\nFreq: " + bundle.getInt("detailsFreq") + "MHz\nChannel: " + freqToChannel(bundle.getInt("detailsFreq"))
            + "\nMAC: " + bundle.getString("detailsMac") + "\nEncryption: " + bundle.getString("detailsEncryption"));
        }
    }

    int freqToChannel(int freq) {
        if (freq == 2484)
            return 14;
        if (freq < 2484)
            return (freq - 2407) / 5;
        return freq/5 - 1000;
    }
}