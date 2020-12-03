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

import java.util.ArrayList;
import java.util.Calendar;
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

//    private class Send extends AsyncTask<String, String, String> {
//
//        int CHANNEL_WIDTH(){
//            if (results.get(scanIndex).channelWidth == 0){
//                return 20;
//            }else if (results.get(scanIndex).channelWidth == 1){
//                return 40;
//            }else if (results.get(scanIndex).channelWidth == 2){
//                return 80;
//            }else if (results.get(scanIndex).channelWidth == 3){
//                return 160;
//            }else return 81;
//        }
//
//        @Override
//        protected  void onPreExecute(){
//            System.out.println("Inserting data, preexecute");
//        }
//
//        @Override
//        protected String doInBackground(String... strings){
//            try {
//                System.out.println("error 1");
//                Class.forName("com.mysql.jdbc.Driver");
//                System.out.println("error 2");
//                Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
//
//
//                System.out.println("error 3");
//                System.out.println(Calendar.getInstance().getTime());
//
//                if (conn == null) {
//                    System.out.println("conn error");
//                }
//
//                Statement stmt = conn.createStatement();
//
//                //new test strings
//                prepareInsertStatement();
//
//                System.out.println(insert2);
//                System.out.println(insert5);
//                System.out.println(orientation());
//                System.out.println("szerokosc kanalu: "+results.get(scanIndex).frequency+ "  "+CHANNEL_WIDTH());
//
//                stmt.executeUpdate(insert2+insert5);
//
//                System.out.println("Update powiodl sie 2");
//                conn.close();
//
//            } catch (ClassNotFoundException e) {
//                System.out.println("class not found exception");
//                e.printStackTrace();
//                Intent intent = new Intent(WiFiFingerprints.this, ErrorPopup.class);
//                intent.putExtra("errorDetails", e.toString());
//                startActivity(intent);
//            } catch (SQLException throwables) {
//                System.out.println("sqlexpception throwablss");
//                System.out.println(throwables);
//                Intent intent = new Intent(WiFiFingerprints.this, ErrorPopup.class);
//                intent.putExtra("errorDetails", throwables.toString());
//                startActivity(intent);
//                throwables.printStackTrace();
//
//            }
//            return "ok";
//        }
//        @Override
//        protected void onPostExecute(String msg){
//            System.out.println("Post execute");
//            scanButton.setEnabled(true);
//        }
//    }

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
        if(results2.size() == 0){
             insert2 = "";
        }
        else if (results2.size() == 1){
             insert2 = "insert ignore into fingerprints (x_pos, y_pos, band, ap_1, ss_1, date, device_info, orientation, fingerprint_area, comments) " +
            "values ('"+xPos.getText()+"','"+yPos.getText()+"','"+"2.4"+"','"+results2.get(0).BSSID+"' ,'"+results2.get(0).level+"','"+Calendar.getInstance().getTime()+"'," +
            "'"+ Build.MANUFACTURER+" "+Build.MODEL+"'," +"'"+orientation()+"','"+fingerprintPlaces.getSelectedItem().toString()+"','"+comments.getText()+"');";
        }else if(results2.size() == 2){
             insert2 = "insert ignore into fingerprints (x_pos, y_pos, band, ap_1, ss_1, ap_2, ss_2, date, device_info, orientation, fingerprint_area, comments) " +
                    "values ('"+xPos.getText()+"','"+yPos.getText()+"','"+"2.4"+"','"+results2.get(0).BSSID+"' ,'"+results2.get(0).level+"','"+results2.get(1).BSSID+"','"+results2.get(1).level+"' ,'"+Calendar.getInstance().getTime()+"'," +
                    "'"+ Build.MANUFACTURER+" "+Build.MODEL+"'," +"'"+orientation()+"','"+fingerprintPlaces.getSelectedItem().toString()+"','"+comments.getText()+"');";
        }else if(results2.size() == 3){
             insert2 = "insert ignore into fingerprints (x_pos, y_pos, band, ap_1, ss_1, ap_2, ss_2, ap_3, ss_3, date, device_info, orientation, fingerprint_area, comments) " +
                    "values ('"+xPos.getText()+"','"+yPos.getText()+"','"+"2.4"+"','"+results2.get(0).BSSID+"' ,'"+results2.get(0).level+"','"+results2.get(1).BSSID+"','"+results2.get(1).level+"','"+results2.get(2).BSSID+"','"+results2.get(2).level+"'," +
                    "'"+Calendar.getInstance().getTime()+"'," + "'"+ Build.MANUFACTURER+" "+Build.MODEL+"'," +"'"+orientation()+"','"+fingerprintPlaces.getSelectedItem().toString()+"','"+comments.getText()+"');";
        }else if(results2.size() == 4){
             insert2 = "insert ignore into fingerprints (x_pos, y_pos, band, ap_1, ss_1, ap_2, ss_2, ap_3, ss_3, ap_4, ss_4, date, device_info, orientation, fingerprint_area, comments) " +
                    "values ('"+xPos.getText()+"','"+yPos.getText()+"','"+"2.4"+"','"+results2.get(0).BSSID+"' ,'"+results2.get(0).level+"','"+results2.get(1).BSSID+"','"+results2.get(1).level+"','"+results2.get(2).BSSID+"','"+results2.get(2).level+"','"+results2.get(3).BSSID+"','"+results2.get(3).level+"','"+Calendar.getInstance().getTime()+"'," + "'"+ Build.MANUFACTURER+" "+Build.MODEL+"'," +"'"+orientation()+"','"+fingerprintPlaces.getSelectedItem().toString()+"','"+comments.getText()+"');";
        }else if(results2.size() == 5){
             insert2 = "insert ignore into fingerprints (x_pos, y_pos, band, ap_1, ss_1, ap_2, ss_2, ap_3, ss_3, ap_4, ss_4, ap_5, ss_5, date, device_info, orientation, fingerprint_area, comments) " +
                    "values ('"+xPos.getText()+"','"+yPos.getText()+"','"+"2.4"+"','"+results2.get(0).BSSID+"' ,'"+results2.get(0).level+"','"+results2.get(1).BSSID+"','"+results2.get(1).level+"','"+results2.get(2).BSSID+"','"+results2.get(2).level+"','"+results2.get(3).BSSID+"','"+results2.get(3).level+"','"+results2.get(4).BSSID+"','"+results2.get(4).level+"','"+Calendar.getInstance().getTime()+"'," + "'"+ Build.MANUFACTURER+" "+Build.MODEL+"'," +"'"+orientation()+"','"+fingerprintPlaces.getSelectedItem().toString()+"','"+comments.getText()+"');";
        }else{
             insert2 = "insert ignore into fingerprints (x_pos, y_pos, band, ap_1, ss_1, ap_2, ss_2, ap_3, ss_3, ap_4, ss_4, ap_5, ss_5, ap_6, ss_6, date, device_info, orientation, fingerprint_area, comments) " +
                    "values ('"+xPos.getText()+"','"+yPos.getText()+"','"+"2.4"+"','"+results2.get(0).BSSID+"' ,'"+results2.get(0).level+"','"+results2.get(1).BSSID+"','"+results2.get(1).level+"','"+results2.get(2).BSSID+"','"+results2.get(2).level+"','"+results2.get(3).BSSID+"','"+results2.get(3).level+"','"+results2.get(4).BSSID+"','"+results2.get(4).level+"','"+results2.get(5).BSSID+"','"+results2.get(5).level+"','"+Calendar.getInstance().getTime()+"'," + "'"+ Build.MANUFACTURER+" "+Build.MODEL+"'," +"'"+orientation()+"','"+fingerprintPlaces.getSelectedItem().toString()+"','"+comments.getText()+"');";
        }

        if(results5.size() == 0){
             insert5 = "";
        }else if(results5.size() == 1){
             insert5 = "insert ignore into fingerprints (x_pos, y_pos, band, ap_1, ss_1, date, device_info, orientation, fingerprint_area, comments) " +
                    "values ('"+xPos.getText()+"','"+yPos.getText()+"','"+"5"+"','"+results5.get(0).BSSID+"' ,'"+results5.get(0).level+"','"+Calendar.getInstance().getTime()+"'," +
                    "'"+ Build.MANUFACTURER+" "+Build.MODEL+"'," +"'"+orientation()+"','"+fingerprintPlaces.getSelectedItem().toString()+"','"+comments.getText()+"')";
        }else if(results5.size() == 2){
             insert5 = "insert ignore into fingerprints (x_pos, y_pos, band, ap_1, ss_1, ap_2, ss_2, date, device_info, orientation, fingerprint_area, comments) " +
                    "values ('"+xPos.getText()+"','"+yPos.getText()+"','"+"5"+"','"+results5.get(0).BSSID+"' ,'"+results5.get(0).level+"','"+results5.get(1).BSSID+"','"+results5.get(1).level+"' ,'"+Calendar.getInstance().getTime()+"'," +
                    "'"+ Build.MANUFACTURER+" "+Build.MODEL+"'," +"'"+orientation()+"','"+fingerprintPlaces.getSelectedItem().toString()+"','"+comments.getText()+"')";
        }else if(results5.size() == 3){
             insert5 = "insert ignore into fingerprints (x_pos, y_pos, band, ap_1, ss_1, ap_2, ss_2, ap_3, ss_3, date, device_info, orientation, fingerprint_area, comments) " +
                    "values ('"+xPos.getText()+"','"+yPos.getText()+"','"+"5"+"','"+results5.get(0).BSSID+"' ,'"+results5.get(0).level+"','"+results5.get(1).BSSID+"','"+results5.get(1).level+"','"+results5.get(2).BSSID+"','"+results5.get(2).level+"'," +
                    "'"+Calendar.getInstance().getTime()+"'," + "'"+ Build.MANUFACTURER+" "+Build.MODEL+"'," +"'"+orientation()+"','"+fingerprintPlaces.getSelectedItem().toString()+"','"+comments.getText()+"')";
        }else if(results5.size() == 4){
             insert5 = "insert ignore into fingerprints (x_pos, y_pos, band, ap_1, ss_1, ap_2, ss_2, ap_3, ss_3, ap_4, ss_4, date, device_info, orientation, fingerprint_area, comments) " +
                    "values ('"+xPos.getText()+"','"+yPos.getText()+"','"+"5"+"','"+results5.get(0).BSSID+"' ,'"+results5.get(0).level+"','"+results5.get(1).BSSID+"','"+results5.get(1).level+"','"+results5.get(2).BSSID+"','"+results5.get(2).level+"','"+results5.get(3).BSSID+"','"+results5.get(3).level+"','"+Calendar.getInstance().getTime()+"'," + "'"+ Build.MANUFACTURER+" "+Build.MODEL+"'," +"'"+orientation()+"','"+fingerprintPlaces.getSelectedItem().toString()+"','"+comments.getText()+"')";
        }else if(results5.size() == 5){
             insert5 = "insert ignore into fingerprints (x_pos, y_pos, band, ap_1, ss_1, ap_2, ss_2, ap_3, ss_3, ap_4, ss_4, ap_5, ss_5, date, device_info, orientation, fingerprint_area, comments) " +
                    "values ('"+xPos.getText()+"','"+yPos.getText()+"','"+"5"+"','"+results5.get(0).BSSID+"' ,'"+results5.get(0).level+"','"+results5.get(1).BSSID+"','"+results5.get(1).level+"','"+results5.get(2).BSSID+"','"+results5.get(2).level+"','"+results5.get(3).BSSID+"','"+results5.get(3).level+"','"+results5.get(4).BSSID+"','"+results5.get(4).level+"','"+Calendar.getInstance().getTime()+"'," + "'"+ Build.MANUFACTURER+" "+Build.MODEL+"'," +"'"+orientation()+"','"+fingerprintPlaces.getSelectedItem().toString()+"','"+comments.getText()+"')";
        }else{
             insert5 = "insert ignore into fingerprints (x_pos, y_pos, band, ap_1, ss_1, ap_2, ss_2, ap_3, ss_3, ap_4, ss_4, ap_5, ss_5, ap_6, ss_6, date, device_info, orientation, fingerprint_area, comments) " +
                    "values ('"+xPos.getText()+"','"+yPos.getText()+"','"+"5"+"','"+results5.get(0).BSSID+"' ,'"+results5.get(0).level+"','"+results5.get(1).BSSID+"','"+results5.get(1).level+"','"+results5.get(2).BSSID+"','"+results5.get(2).level+"','"+results5.get(3).BSSID+"','"+results5.get(3).level+"','"+results5.get(4).BSSID+"','"+results5.get(4).level+"','"+results5.get(5).BSSID+"','"+results5.get(5).level+"','"+Calendar.getInstance().getTime()+"'," + "'"+ Build.MANUFACTURER+" "+Build.MODEL+"'," +"'"+orientation()+"','"+fingerprintPlaces.getSelectedItem().toString()+"','"+comments.getText()+"')";
        }

    }
    String orientation(){
        if(radioVertical.isChecked()){
            return "vertical";
        }else return "horizontal";
    }

