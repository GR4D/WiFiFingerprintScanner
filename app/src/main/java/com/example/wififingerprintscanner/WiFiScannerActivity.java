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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.mysql.jdbc.StringUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class WiFiScannerActivity extends AppCompatActivity {

    private WifiManager wifiManager;
    private ListView wifiList;
    private List<ScanResult> results;
    private Button scanButton;
    private Button sendUpdateButton;
    private Button switchToMapButton;
    private Button aboutButton;
    private ArrayList<String> arrayList = new ArrayList<>();
    private ArrayAdapter adapter;
    protected static String DB_URL_PREFIX = "jdbc:mysql://";
    protected static String DB_URL_SUFFIX = "/wifi_fingerprint_db?allowMultiQueries=true";
    protected int scanIndex = 0;
    protected static String DB_URL ;
    protected static String USER ;
    protected static String PASS ;

    WiFiScannerDetails wiFiScannerDetails = new WiFiScannerDetails();

    BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            results = wifiManager.getScanResults();
            unregisterReceiver(this);
            scanButton.setEnabled(true);
            sendUpdateButton.setEnabled(true);

            for (ScanResult scanResult : results) {
                arrayList.add(scanResult.SSID + " RSSI: " + scanResult.level + "dBm");
                adapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        aboutButton = findViewById(R.id.aboutBtn);
        aboutButton.setOnClickListener(view -> {
            Intent aboutView = new Intent(WiFiScannerActivity.this, About.class);
            startActivity(aboutView);
        });
        if (!StringUtils.isNullOrEmpty(DB_URL) || !StringUtils.isNullOrEmpty(USER) || !StringUtils.isNullOrEmpty(PASS)){
            Toast.makeText(this, "Uzupełnij dane do logowania w zakładce ABOUT.", Toast.LENGTH_LONG).show();
        }

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            Toast.makeText(this, "You need to enable WiFi in order to scan for nearby APs.", Toast.LENGTH_LONG).show();
            wifiManager.setWifiEnabled(true);
        }

        scanButton = findViewById(R.id.scanBtn);
        scanButton.setOnClickListener(view -> {
            scanWifi();
            scanButton.setEnabled(false);
            switchToMapButton.setEnabled(false);
        });

        sendUpdateButton = findViewById(R.id.updateBtn);
        sendUpdateButton.setEnabled(false);
        sendUpdateButton.setOnClickListener(view -> {
           if(results != null){
               DB_URL = DB_URL_PREFIX + About.url + DB_URL_SUFFIX;
               USER = About.username;
               PASS = About.password;
               updateApList();
               sendUpdateButton.setEnabled(false);
               switchToMapButton.setEnabled(true);

           }
        });

        switchToMapButton = findViewById(R.id.switchToFingerprint);
        switchToMapButton.setEnabled(false);
        switchToMapButton.setOnClickListener(view -> {
            Intent mapa = new Intent(WiFiScannerActivity.this, WiFiFingerprints.class);
            startActivity(mapa);
        });

        wifiList = findViewById(R.id.wifiList);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayList);
        wifiList.setAdapter(adapter);
        wifiList.setOnItemClickListener((adapterView, view, i, l) -> {

            Intent intent = new Intent(WiFiScannerActivity.this, WiFiScannerDetails.class);
            fillData(intent,i);
            startActivity(intent);
        });
    }

    public void fillData(Intent intent, int i){
        intent.putExtra("detailsSsid", results.get(i).SSID);
        intent.putExtra("detailsRssi", results.get(i).level);
        intent.putExtra("detailsFreq", results.get(i).frequency);
        intent.putExtra("detailsMac", results.get(i).BSSID);
        intent.putExtra("detailsEncryption", results.get(i).capabilities);
    }

    public void scanWifi() {
        arrayList.clear();
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
       if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ){
           if(!wifiManager.startScan()){
               Toast.makeText(this, "Należy odczekać 2 minuty aby ponowić skanowanie...", Toast.LENGTH_SHORT).show();
           }else{
               Toast.makeText(this, "Scanning WiFi ...", Toast.LENGTH_SHORT).show();
           }
       }else if(shouldShowRequestPermissionRationale(String.valueOf(PackageManager.PERMISSION_GRANTED))){
           Toast.makeText(this, "Error: 1 ", Toast.LENGTH_SHORT).show();
       }else{
           Toast.makeText(this, "Brak dostepu do lokalizacji.", Toast.LENGTH_SHORT).show();
       }
    }

    int CHANNEL_WIDTH(int channelWidth){
        switch (channelWidth) {
            case 0:
                return 20;
            case 1:
                return 40;
            case 2:
                return 80;
            case 3:
                return 160;
            default:
                return 81;
        }
    }

    public void updateApList(){
        DB_URL = About.url;
        USER = About.username;
        PASS = About.password;
        if (!StringUtils.isNullOrEmpty(DB_URL) || !StringUtils.isNullOrEmpty(USER) || !StringUtils.isNullOrEmpty(PASS)){
            for(int x = 0; x < results.size(); x++){
                scanIndex = x;
                String insert = "insert ignore into access_points (mac, ssid, frequency, channel, channel_width, first_seen ) " +
                        "values ('" + results.get(scanIndex).BSSID + "','" + results.get(scanIndex).SSID + "','" + results.get(scanIndex).frequency + "','" + wiFiScannerDetails.freqToChannel(results.get(scanIndex).frequency) + "' ,'" + CHANNEL_WIDTH(results.get(scanIndex).channelWidth) + "', '" + Calendar.getInstance().getTime() + "')";
                DatabaseSend objSend = new DatabaseSend();
                objSend.setData(DB_URL, USER, PASS, insert);

                DatabaseSend.SendUpdate objSendUpdate = objSend.new SendUpdate();
                objSendUpdate.execute("");
            }
        }else {
            Toast.makeText(this, "Uzupełnij dane do logowania w zakładce ABOUT.", Toast.LENGTH_LONG).show();
        }
        switchToMapButton.setEnabled(true);
    }
}