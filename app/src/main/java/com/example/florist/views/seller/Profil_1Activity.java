package com.example.florist.views.seller;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;



import com.example.florist.R;

public class Profil_1Activity extends AppCompatActivity {

//    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil1);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView toolbar_title = toolbar.findViewById(R.id.toolbar_title);
        toolbar_title.setText("Akun Saya");
        setSupportActionBar(toolbar);

//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setTitle("Akun Saya");
//        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF")));
//        actionBar.setElevation(2);
    }
}