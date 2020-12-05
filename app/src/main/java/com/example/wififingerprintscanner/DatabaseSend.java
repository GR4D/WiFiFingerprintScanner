package com.example.wififingerprintscanner;

import android.os.AsyncTask;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;

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
                }else{
                    Statement stmt = conn.createStatement();
                    stmt.executeUpdate(insert);
                    conn.close();
                }

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