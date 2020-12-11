package com.example.wififingerprintscanner;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class About extends AppCompatActivity {

    protected static String url;
    protected static String username;
    protected static String password;
    protected Button acceptButton;
    protected EditText urlText;
    protected EditText usernameText;
    protected EditText passwordText;
    protected Button defaultButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        urlText = findViewById(R.id.inputUrl);
        usernameText = findViewById(R.id.inputUser);
        passwordText = findViewById(R.id.inputPassword);

        defaultButton = findViewById(R.id.defaultBtn);
        defaultButton.setOnClickListener(view -> setDefaultValues());

        acceptButton = findViewById(R.id.acceptBtn);
        acceptButton.setOnClickListener(view -> {

            url = WiFiScannerActivity.DB_URL_PREFIX + urlText.getText().toString() + WiFiScannerActivity.DB_URL_SUFFIX;
            username = String.valueOf(usernameText.getText());
            password = String.valueOf(passwordText.getText());

            Toast.makeText(this, "Zapisano dane logowania", Toast.LENGTH_SHORT).show();
        });
    }
    public void setDefaultValues(){
        urlText.setText("192.168.0.5:3306");
        usernameText.setText("gr4d");
        passwordText.setText("172216");

    }
}