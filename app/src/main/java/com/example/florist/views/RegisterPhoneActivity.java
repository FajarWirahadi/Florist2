package com.example.florist.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.florist.R;
import com.example.florist.model.User;

public class RegisterPhoneActivity extends AppCompatActivity {
    TextView textViewHintPhoneNumber;
    EditText editTextPhoneNumber;
    String phoneNumber;
    Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_phone);

        textViewHintPhoneNumber = findViewById(R.id.textViewHintPhoneNumber);
        editTextPhoneNumber = findViewById(R.id.editTextPhoneNumber);
        btnRegister = findViewById(R.id.btnRegister);

        Bundle extras = getIntent().getExtras();
        if (extras!=null) {
            phoneNumber = extras.getString("phoneNumber");
            editTextPhoneNumber.setText(phoneNumber.substring(3, phoneNumber.length()));
            editTextPhoneNumber.requestFocus();
        }
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phoneNumber = (textViewHintPhoneNumber.getText().toString()
                        +(editTextPhoneNumber.getText().toString()));
                Intent intent = new Intent(RegisterPhoneActivity.this, RegisterActivity.class);
                intent.putExtra("phoneNumber", phoneNumber);

                if (editTextPhoneNumber.getText().toString().isEmpty()) {
                    editTextPhoneNumber.setBackground(AppCompatResources.getDrawable(RegisterPhoneActivity.this ,R.drawable.rounded_error_edittext));
                } else {
                    RegisterPhoneActivity.this.startActivity(intent);
                }
            }
        });




    }
}