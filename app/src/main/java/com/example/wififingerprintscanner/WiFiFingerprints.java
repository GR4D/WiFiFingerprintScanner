package com.example.wififingerprintscanner;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class WiFiFingerprints extends AppCompatActivity {

    private WifiManager wifiManager;
    private ListView wifiList;
    private Button fingerprintButton;
    private Button apButton;
    private Button sendButton;
    private List<ScanResult> results;
    private final ArrayList<String> arrayList = new ArrayList<>();
    private ArrayAdapter adapter;
    private static final String DB_URL = "jdbc:mysql://192.168.0.2:3306/wifi_fingerprint_db";
    private static final String USER = "gr4d";
    private static final String PASS = "172216";
    public int scanIndex = 0;
    private TextView errorsView;
    private final ResultSet query = null;
    private ImageView mapa;
    private EditText xPos;
    private EditText yPos;
    private EditText orientationField;
    private RadioButton radioVertical;
    private RadioButton radioHorizontal;
    private EditText comments;
    public List<ScanResult> results2 = new ArrayList<>();
    public List<ScanResult> results5 = new ArrayList<>();


    WiFiScannerDetails wiFiScannerDetails = new WiFiScannerDetails();
    BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            results = wifiManager.getScanResults();
            unregisterReceiver(this);
            sendButton.setEnabled(true);
            filterFrequency();


            for (ScanResult scanResult : results2){
                System.out.println(scanResult.BSSID + " 2.4");
            }

            for (ScanResult scanResult : results5){
                System.out.println(scanResult.BSSID);
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wi_fi_fingerprints);


        fingerprintButton = findViewById(R.id.scanBtn);
        orientationField = findViewById(R.id.commentsText);

        fingerprintButton = findViewById(R.id.fingerprintButton);
        fingerprintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendButton.setEnabled(false);
                scanWifi();
            }
        });

        sendButton = findViewById(R.id.sendButton);
        sendButton.setEnabled(false);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(results != null){
                    fingerprintButton.setEnabled(false);
                    System.out.println(" WYKONALO SIE ");
                    Send objSend = new Send();
                    objSend.execute("");
                    sendButton.setEnabled(false);

                }else System.out.println(" RESULTS NULLLLLLLLL");
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

        mapa = findViewById(R.id.imageMap);
        //Oznaczanie pozycji na planie i przeskalowanie (0-100)
        mapa.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                float x = (motionEvent.getX()/mapa.getWidth())*100;
                float y = (motionEvent.getY()/mapa.getHeight())*100;

                xPos = findViewById(R.id.xPos);
                yPos = findViewById(R.id.yPos);

                System.out.println("X: "+x+" Y: "+y);

                xPos.setText(String.valueOf(Math.round(x)));
                yPos.setText(String.valueOf(Math.round(y)));

                return false;
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
            Toast.makeText(this, "Error: 1 ", Toast.LENGTH_SHORT).show();
            System.out.println("Nalezy wyswietlic UI.");
        }else{
            Toast.makeText(this, "Brak dostepu do lokalizacji.", Toast.LENGTH_SHORT).show();
            System.out.println("Nalezy nadac uprawnienia");
        }
    }

    private class Send extends AsyncTask<String, String, String> {
        String msg = "";
        String BSSID = results.get(scanIndex).BSSID;
        String SSID = results.get(scanIndex).SSID;
        int FREQ = results.get(scanIndex).frequency;
        float BAND(int f){
            if(f > 2350 & f<2550){
                return (float) 2.4;
            }else return  5;
        }

        String orientation(){
            if(radioVertical.isChecked()){
                return "vertical";
            }else return "horizontal";
        }



        int CHANNEL_WIDTH(){
            if (results.get(scanIndex).channelWidth == 0){
                return 20;
            }else if (results.get(scanIndex).channelWidth == 1){
                return 40;
            }else if (results.get(scanIndex).channelWidth == 2){
                return 80;
            }else if (results.get(scanIndex).channelWidth == 3){
                return 160;
            }else return 81;

        }

        @Override
        protected  void onPreExecute(){
            System.out.println("Inserting data, preexecute");
        }

        @Override
        protected String doInBackground(String... strings){
            try {
                System.out.println("error 1");
                Class.forName("com.mysql.jdbc.Driver");
                System.out.println("error 2");
                Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);


                System.out.println("error 3");
                System.out.println(Calendar.getInstance().getTime());

                if (conn == null) {
                    System.out.println("conn error");
                }
                //fillData();
                Statement stmt = conn.createStatement();

                //TODO: poprawic wartosci
//                String insert = "insert ignore into fingerprints (x_pos, y_pos, band, ap_1, ss_1, ap_2, ss_2, ap_3, ss_3, ap_4, ss_4, ap_5, ss_5, ap_6, ss_6, date, device_info, orientation) " +
//                        "values ('"+xPos.getText()+"','"+yPos.getText()+"','"+BAND(FREQ)+"','"+results.get(0).BSSID+"' ,'"+results.get(0).level+"', '"+ results.get(1).BSSID+ "'," +
//                        "'"+results.get(1).level+"','"+results.get(2).BSSID+"','"+results.get(2).level+"','"+results.get(3).BSSID+"','"+results.get(3).level+"','"+results.get(4).BSSID+"'," +
//                        "'"+results.get(4).level+"','"+results.get(5).BSSID+"','"+results.get(5).level+"','"+Calendar.getInstance().getTime()+"','"+ Build.MANUFACTURER+" "+Build.MODEL+"'," +
//                        "'"+orientation()+"')";


                //TEST STRING
                String insert = "insert ignore into fingerprints (x_pos, y_pos, band, ap_1, ss_1, ap_2, ss_2, ap_3, ss_3, ap_4, ss_4, ap_5, ss_5, ap_6, ss_6, date, device_info, orientation, comments) " +
                        "values ('"+xPos.getText()+"','"+yPos.getText()+"','"+BAND(FREQ)+"','"+results.get(0).BSSID+"' ,'"+results.get(0).level+"', '"+ results.get(0).BSSID+ "'," +
                        "'"+results.get(0).level+"','"+results.get(0).BSSID+"','"+results.get(0).level+"','"+results.get(0).BSSID+"','"+results.get(0).level+"','"+results.get(0).BSSID+"'," +
                        "'"+results.get(0).level+"','"+results.get(0).BSSID+"','"+results.get(0).level+"','"+Calendar.getInstance().getTime()+"','"+ Build.MANUFACTURER+" "+Build.MODEL+"'," +
                        "'"+orientation()+"','"+comments.getText()+"')";
                System.out.println(orientation());
                //orientation(orientationField.getText())   zamiast testtest


                System.out.println("szerokosc kanalu: "+results.get(scanIndex).frequency+ "  "+CHANNEL_WIDTH());

                stmt.executeUpdate(insert);

                System.out.println("Update powiodl sie 2");
                conn.close();

            } catch (ClassNotFoundException e) {
                System.out.println("class not found exception");
                e.printStackTrace();
                Intent intent = new Intent(WiFiFingerprints.this, ErrorPopup.class);
                intent.putExtra("errorDetails", e.toString());
                startActivity(intent);
            } catch (SQLException throwables) {
                System.out.println("sqlexpception throwablss");
                System.out.println(throwables);
                Intent intent = new Intent(WiFiFingerprints.this, ErrorPopup.class);
                intent.putExtra("errorDetails", throwables.toString());
                startActivity(intent);
                throwables.printStackTrace();

            }
            return "ok";
        }
        @Override
        protected void onPostExecute(String msg){
            System.out.println("Post execute");
            fingerprintButton.setEnabled(true);
        }
    }
    public void openApList(){
        Intent intent = new Intent(this, WiFiList.class);
        startActivity(intent);
    }

    private void fetchData(ResultSet resultSet) throws SQLException {
        while (resultSet.next()) {

            String user = resultSet.getString("id");
            String website = resultSet.getString("mac");
            String summary = resultSet.getString("ssid");
            String date = resultSet.getString("channel");
            String comment = resultSet.getString("comments");
            System.out.println("User: " + user);
            System.out.println("Website: " + website);
            System.out.println("summary: " + summary);
            System.out.println("Date: " + date);
            System.out.println("Comment: " + comment);
            //errorsView.setText(user+" "+website+" "+summary);
            //errorsView.append(user+" "+website+" "+summary+"\n");
        }
    }

    public void updateApList(){
        for(int x = 0; x < results.size(); x++){
            scanIndex = x;
            WiFiFingerprints.Send objSend = new WiFiFingerprints.Send();
            objSend.execute("");
        }

    }

    public void filterFrequency(){
        if(results != null) {
//            for (int i = 0; i < results.size(); i++) {
//                if(results.get(i).frequency < 2550) {
//                    results2.add(results.get(i));
//                }else{
//                    results5.add()
//                    System.out.println("results 5");
//                }
//
//            }
            for(ScanResult scanResult : results){
                if(scanResult.frequency < 2550){
                    results2.add(scanResult);
                }else
                    results5.add(scanResult);
            }
        }
    }

}