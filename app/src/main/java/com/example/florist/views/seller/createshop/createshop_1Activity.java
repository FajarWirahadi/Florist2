package com.example.florist.views.seller.createshop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.florist.R;
import com.example.florist.views.Login;
import com.example.florist.views.homepage.HomepageActivity;

public class createshop_1Activity extends AppCompatActivity {

    Button btnCreateShop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createshop1);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView toolbar_title = toolbar.findViewById(R.id.toolbar_title);
        toolbar_title.setText(R.string.buka_toko);
        setSupportActionBar(toolbar);

        btnCreateShop = findViewById(R.id.btnCreateShop);

        btnCreateShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(createshop_1Activity.this, CreateShop_2Activity.class);
                startActivity(intent);
            }
        });
    }
}