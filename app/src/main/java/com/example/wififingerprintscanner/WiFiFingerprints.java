package com.example.wififingerprintscanner;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.mysql.jdbc.StringUtils;

import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class WiFiFingerprints extends AppCompatActivity{

    private WifiManager wifiManager;
    private Button scanButton;
    private Button sendButton;
    private List<ScanResult> results;
    protected int scanIndex = 0;
    private PaintView mapa;
    protected EditText xPos;
    protected EditText yPos;
    private RadioButton radioVertical;
    private RadioButton radioHorizontal;
    private EditText comments;
    private List<ScanResult> results2 = new ArrayList<>();
    private List<ScanResult> results5 = new ArrayList<>();
    private Spinner fingerprintPlaces;
    private String insert2;
    private String insert5;

    WiFiScannerActivity wiFiScannerActivity = new WiFiScannerActivity();
    WiFiScannerDetails wiFiScannerDetails = new WiFiScannerDetails();

    BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            results = wifiManager.getScanResults();
            unregisterReceiver(this);
            sendButton.setEnabled(true);
            filterFrequency();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wi_fi_fingerprints);

        mapa = findViewById(R.id.paint_view);
        fingerprintPlaces = findViewById(R.id.fingerprintPlacesList);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.fingerprintPlace, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fingerprintPlaces.setAdapter(adapter2);
        fingerprintPlaces.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if(fingerprintPlaces.getSelectedItemPosition() == 0){
                    mapa.setBackgroundResource(R.drawable.plan);
                }else if(fingerprintPlaces.getSelectedItemPosition() == 1){
                    mapa.setBackgroundResource(R.drawable.plan2);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        scanButton = findViewById(R.id.scanBtn);
        comments = findViewById(R.id.commentsText);
        comments.setOnClickListener(view -> comments.setText(null));

        scanButton = findViewById(R.id.fingerprintButton);
        scanButton.setOnClickListener(view -> {
            sendButton.setEnabled(false);
            scanWifi();
        });

        sendButton = findViewById(R.id.sendButton);
        sendButton.setEnabled(false);
        sendButton.setOnClickListener(view -> {
            if(results != null){
                scanButton.setEnabled(false);
                updateApList();
                sendButton.setEnabled(false);
            }
        });

        radioVertical = findViewById(R.id.radioVertical);
        radioHorizontal = findViewById(R.id.radioHorizontal);
        comments = findViewById(R.id.commentsText);

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);


        if (!wifiManager.isWifiEnabled()) {
            Toast.makeText(this, "You need to enable WiFi in order to perform a fingerprint scan.", Toast.LENGTH_LONG).show();
            wifiManager.setWifiEnabled(true);
        }

                xPos = findViewById(R.id.xPos);
                yPos = findViewById(R.id.yPos);

        PaintView paintView = findViewById(R.id.paint_view);
        paintView.setOnTouchListener(paintView);
    }

    public void scanWifi() {

            PaintView paintView = findViewById(R.id.paint_view);
        if (paintView.isClicked){


            xPos.setText(String.valueOf(paintView.xX));
            yPos.setText(String.valueOf(paintView.yY));

            registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ){
                if(!wifiManager.startScan() ){
                    Toast.makeText(this, "Należy odczekać 2 minuty aby ponowić skanowanie...", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this, "Scanning WiFi ...", Toast.LENGTH_SHORT).show();
                }
            }else if(shouldShowRequestPermissionRationale(String.valueOf(PackageManager.PERMISSION_GRANTED))){
                Toast.makeText(this, "Error: 1 ", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "Brak dostepu do lokalizacji.", Toast.LENGTH_SHORT).show();
            }
        }else
            Toast.makeText(this, "Należy oznaczyć miejsce pomiaru", Toast.LENGTH_SHORT).show();

    }

    public void filterFrequency(){
        if(results != null) {
            results2.clear();
            results5.clear();
            for(ScanResult scanResult : results){
                if(scanResult.frequency < 2550){
                    results2.add(scanResult);
                }else
                    results5.add(scanResult);
            }
        }
    }
    
    public void prepareInsertStatement(){
        switch (results2.size()) {
            case 0:
                insert2 = "";
                break;
            case 1:
                insert2 = "insert ignore into fingerprints (x_pos, y_pos, band, ap_1, ss_1, date, device_info, device_mac, orientation, fingerprint_area, comments) " +
                        "values ('" + xPos.getText() + "','" + yPos.getText() + "','" + "2.4" + "','" + results2.get(0).BSSID + "' ,'" + results2.get(0).level + "','" + Calendar.getInstance().getTime() + "'," +
                        "'" + Build.MANUFACTURER + " " + Build.MODEL + "','" + getMacAddr() + "', '" + orientation() + "','" + fingerprintPlaces.getSelectedItem().toString() + "','" + comments.getText() + "');";
                break;
            case 2:
                insert2 = "insert ignore into fingerprints (x_pos, y_pos, band, ap_1, ss_1, ap_2, ss_2, date, device_info, device_mac, orientation, fingerprint_area, comments) " +
                        "values ('" + xPos.getText() + "','" + yPos.getText() + "','" + "2.4" + "','" + results2.get(0).BSSID + "' ,'" + results2.get(0).level + "','" + results2.get(1).BSSID + "','" + results2.get(1).level + "' ,'" + Calendar.getInstance().getTime() + "'," +
                        "'" + Build.MANUFACTURER + " " + Build.MODEL + "','" + getMacAddr() + "', '" + orientation() + "','" + fingerprintPlaces.getSelectedItem().toString() + "','" + comments.getText() + "');";
                break;
            case 3:
                insert2 = "insert ignore into fingerprints (x_pos, y_pos, band, ap_1, ss_1, ap_2, ss_2, ap_3, ss_3, date, device_info, device_mac, orientation, fingerprint_area, comments) " +
                        "values ('" + xPos.getText() + "','" + yPos.getText() + "','" + "2.4" + "','" + results2.get(0).BSSID + "' ,'" + results2.get(0).level + "','" + results2.get(1).BSSID + "','" + results2.get(1).level + "','" + results2.get(2).BSSID + "','" + results2.get(2).level + "'," +
                        "'" + Calendar.getInstance().getTime() + "'," + "'" + Build.MANUFACTURER + " " + Build.MODEL + "','" + getMacAddr() + "', '" + orientation() + "','" + fingerprintPlaces.getSelectedItem().toString() + "','" + comments.getText() + "');";
                break;
            case 4:
                insert2 = "insert ignore into fingerprints (x_pos, y_pos, band, ap_1, ss_1, ap_2, ss_2, ap_3, ss_3, ap_4, ss_4, date, device_info,  device_mac,orientation, fingerprint_area, comments) " +
                        "values ('" + xPos.getText() + "','" + yPos.getText() + "','" + "2.4" + "','" + results2.get(0).BSSID + "' ,'" + results2.get(0).level + "','" + results2.get(1).BSSID + "','" + results2.get(1).level + "','" + results2.get(2).BSSID + "','" + results2.get(2).level + "','" + results2.get(3).BSSID + "','" + results2.get(3).level + "','" + Calendar.getInstance().getTime() + "'," + "'" + Build.MANUFACTURER + " " + Build.MODEL + "','" + getMacAddr() + "', '" + orientation() + "','" + fingerprintPlaces.getSelectedItem().toString() + "','" + comments.getText() + "');";
                break;
            case 5:
                insert2 = "insert ignore into fingerprints (x_pos, y_pos, band, ap_1, ss_1, ap_2, ss_2, ap_3, ss_3, ap_4, ss_4, ap_5, ss_5, date, device_info, device_mac, orientation, fingerprint_area, comments) " +
                        "values ('" + xPos.getText() + "','" + yPos.getText() + "','" + "2.4" + "','" + results2.get(0).BSSID + "' ,'" + results2.get(0).level + "','" + results2.get(1).BSSID + "','" + results2.get(1).level + "','" + results2.get(2).BSSID + "','" + results2.get(2).level + "','" + results2.get(3).BSSID + "','" + results2.get(3).level + "','" + results2.get(4).BSSID + "','" + results2.get(4).level + "','" + Calendar.getInstance().getTime() + "'," + "'" + Build.MANUFACTURER + " " + Build.MODEL + "','" + getMacAddr() + "', '" + orientation() + "','" + fingerprintPlaces.getSelectedItem().toString() + "','" + comments.getText() + "');";
                break;
            default:
                insert2 = "insert ignore into fingerprints (x_pos, y_pos, band, ap_1, ss_1, ap_2, ss_2, ap_3, ss_3, ap_4, ss_4, ap_5, ss_5, ap_6, ss_6, date, device_info, device_mac, orientation, fingerprint_area, comments) " +
                        "values ('" + xPos.getText() + "','" + yPos.getText() + "','" + "2.4" + "','" + results2.get(0).BSSID + "' ,'" + results2.get(0).level + "','" + results2.get(1).BSSID + "','" + results2.get(1).level + "','" + results2.get(2).BSSID + "','" + results2.get(2).level + "','" + results2.get(3).BSSID + "','" + results2.get(3).level + "','" + results2.get(4).BSSID + "','" + results2.get(4).level + "','" + results2.get(5).BSSID + "','" + results2.get(5).level + "','" + Calendar.getInstance().getTime() + "'," + "'" + Build.MANUFACTURER + " " + Build.MODEL + "','" + getMacAddr() + "', '" + orientation() + "','" + fingerprintPlaces.getSelectedItem().toString() + "','" + comments.getText() + "');";
                break;
        }

        switch (results5.size()) {
            case 0:
                insert5 = "";
                break;
            case 1:
                insert5 = "insert ignore into fingerprints (x_pos, y_pos, band, ap_1, ss_1, date, device_info, device_mac, orientation, fingerprint_area, comments) " +
                        "values ('" + xPos.getText() + "','" + yPos.getText() + "','" + "5" + "','" + results5.get(0).BSSID + "' ,'" + results5.get(0).level + "','" + Calendar.getInstance().getTime() + "'," +
                        "'" + Build.MANUFACTURER + " " + Build.MODEL + "','" + getMacAddr() + "', '" + orientation() + "','" + fingerprintPlaces.getSelectedItem().toString() + "','" + comments.getText() + "')";
                break;
            case 2:
                insert5 = "insert ignore into fingerprints (x_pos, y_pos, band, ap_1, ss_1, ap_2, ss_2, date, device_info, device_mac, orientation, fingerprint_area, comments) " +
                        "values ('" + xPos.getText() + "','" + yPos.getText() + "','" + "5" + "','" + results5.get(0).BSSID + "' ,'" + results5.get(0).level + "','" + results5.get(1).BSSID + "','" + results5.get(1).level + "' ,'" + Calendar.getInstance().getTime() + "'," +
                        "'" + Build.MANUFACTURER + " " + Build.MODEL + "','" + getMacAddr() + "', '" + orientation() + "','" + fingerprintPlaces.getSelectedItem().toString() + "','" + comments.getText() + "')";
                break;
            case 3:
                insert5 = "insert ignore into fingerprints (x_pos, y_pos, band, ap_1, ss_1, ap_2, ss_2, ap_3, ss_3, date, device_info, device_mac, orientation, fingerprint_area, comments) " +
                        "values ('" + xPos.getText() + "','" + yPos.getText() + "','" + "5" + "','" + results5.get(0).BSSID + "' ,'" + results5.get(0).level + "','" + results5.get(1).BSSID + "','" + results5.get(1).level + "','" + results5.get(2).BSSID + "','" + results5.get(2).level + "'," +
                        "'" + Calendar.getInstance().getTime() + "'," + "'" + Build.MANUFACTURER + " " + Build.MODEL + "','" + getMacAddr() + "', '" + orientation() + "','" + fingerprintPlaces.getSelectedItem().toString() + "','" + comments.getText() + "')";
                break;
            case 4:
                insert5 = "insert ignore into fingerprints (x_pos, y_pos, band, ap_1, ss_1, ap_2, ss_2, ap_3, ss_3, ap_4, ss_4, date, device_info, device_mac, orientation, fingerprint_area, comments) " +
                        "values ('" + xPos.getText() + "','" + yPos.getText() + "','" + "5" + "','" + results5.get(0).BSSID + "' ,'" + results5.get(0).level + "','" + results5.get(1).BSSID + "','" + results5.get(1).level + "','" + results5.get(2).BSSID + "','" + results5.get(2).level + "','" + results5.get(3).BSSID + "','" + results5.get(3).level + "','" + Calendar.getInstance().getTime() + "'," + "'" + Build.MANUFACTURER + " " + Build.MODEL + "','" + getMacAddr() + "', '" + orientation() + "','" + fingerprintPlaces.getSelectedItem().toString() + "','" + comments.getText() + "')";
                break;
            case 5:
                insert5 = "insert ignore into fingerprints (x_pos, y_pos, band, ap_1, ss_1, ap_2, ss_2, ap_3, ss_3, ap_4, ss_4, ap_5, ss_5, date, device_info, device_mac, orientation, fingerprint_area, comments) " +
                        "values ('" + xPos.getText() + "','" + yPos.getText() + "','" + "5" + "','" + results5.get(0).BSSID + "' ,'" + results5.get(0).level + "','" + results5.get(1).BSSID + "','" + results5.get(1).level + "','" + results5.get(2).BSSID + "','" + results5.get(2).level + "','" + results5.get(3).BSSID + "','" + results5.get(3).level + "','" + results5.get(4).BSSID + "','" + results5.get(4).level + "','" + Calendar.getInstance().getTime() + "'," + "'" + Build.MANUFACTURER + " " + Build.MODEL + "','" + getMacAddr() + "', '" + orientation() + "','" + fingerprintPlaces.getSelectedItem().toString() + "','" + comments.getText() + "')";
                break;
            default:
                insert5 = "insert ignore into fingerprints (x_pos, y_pos, band, ap_1, ss_1, ap_2, ss_2, ap_3, ss_3, ap_4, ss_4, ap_5, ss_5, ap_6, ss_6, date, device_info, device_mac, orientation, fingerprint_area, comments) " +
                        "values ('" + xPos.getText() + "','" + yPos.getText() + "','" + "5" + "','" + results5.get(0).BSSID + "' ,'" + results5.get(0).level + "','" + results5.get(1).BSSID + "','" + results5.get(1).level + "','" + results5.get(2).BSSID + "','" + results5.get(2).level + "','" + results5.get(3).BSSID + "','" + results5.get(3).level + "','" + results5.get(4).BSSID + "','" + results5.get(4).level + "','" + results5.get(5).BSSID + "','" + results5.get(5).level + "','" + Calendar.getInstance().getTime() + "'," + "'" + Build.MANUFACTURER + " " + Build.MODEL + "','" + getMacAddr() + "', '" + orientation() + "','" + fingerprintPlaces.getSelectedItem().toString() + "','" + comments.getText() + "')";
                break;
        }

    }
    String orientation(){
        if(radioVertical.isChecked()){
            return "vertical";
        }else return "horizontal";
    }

    public void updateApList(){
        if (!StringUtils.isNullOrEmpty(WiFiScannerActivity.DB_URL) || !StringUtils.isNullOrEmpty(WiFiScannerActivity.USER) || !StringUtils.isNullOrEmpty(WiFiScannerActivity.PASS)){
            for(int x = 0; x < results.size(); x++){
                scanIndex = x;

                String insert = "insert ignore into access_points (mac, ssid, frequency, channel, channel_width, first_seen ) " +
                        "values ('" + results.get(scanIndex).BSSID + "','" + results.get(scanIndex).SSID + "','" + results.get(scanIndex).frequency + "','" + wiFiScannerDetails.freqToChannel(results.get(scanIndex).frequency) + "' ,'" + wiFiScannerActivity.CHANNEL_WIDTH(results.get(scanIndex).channelWidth) + "', '" + Calendar.getInstance().getTime() + "')";
                DatabaseSend objSend = new DatabaseSend();
                objSend.setData(WiFiScannerActivity.DB_URL, WiFiScannerActivity.USER, WiFiScannerActivity.PASS, insert);
                DatabaseSend.SendUpdate objSendUpdate = objSend.new SendUpdate();
                objSendUpdate.execute("");
            }
            prepareInsertStatement();
            String insert = insert2 + insert5;

            DatabaseSend objSend = new DatabaseSend();
            objSend.setData(WiFiScannerActivity.DB_URL, WiFiScannerActivity.USER, WiFiScannerActivity.PASS, insert);
            DatabaseSend.SendUpdate objSendUpdate = objSend.new SendUpdate();
            objSendUpdate.execute("");
            scanButton.setEnabled(true);
        }else {
            Toast.makeText(this, "Uzupełnij dane do logowania w zakładce ABOUT.", Toast.LENGTH_LONG).show();
        }

    }
    public static String getMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(Integer.toHexString(b & 0xFF) + ":");
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
        }
        return "";
    }
}

