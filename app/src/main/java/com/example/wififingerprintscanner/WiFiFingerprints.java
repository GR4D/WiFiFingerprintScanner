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
    private final ArrayList<String> arrayList = new ArrayList<>();
    public int scanIndex = 0;
    private PaintView mapa;
    public EditText xPos;
    public EditText yPos;
    private RadioButton radioVertical;
    private RadioButton radioHorizontal;
    private EditText comments;
    private List<ScanResult> results2 = new ArrayList<>();
    private List<ScanResult> results5 = new ArrayList<>();
    private Spinner fingerprintPlaces;
    private String insert2;
    private String insert5;
    public float test;
    public TextView isErrorViewText;

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

        isErrorViewText = findViewById(R.id.isErrorView);

       //

        mapa = findViewById(R.id.paint_view);
        fingerprintPlaces = findViewById(R.id.fingerprintPlacesList);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.fingerprintPlace, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fingerprintPlaces.setAdapter(adapter2);
        fingerprintPlaces.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                System.out.println("item selected");
                System.out.println(fingerprintPlaces.getSelectedItem().toString());
                System.out.println(fingerprintPlaces.getSelectedItemPosition());
                if(fingerprintPlaces.getSelectedItemPosition() == 0){
                    //mapa.setImageResource(R.drawable.plan);
                    //mapa.setBackground(R.drawable.plan);
                    mapa.setBackgroundResource(R.drawable.plan);
                    System.out.println("Plan 1");
                }else if(fingerprintPlaces.getSelectedItemPosition() == 1){
                    //mapa.setImageResource(R.drawable.plan2);
                    mapa.setBackgroundResource(R.drawable.plan2);
                    //mapa.setBac
                    System.out.println("PLan 2");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        scanButton = findViewById(R.id.scanBtn);
        comments = findViewById(R.id.commentsText);
        comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                comments.setText(null);
            }
        });

        scanButton = findViewById(R.id.fingerprintButton);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendButton.setEnabled(false);
                scanWifi();
                System.out.println(fingerprintPlaces.getSelectedItem().toString());
            }
        });

        sendButton = findViewById(R.id.sendButton);
        sendButton.setEnabled(false);
        sendButton.setOnClickListener(view -> {
            if(results != null){
                scanButton.setEnabled(false);

                System.out.println(" WYKONALO SIE ");

                updateApList();
                sendButton.setEnabled(false);

            }else System.out.println(" RESULTS NULLLLLLLLL");
        });

        radioVertical = findViewById(R.id.radioVertical);
        radioHorizontal = findViewById(R.id.radioHorizontal);
        comments = findViewById(R.id.commentsText);

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);


        if (!wifiManager.isWifiEnabled()) {
            Toast.makeText(this, "You need to enable WiFi in order to perform a fingerprint scan.", Toast.LENGTH_LONG).show();
            wifiManager.setWifiEnabled(true);
        }


       // Oznaczanie pozycji na planie i przeskalowanie (0-100)
//        mapa.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                float x = (motionEvent.getX()/mapa.getWidth())*100;
//                float y = (motionEvent.getY()/mapa.getHeight())*100;
//
                xPos = findViewById(R.id.xPos);
                yPos = findViewById(R.id.yPos);

//                System.out.println("X: "+x+" Y: "+y);
//
//                xPos.setText(String.valueOf(Math.round(x)));
//                yPos.setText(String.valueOf(Math.round(y)));
//
//                return false;
//            }
//        });
        PaintView paintView = (PaintView)findViewById(R.id.paint_view);
        paintView.setOnTouchListener(paintView);

    }

    public void scanWifi() {

            PaintView paintView = (PaintView)findViewById(R.id.paint_view);
            System.out.println(paintView.xX);
        if (paintView.isClicked){
//            x = (paintView.mX/mapa.getWidth()) * 100;
//            y = (paintView.mY/mapa.getHeight()) * 100;

            xPos.setText(String.valueOf(paintView.xX));
            yPos.setText(String.valueOf(paintView.yY));

            arrayList.clear();
            System.out.println("Test");
            registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ){
                if(!wifiManager.startScan() ){
                    Toast.makeText(this, "Należy odczekać 2 minuty aby ponowić skanowanie...", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this, "Scanning WiFi ...", Toast.LENGTH_SHORT).show();
                    System.out.println("Skanowanie prawidlowe");
                }
            }else if(shouldShowRequestPermissionRationale(String.valueOf(PackageManager.PERMISSION_GRANTED))){
                Toast.makeText(this, "Error: 1 ", Toast.LENGTH_SHORT).show();
                System.out.println("Nalezy wyswietlic UI.");
            }else{
                Toast.makeText(this, "Brak dostepu do lokalizacji.", Toast.LENGTH_SHORT).show();
                System.out.println("Nalezy nadac uprawnienia");
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
    
    public void prepareInsertStatement(){ //DODAC ADRESY MAC TELEFONU
        switch (results2.size()) {
            case 0:
                insert2 = "";
                break;
            //wifiManager.getConnectionInfo().getMacAddress()
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
       // isSetError = false;
        for(int x = 0; x < results.size(); x++){
            scanIndex = x;
//            Update objUpdate = new Update();
//            objUpdate.execute("");
            //aktualizacja ap
            String insert = "insert ignore into access_points (mac, ssid, frequency, channel, channel_width, first_seen ) " +
                    "values ('" + results.get(scanIndex).BSSID + "','" + results.get(scanIndex).SSID + "','" + results.get(scanIndex).frequency + "','" + wiFiScannerDetails.freqToChannel(results.get(scanIndex).frequency) + "' ,'" + wiFiScannerActivity.CHANNEL_WIDTH(results.get(scanIndex).channelWidth) + "', '" + Calendar.getInstance().getTime() + "')";
            DatabaseSend objSend = new DatabaseSend();
            objSend.setData(wiFiScannerActivity.DB_URL, wiFiScannerActivity.USER, wiFiScannerActivity.PASS, insert);
            DatabaseSend.SendUpdate objSendUpdate = objSend.new SendUpdate();
            objSendUpdate.execute("");


        }
        //insert pomiary
        prepareInsertStatement();
        String insert = insert2 + insert5;

        DatabaseSend objSend = new DatabaseSend();
        objSend.setData(wiFiScannerActivity.DB_URL, wiFiScannerActivity.USER, wiFiScannerActivity.PASS, insert);
        DatabaseSend.SendUpdate objSendUpdate = objSend.new SendUpdate();
        objSendUpdate.execute("");
        scanButton.setEnabled(true);

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

