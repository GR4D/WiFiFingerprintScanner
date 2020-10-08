package com.example.wififingerprintscanner;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;


public class WiFiScannerActivity extends AppCompatActivity {

    private WifiManager wifiManager;
    private ListView wifiList;
    private Button buttonScan;
    private List<ScanResult> results;
    private ArrayList<String> arrayList = new ArrayList<>();
    private ArrayAdapter adapter;

    BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            results = wifiManager.getScanResults();
            unregisterReceiver(this);

            for (ScanResult scanResult : results) {
                arrayList.add(scanResult.SSID + " RSSI: " + scanResult.level + "dBm");
                adapter.notifyDataSetChanged();
            }
        };
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonScan = findViewById(R.id.scanBtn);
        buttonScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanWifi();
            }
        });

        wifiList = findViewById(R.id.wifiList);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);


        if (!wifiManager.isWifiEnabled()) {
            Toast.makeText(this, "You need to enable WiFi in order to scan for nearby APs.", Toast.LENGTH_LONG).show();
            wifiManager.setWifiEnabled(true);
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayList);
        wifiList.setAdapter(adapter);
        wifiList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(WiFiScannerActivity.this, WiFiScannerDetails.class);

                fillData(intent,i);

                startActivity(intent);
            }
        });
    }

    public void fillData(Intent intent, int i){
        intent.putExtra("detailsSsid", results.get(i).SSID);
        intent.putExtra("detailsRssi", results.get(i).level);
        intent.putExtra("detailsFreq", results.get(i).frequency);
        intent.putExtra("detailsMac", results.get(i).BSSID);
        intent.putExtra("detailsEncryption", results.get(i).capabilities);
        System.out.println(results.get(i).BSSID + " BLANK RSSI VALUE: " + results.get(i).level);
    }

    public void scanWifi() {
        arrayList.clear();
        System.out.println("Test");
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
       if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ){
           if(!wifiManager.startScan()){
               Toast.makeText(this, "Należy odczekać 2 minuty aby ponowić skanowanie...", Toast.LENGTH_SHORT).show();
           }else{
               Toast.makeText(this, "Scanning WiFi ...", Toast.LENGTH_SHORT).show();
               System.out.println("Skanowanie prawidlowe");
           }
       }else if(shouldShowRequestPermissionRationale(String.valueOf(PackageManager.PERMISSION_GRANTED))){
           System.out.println("Nalezy wyswietlic UI.");
       }else{
           System.out.println("Nalezy nadac uprawnienia");
       }

    }
}