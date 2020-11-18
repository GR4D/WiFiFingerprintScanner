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
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class WiFiScannerActivity extends AppCompatActivity {

    private WifiManager wifiManager;
    private ListView wifiList;
    private Button buttonScan;
    private Button apButton;
    private Button switchMap;
    private List<ScanResult> results;
    private ArrayList<String> arrayList = new ArrayList<>();
    private ArrayAdapter adapter;
    private static final String DB_URL = "jdbc:mysql://192.168.0.2:3306/wifi_fingerprint_db";
    private static final String USER = "gr4d";
    private static final String PASS = "172216";
    public int scanIndex = 0;
    private TextView errorsView;
    private ResultSet query = null;

    WiFiScannerDetails wiFiScannerDetails = new WiFiScannerDetails();

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

        apButton = findViewById(R.id.listBtn);
        apButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if(results != null){
                   updateApList();
                   System.out.println("not null");
               }else{
                   System.out.println("null");
               }
            }
        });

        switchMap = findViewById(R.id.switchTest);
        switchMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mapa = new Intent(WiFiScannerActivity.this, WiFiFingerprints.class);

                startActivity(mapa);
            }
        });
        //errorsView = findViewById(R.id.errorView);

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

                scanIndex = i;
                Send objSend = new Send();
                objSend.execute("");

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
           Toast.makeText(this, "Error: 1 ", Toast.LENGTH_SHORT).show();
           System.out.println("Nalezy wyswietlic UI.");
       }else{
           Toast.makeText(this, "Brak dostepu do lokalizacji.", Toast.LENGTH_SHORT).show();
           System.out.println("Nalezy nadac uprawnienia");
       }
    }

    private class Send extends AsyncTask<String, String, String>{
        
        //Dodawanie danych o AP do tabeli access_points

        String msg = "";
        String BSSID = results.get(scanIndex).BSSID;
        String SSID = results.get(scanIndex).SSID;
        int FREQ = results.get(scanIndex).frequency;
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
                String insert = "insert ignore into access_points (mac, ssid, frequency, channel, channel_width, last_seen ) " +
                    "values ('"+BSSID+"','"+SSID+"','"+FREQ+"','"+wiFiScannerDetails.freqToChannel(FREQ)+"' ,'"+CHANNEL_WIDTH()+"', '"+ Calendar.getInstance().getTime()+ "')";

                System.out.println("szerokosc kanalu: "+results.get(scanIndex).frequency+ "  "+CHANNEL_WIDTH());

                stmt.executeUpdate(insert);

                System.out.println("Update powiodl sie 2");
                conn.close();

            } catch (ClassNotFoundException e) {
                System.out.println("class not found exception");
                e.printStackTrace();
            } catch (SQLException throwables) {
                System.out.println("sqlexpception throwablss");
                System.out.println(throwables);
                errorsView.setText(throwables.toString());
                throwables.printStackTrace();
            }
            return "ok";
        }
        @Override
        protected void onPostExecute(String msg){
            System.out.println("Post execute");
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
            errorsView.append(user+" "+website+" "+summary+"\n");
        }
    }

    public void updateApList(){
        for(int x = 0; x < results.size(); x++){
            scanIndex = x;
            Send objSend = new Send();
            objSend.execute("");
        }

    }
}