package com.example.florist.views.seller.createshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.florist.R;
import com.example.florist.utils.MyUtils;
import com.example.florist.viewmodels.SellerViewModel;
import com.example.florist.views.RegisterPhoneActivity;

public class CreateShop_2Activity extends AppCompatActivity {

    Button btnNext;
    ImageView btnBack;
    EditText editTextShopName, editTextUsername;

    SellerViewModel sViewModel;
    MyUtils myUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_shop2);

        btnBack = findViewById(R.id.btnBack);
        btnNext = findViewById(R.id.btnNext);
        editTextShopName = findViewById(R.id.editTextShopName);
        editTextUsername = findViewById(R.id.editTextUsername);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView toolbar_title = toolbar.findViewById(R.id.toolbar_title);
        toolbar_title.setText(R.string.buka_toko);
        setSupportActionBar(toolbar);

//        editTextShopName.addTextChangedListener(new MyUtils.GenericTextWatcher(
//                new EditText[]{editTextShopName, editTextShopName}, new int[]{R.id.editTextShopName, R.id.editTextUsername}, CreateShop_2Activity.this, btnNext));

        new MyUtils.MultiTextWatcher()
                .registerEditText(editTextShopName)
                .registerEditText(editTextUsername)
                        .setCallback(new MyUtils.MultiTextWatcher.TextWatcherWithInstance() {
                            @Override
                            public void beforeTextChanged(EditText editText, CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(EditText editText, CharSequence s, int start, int before, int count) {

                            }

                            @Override
                            public void afterTextChanged(EditText editText, Editable editable) {
                            String text = editable.toString();
                            String text2 = editText.toString();



                                if (text.length() >=6 && text2.length() >=6) {

                            } else {
                                btnNext.setBackground(AppCompatResources.getDrawable(CreateShop_2Activity.this, R.drawable.rounded_gray_button));

                            }
                            }
                        });
//        sViewModel.getButtonResult().observe(this, new Observer<Integer>() {
//            @Override
//            public void onChanged(Integer integer) {
//                btnNext.setBackground(AppCompatResources.getDrawable(CreateShop_2Activity.this, integer));
//
//            }
//        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CreateShop_2Activity.this, CreateShop_3Activity.class);
                startActivity(intent);
            }
        });
    }
}