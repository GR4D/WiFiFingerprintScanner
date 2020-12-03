package com.example.wififingerprintscanner;

import android.net.wifi.ScanResult;
import android.os.AsyncTask;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.List;

public class FingerprintsSend {

    private List<ScanResult> results;
    private int scanIndex;
    private String DB_URL;
    private String USER;
    private String PASS;
    private String insert2;
    private String insert5;

    public void setData(List<ScanResult> results, int scanIndex, String DB_URL, String USER, String PASS, String insert2, String insert5) {
        this.results = results;
        this.scanIndex = scanIndex;
        this.DB_URL = DB_URL;
        this.USER = USER;
        this.PASS = PASS;
        this.insert2 = insert2;
        this.insert5 = insert5;
    }

    public class SendFingerprint extends AsyncTask<String, String, String> {

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
                    //status = 2;
                }

                Statement stmt = conn.createStatement();

                //new test strings
//                prepareInsertStatement();

                System.out.println(insert2);
                System.out.println(insert5);
                //System.out.println(wiFiFingerprints.orientation());
                System.out.println("szerokosc kanalu: "+results.get(scanIndex).frequency+ "  "+CHANNEL_WIDTH());

                //stmt.executeUpdate(insert2+insert5);
                stmt.executeUpdate(insert2 + insert5);
                System.out.println("Update powiodl sie 2");
                conn.close();

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
        protected void onPostExecute(String msg){
            System.out.println("Post execute");
        }
    }
}
