package com.example.wififingerprintscanner;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.annotation.Nullable;

public class PopupPosition extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popupposition);
        ImageView mapView = findViewById(R.id.mapView);

        int imageResource = getResources().getIdentifier("@drawable/planbudynku", null, this.getPackageName());
        mapView.setImageResource(imageResource);
    }
}
