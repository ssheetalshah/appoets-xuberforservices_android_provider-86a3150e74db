package com.xuber_for_services.provider.Activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.xuber_for_services.provider.Helper.SharedHelper;
import com.xuber_for_services.provider.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jayakumar on 31/01/17.
 */

public class ActivityEmail extends AppCompatActivity {

    ImageView backArrow;
    FloatingActionButton nextICON;
    EditText email;
    TextView register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }
        setContentView(R.layout.activity_email);

        if (Build.VERSION.SDK_INT > 15) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        email = (EditText) findViewById(R.id.email);
        nextICON = (FloatingActionButton) findViewById(R.id.right_arrow);
        backArrow = (ImageView) findViewById(R.id.backArrow);

        register = (TextView) findViewById(R.id.register);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedHelper.putKey(getApplicationContext(), "from", "email");
                SharedHelper.putKey(getApplicationContext(), "email", "" + email.getText().toString());
                Intent mainIntent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(mainIntent);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });


        nextICON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (email.getText().toString().equals("") || email.getText().toString().equalsIgnoreCase(getString(R.string.sample_mail_id))) {

                    displayMessage(getString(R.string.email_validation));

                } else {

                    if ((!isValidEmail(email.getText().toString()))) {

                        displayMessage(getString(R.string.email_validation));

                    } else {
                        SharedHelper.putKey(ActivityEmail.this, "email", email.getText().toString());
                        Intent mainIntent = new Intent(ActivityEmail.this, ActivityPassword.class);
                        startActivity(mainIntent);
                        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    }
                }
            }
        });

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* SharedHelper.putKey(ActivityEmail.this,"email", "");
                Intent mainIntent = new Intent(ActivityEmail.this, BeginScreen.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mainIntent);

                ActivityEmail.this.finish();*/
                onBackPressed();
            }
        });

    }

    public void displayMessage(String toastString) {
        Snackbar snackbar = Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(ContextCompat.getColor(this, R.color.black));
        TextView textView = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(ContextCompat.getColor(this, R.color.white));
        snackbar.show();
    }

    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }



   /* @Override
    public void onBackPressed() {
        SharedHelper.putKey(ActivityEmail.this,"email", "");
        Intent mainIntent = new Intent(ActivityEmail.this, BeginScreen.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        ActivityEmail.this.finish();
    }*/

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
    }
}