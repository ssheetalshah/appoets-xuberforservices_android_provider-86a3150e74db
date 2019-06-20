package com.xuber_for_services.provider.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.xuber_for_services.provider.Constant.URLHelper;
import com.xuber_for_services.provider.Helper.ConnectionHelper;
import com.xuber_for_services.provider.Helper.CustomDialog;
import com.xuber_for_services.provider.Helper.SharedHelper;
import com.xuber_for_services.provider.R;
import com.xuber_for_services.provider.XuberServicesApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ChangePassword extends AppCompatActivity {
    String TAG = "ChangePasswordActivity";
    public Context context = ChangePassword.this;
    public Activity activity = ChangePassword.this;
    CustomDialog customDialog;
    ConnectionHelper helper;
    Boolean isInternet;
    Button changePasswordBtn;
    ImageView backArrow;
    EditText new_password, confirm_new_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }
        setContentView(R.layout.change_password);
        findViewByIdandInitialization();

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        changePasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String new_password_value = new_password.getText().toString();
                String confirm_password_value = confirm_new_password.getText().toString();
                if (new_password_value == null || new_password_value.equalsIgnoreCase("")) {
                    displayMessage(getString(R.string.please_enter_new_pass));
                } else if (confirm_password_value == null || confirm_password_value.equalsIgnoreCase("")) {
                    displayMessage(getString(R.string.please_enter_confirm_pass));
                } else if (!new_password_value.equals(confirm_password_value)) {
                    displayMessage(getString(R.string.different_passwords));
                } else if (new_password.getText().toString().length() < 6) {
                    displayMessage(getString(R.string.passwd_length));
                } else if (confirm_new_password.getText().toString().length() < 6) {
                    displayMessage(getString(R.string.passwd_length));
                } else {
                    resetPassword(new_password_value, confirm_password_value);
                }
            }
        });
    }

    public void findViewByIdandInitialization() {
        new_password = (EditText) findViewById(R.id.new_password);
        confirm_new_password = (EditText) findViewById(R.id.confirm_password);
        changePasswordBtn = (Button) findViewById(R.id.changePasswordBtn);
        backArrow = (ImageView) findViewById(R.id.imgBack);
        helper = new ConnectionHelper(context);
        isInternet = helper.isConnectingToInternet();

    }

    private void resetPassword(String new_pass, String confirm_new_pass) {

        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        customDialog.show();
        JSONObject object = new JSONObject();
        try {
            object.put("password", new_pass);
            object.put("password_confirmation", confirm_new_pass);
            object.put("id", SharedHelper.getKey(context, "reset_id"));
            Log.e("ChangePasswordAPI", "" + object);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.RESET_PASSWORD, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                customDialog.dismiss();
                Log.v("SignInResponse", response.toString());
                displayMessage(response.optString("message"));
                GoToBeginActivity();
                SharedHelper.putKey(context, "otp", "");
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                customDialog.dismiss();
                String json = null;
                String Message;
                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {
                    try {
                        JSONObject errorObj = new JSONObject(new String(response.data));
                        Log.e("ErrorChangePasswordAPI", "" + errorObj.toString());

                        if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                            try {
                                displayMessage(errorObj.optString("error"));
                            } catch (Exception e) {
                                displayMessage(getString(R.string.something_went_wrong));
                            }
                        } else if (response.statusCode == 401) {
                            GoToBeginActivity();
                        } else if (response.statusCode == 422) {
                            json = XuberServicesApplication.trimMessage(new String(response.data));
                            if (json != "" && json != null) {
                                displayMessage(json);
                            } else {
                                displayMessage(getString(R.string.please_try_again));
                            }
                        } else {
                            displayMessage(getString(R.string.please_try_again));
                        }

                    } catch (Exception e) {
                        displayMessage(getString(R.string.something_went_wrong));
                    }


                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "Bearer " + SharedHelper.getKey(context, "access_token"));
                return headers;
            }
        };

        XuberServicesApplication.getInstance().addToRequestQueue(jsonObjectRequest);

    }


    private void changePassword(String current_pass, String new_pass, String confirm_new_pass) {

        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        customDialog.show();
        JSONObject object = new JSONObject();
        try {
            object.put("password", new_pass);
            object.put("password_confirmation", confirm_new_pass);
            object.put("old_password", current_pass);
            Log.e("ChangePasswordAPI", "" + object);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.CHANGE_PASSWORD_API, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                customDialog.dismiss();
                Log.v("SignInResponse", response.toString());
                displayMessage(response.optString("message"));
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                customDialog.dismiss();
                String json = null;
                String Message;
                NetworkResponse response = error.networkResponse;
                Log.e("MyTest", "" + error);
                Log.e("MyTestError", "" + error.networkResponse);
                Log.e("MyTestError1", "" + response.statusCode);
                if (response != null && response.data != null) {
                    try {
                        JSONObject errorObj = new JSONObject(new String(response.data));
                        Log.e("ErrorChangePasswordAPI", "" + errorObj.toString());

                        if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                            try {
                                displayMessage(errorObj.optString("error"));
                            } catch (Exception e) {
                                displayMessage(getString(R.string.something_went_wrong));
                            }
                        } else if (response.statusCode == 401) {
                            GoToBeginActivity();
                        } else if (response.statusCode == 422) {
                            json = XuberServicesApplication.trimMessage(new String(response.data));
                            if (json != "" && json != null) {
                                displayMessage(json);
                            } else {
                                displayMessage(getString(R.string.please_try_again));
                            }
                        } else {
                            displayMessage(getString(R.string.please_try_again));
                        }

                    } catch (Exception e) {
                        displayMessage(getString(R.string.something_went_wrong));
                    }


                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "Bearer " + SharedHelper.getKey(context, "access_token"));
                return headers;
            }
        };

        XuberServicesApplication.getInstance().addToRequestQueue(jsonObjectRequest);

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void GoToBeginActivity() {
        SharedHelper.putKey(activity, "loggedIn", getString(R.string.False));
        Intent mainIntent = new Intent(activity, ActivityPassword.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        activity.finish();
    }

    public void displayMessage(String toastString) {
        Snackbar snackbar = Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(ContextCompat.getColor(this, R.color.black));
        TextView textView = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(ContextCompat.getColor(this, R.color.white));
        snackbar.show();
    }

}