//    private class Update extends AsyncTask<String, String, String>{
//
//        //Dodawanie danych o AP do tabeli access_points
//        String BSSID = results.get(scanIndex).BSSID;
//        String SSID = results.get(scanIndex).SSID;
//        int FREQ = results.get(scanIndex).frequency;
//        int CHANNEL_WIDTH(){
//            switch (results.get(scanIndex).channelWidth) {
//                case 0:
//                    return 20;
//                case 1:
//                    return 40;
//                case 2:
//                    return 80;
//                case 3:
//                    return 160;
//                default:
//                    return 81;
//            }
//        }
//
//        @Override
//        protected  void onPreExecute(){
//            System.out.println("Inserting data, preexecute");
//        }
//
//        @Override
//        protected String doInBackground(String... strings){
//            try {
//                System.out.println("error 1");
//                Class.forName("com.mysql.jdbc.Driver");
//                System.out.println("error 2");
//                Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
//                System.out.println("error 3");
//                System.out.println(Calendar.getInstance().getTime());
//
//                if (conn == null) {
//                    System.out.println("conn error");
//                }
//                Statement stmt = conn.createStatement();
//                String insert = "insert ignore into access_points (mac, ssid, frequency, channel, channel_width, first_seen ) " +
//                        "values ('"+BSSID+"','"+SSID+"','"+FREQ+"','"+wiFiScannerDetails.freqToChannel(FREQ)+"' ,'"+CHANNEL_WIDTH()+"', '"+ Calendar.getInstance().getTime()+ "')";
//
//                System.out.println("szerokosc kanalu: "+results.get(scanIndex).frequency+ "  "+CHANNEL_WIDTH());
//
//                stmt.executeUpdate(insert);
//
//                System.out.println("Update powiodl sie 2");
//                conn.close();
//
//            } catch (ClassNotFoundException e) {
//                System.out.println("class not found exception");
//                e.printStackTrace();
//            } catch (SQLException throwables) {
//                System.out.println("sqlexpception throwablss");
//                System.out.println(throwables);
//                throwables.printStackTrace();
//            }
//            return "ok";
//        }
//        @Override
//        protected void onPostExecute(String msg){
//            System.out.println("Post execute");
//        }
//    }

    public void updateApList(){
       // isSetError = false;
        for(int x = 0; x < results.size(); x++){
            scanIndex = x;
//            Update objUpdate = new Update();
//            objUpdate.execute("");
            //aktualizacja ap
            ApUpdate objSend = new ApUpdate();
            objSend.setData(results, scanIndex, wiFiScannerActivity.DB_URL, wiFiScannerActivity.USER, wiFiScannerActivity.PASS);
            ApUpdate.SendUpdate senddd = objSend.new SendUpdate();
            senddd.execute("");


        }
        //insert pomiary
        prepareInsertStatement();

        FingerprintsSend fingerprintsSend = new FingerprintsSend();
        fingerprintsSend.setData(results, scanIndex, wiFiScannerActivity.DB_URL, wiFiScannerActivity.USER, wiFiScannerActivity.PASS, insert2, insert5);
        FingerprintsSend.SendFingerprint sendFingerprint = fingerprintsSend.new SendFingerprint();
        sendFingerprint.execute("");

        scanButton.setEnabled(true);

    }



}

