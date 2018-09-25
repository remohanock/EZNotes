package com.remo.material.easynotes.activities;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.remo.material.easynotes.R;

public class SplashScreenActivity extends AppCompatActivity {

    Typeface typeface;
    TextView tv_notes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        typeface = Typeface.createFromAsset(getAssets(), "fonts/PT_Serif_Web_Regular.ttf");
        tv_notes = findViewById(R.id.tv_notes);
        tv_notes.setTypeface(typeface);
    }
}
