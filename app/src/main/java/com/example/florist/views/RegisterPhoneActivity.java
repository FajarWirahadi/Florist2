package com.example.florist.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.florist.R;

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

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phoneNumber = textViewHintPhoneNumber.getText().toString()
                        +(editTextPhoneNumber.getText().toString());
                Log.d("Phone Number", phoneNumber);
                Log.d("Edit text phone number", editTextPhoneNumber.getText().toString());
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