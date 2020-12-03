package com.example.wififingerprintscanner;

import android.net.wifi.ScanResult;
import android.os.AsyncTask;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.List;

public class ApUpdate {

    WiFiScannerDetails wiFiScannerDetails = new WiFiScannerDetails();

    private List<ScanResult> results;
    int scanIndex;

    private String DB_URL;
    private String USER;
    private String PASS;

    public void setData(List<ScanResult> results, int scanIndex, String DB_URL, String USER, String PASS) {
        this.results = results;
        this.scanIndex = scanIndex;
        this.DB_URL = DB_URL;
        this.USER = USER;
        this.PASS = PASS;
    }

    public class SendUpdate extends AsyncTask<String, String, String> {

        //Dodawanie danych o AP do tabeli access_points
        String BSSID = results.get(scanIndex).BSSID;
        String SSID = results.get(scanIndex).SSID;
        int FREQ = results.get(scanIndex).frequency;

        int CHANNEL_WIDTH(){
            switch (results.get(scanIndex).channelWidth) {
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

        @Override
        protected void onPreExecute() {
            System.out.println("Inserting data, preexecute");
        }

        @Override
        protected String doInBackground(String... strings) {
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
                String insert = "insert ignore into access_points (mac, ssid, frequency, channel, channel_width, first_seen ) " +
                        "values ('" + BSSID + "','" + SSID + "','" + FREQ + "','" + wiFiScannerDetails.freqToChannel(FREQ) + "' ,'" + CHANNEL_WIDTH() + "', '" + Calendar.getInstance().getTime() + "')";

                System.out.println("szerokosc kanalu: " + results.get(scanIndex).frequency + "  " + CHANNEL_WIDTH());

                stmt.executeUpdate(insert);

                System.out.println("Update powiodl sie 2");
                conn.close();

                //odkomentowac w oficjalnej wersji
                // switchToMapButton.setEnabled(true);

            } catch (ClassNotFoundException e) {
                System.out.println("class not found exception");
                e.printStackTrace();
            } catch (SQLException throwables) {
                System.out.println("sqlexpception throwablss");
                System.out.println(throwables);
                throwables.printStackTrace();
            }
            return "ok";
        }

        @Override
        protected void onPostExecute(String msg) {
            System.out.println("Post execute");
        }
    }
}