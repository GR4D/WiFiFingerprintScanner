package com.example.wififingerprintscanner;

import android.os.AsyncTask;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseSend {

    private String DB_URL;
    private String USER;
    private String PASS;
    private String insert;

    public void setData(String DB_URL, String USER, String PASS, String insert) {
        this.DB_URL = DB_URL;
        this.USER = USER;
        this.PASS = PASS;
        this.insert = insert;
    }

    public class SendUpdate extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);

                if (conn == null) {

                }else{
                    Statement stmt = conn.createStatement();
                    stmt.executeUpdate(insert);
                    conn.close();
                }

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            return "ok";
        }

        @Override
        protected void onPostExecute(String msg) {
        }
    }
}