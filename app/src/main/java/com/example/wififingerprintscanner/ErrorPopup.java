package com.example.wififingerprintscanner;

import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class ErrorPopup extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error_popup);
        TextView errors = findViewById(R.id.errorTextView);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            errors.setText(bundle.getString("errorDetails"));
        }

    }
}