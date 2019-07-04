package com.xuber_for_services.provider;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class OtppActivity extends AppCompatActivity {
    EditText enterOtp;
    Button verifyBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpp);

        enterOtp = (EditText)findViewById(R.id.enterOtp);
        verifyBtn = (Button)findViewById(R.id.verifyBtn);

        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}
