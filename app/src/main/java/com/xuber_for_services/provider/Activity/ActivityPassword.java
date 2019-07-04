package com.xuber_for_services.provider.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.splunk.mint.Mint;
import com.xuber_for_services.provider.Constant.URLHelper;
import com.xuber_for_services.provider.Helper.AppHelper;
import com.xuber_for_services.provider.Helper.ConnectionHelper;
import com.xuber_for_services.provider.Helper.CustomDialog;
import com.xuber_for_services.provider.Helper.SharedHelper;
import com.xuber_for_services.provider.KycActivity;
import com.xuber_for_services.provider.R;
import com.xuber_for_services.provider.Utils.Utilities;
import com.xuber_for_services.provider.XuberServicesApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.xuber_for_services.provider.XuberServicesApplication.trimMessage;

public class ActivityPassword extends AppCompatActivity {

    public Context context = ActivityPassword.this;
    public Activity activity = ActivityPassword.this;
    ConnectionHelper helper;
    Boolean isInternet;
    EditText password, email;

    TextView forgetPasswordTxt;

    Button signInBtn;
    LinearLayout signUpLayout;

    CustomDialog customDialog;
    String TAG = "ActivityPassword";
    String device_token, device_UDID;
    Utilities utils = new Utilities();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }

        Mint.setApplicationEnvironment(Mint.appEnvironmentStaging);
        Mint.initAndStartSession(this.getApplication(), "7e86d1ca");

        setContentView(R.layout.activity_password);
        findViewByIdandInit();
        GetToken();
        if (Build.VERSION.SDK_INT > 15) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }


        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (email.getText().toString().equals("") || email.getText().toString().equalsIgnoreCase(getString(R.string.sample_number))) {
                    displayMessage(getString(R.string.mobile_number_empty));
                } else if (password.getText().toString().equals("") || password.getText().toString().equalsIgnoreCase
                        (getString(R.string.password_txt))) {
                    displayMessage(getString(R.string.password_validation));
                } else if (password.getText().toString().length() < 6) {
                    displayMessage(getString(R.string.passwd_length));
                } else {
                    signIn();
                }
            }
        });

        forgetPasswordTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, ForgetPassword.class);
                startActivity(intent);
            }
        });

        signUpLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedHelper.putKey(getApplicationContext(), "from", "password");
                SharedHelper.putKey(context, "password", "");
                Intent mainIntent = new Intent(activity, RegisterActivity.class);
                startActivity(mainIntent);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });

       /* forgetPasswordTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedHelper.putKey(context,"password", "");
                Intent mainIntent = new Intent(activity, ForgetPassword.class);
                startActivity(mainIntent);
            }
        });*/


    }


    private void signIn() {
        if (isInternet) {
            customDialog = new CustomDialog(activity);
            customDialog.setCancelable(false);
            customDialog.show();
            JSONObject object = new JSONObject();
            try {

//                object.put("grant_type", "password");
//                object.put("CLIENT_ID", URLHelper.CLIENT_ID);
//                object.put("CLIENT_SECRET_KEY", URLHelper.CLIENT_SECRET_KEY);
                object.put("mobile", email.getText().toString());
                object.put("password", password.getText().toString());
                object.put("device_type", "android");
                object.put("device_id", device_UDID);
                object.put("device_token", device_token);
                Log.e("InputToLoginAPI", "" + object);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.LOGIN, object, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    customDialog.dismiss();
                    utils.print("SignUpResponse", response.toString());
                    Log.e("loginres",response.toString());
                    Log.e("signin",response.toString());
                    SharedHelper.putKey(context, "access_token", response.optString("access_token"));
                    SharedHelper.putKey(context, "refresh_token", response.optString("refresh_token"));
                    SharedHelper.putKey(context, "token_type", "Bearer");
                    SharedHelper.putKey(context, "id", response.optString("id"));
                    SharedHelper.putKey(context, "first_name", response.optString("first_name"));
                    SharedHelper.putKey(context, "last_name", response.optString("last_name"));
                    SharedHelper.putKey(context, "email", response.optString("email"));
                    SharedHelper.putKey(context, "picture", AppHelper.getImageUrl(response.optString("picture")));
                    SharedHelper.putKey(context, "gender", response.optString("gender"));
                    SharedHelper.putKey(context, "mobile", response.optString("mobile"));
                    SharedHelper.putKey(context, "description", response.optString("description"));
                    SharedHelper.putKey(context, "wallet_balance", response.optString("wallet_balance"));
                    SharedHelper.putKey(context, "payment_mode", response.optString("payment_mode"));
                    if (!response.optString("currency").equalsIgnoreCase("") || !response.optString("currency").equalsIgnoreCase("null")) {
                        SharedHelper.putKey(context, "currency", response.optString("currency"));
                    } else {
                        SharedHelper.putKey(context, "currency", "$");
                    }
                    SharedHelper.putKey(context, "loggedIn", getString(R.string.True));
                    getProfile();


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    customDialog.dismiss();
                    String json = null;
                    String Message;
                    NetworkResponse response = error.networkResponse;
                    utils.print("MyTest", "" + error);
                    utils.print("MyTestError", "" + error.networkResponse);

                    if (response != null && response.data != null) {
                        try {
                            JSONObject errorObj = new JSONObject(new String(response.data));

                            if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500 || response.statusCode == 401) {
                                try {
                                    displayMessage(errorObj.optString("error"));
                                } catch (Exception e) {
                                    displayMessage(getString(R.string.something_went_wrong));
                                }
                            } else if (response.statusCode == 422) {
                                json = trimMessage(new String(response.data));
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
                    return headers;
                }
            };

            XuberServicesApplication.getInstance().addToRequestQueue(jsonObjectRequest);

        } else {
            displayMessage(getString(R.string.something_went_wrong_net));
        }

    }

    public void getProfile() {

        if (isInternet) {

            customDialog = new CustomDialog(context);
            customDialog.setCancelable(false);
            customDialog.show();
            JSONObject object = new JSONObject();
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URLHelper.PROVIDER_PROFILE, object, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    customDialog.dismiss();
                   /// Log.e("loginres",response.toString());
                    utils.print("GetProfile", response.toString());
                    GoToMainActivity();

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

                            if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                                try {
                                    displayMessage(errorObj.optString("message"));
                                } catch (Exception e) {
                                    displayMessage(getString(R.string.something_went_wrong));
                                }
                            } else if (response.statusCode == 401) {
                                refreshAccessToken();
                            } else if (response.statusCode == 422) {

                                json = trimMessage(new String(response.data));
                                if (json != "" && json != null) {
                                    displayMessage(json);
                                } else {
                                    displayMessage(getString(R.string.please_try_again));
                                }

                            } else if (response.statusCode == 503) {
                                displayMessage(getString(R.string.server_down));
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
                    headers.put("Authorization", "Bearer" + " " + SharedHelper.getKey(context, "access_token"));
                    utils.print("Authoization", "Bearer" + " "
                            + SharedHelper.getKey(context, "access_token"));
                    return headers;
                }
            };

            XuberServicesApplication.getInstance().addToRequestQueue(jsonObjectRequest);
        } else {
            displayMessage(getString(R.string.something_went_wrong_net));
        }

    }


    private void refreshAccessToken() {
        if (isInternet) {
            customDialog = new CustomDialog(activity);
            customDialog.setCancelable(false);
            customDialog.show();
            JSONObject object = new JSONObject();
            try {

                object.put("grant_type", "refresh_token");
                object.put("client_id", URLHelper.CLIENT_ID);
                object.put("client_secret", URLHelper.CLIENT_SECRET_KEY);
                object.put("refresh_token", SharedHelper.getKey(context, "refresh_token"));
                object.put("scope", "");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.LOGIN, object, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    customDialog.dismiss();
                    Log.e("loginres",response.toString());
                    utils.print("SignUpResponse", response.toString());
                    SharedHelper.putKey(context, "access_token", response.optString("access_token"));
                    SharedHelper.putKey(context, "refresh_token", response.optString("refresh_token"));
                    SharedHelper.putKey(context, "token_type", response.optString("token_type"));
                    getProfile();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    customDialog.dismiss();
                    String json = null;
                    String Message;
                    NetworkResponse response = error.networkResponse;
                    utils.print("MyTest", "" + error);
                    utils.print("MyTestError", "" + error.networkResponse);
                    utils.print("MyTestError1", "" + response.statusCode);

                    if (response != null && response.data != null) {
                        SharedHelper.putKey(context, "loggedIn", getString(R.string.False));
                        GoToBeginActivity();
                    }
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("X-Requested-With", "XMLHttpRequest");
                    return headers;
                }
            };

            XuberServicesApplication.getInstance().addToRequestQueue(jsonObjectRequest);

        } else {
            displayMessage(getString(R.string.something_went_wrong_net));
        }

    }

    public void findViewByIdandInit() {
        password = (EditText) findViewById(R.id.txtpassword);
        helper = new ConnectionHelper(context);
        isInternet = helper.isConnectingToInternet();
        forgetPasswordTxt = (TextView) findViewById(R.id.lblforgotpassword);
        signInBtn = (Button) findViewById(R.id.btnSignIn);
        signUpLayout = (LinearLayout) findViewById(R.id.lnrRegister);
        email = (EditText) findViewById(R.id.txtemail);
    }

    public void GoToBeginActivity() {
        Intent mainIntent = new Intent(activity, ActivityPassword.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
        activity.finish();
    }

    public void displayMessage(String toastString) {
        try {
            if (getCurrentFocus() != null) {
                Snackbar snackbar = Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT);
                View snackBarView = snackbar.getView();
                snackBarView.setBackgroundColor(ContextCompat.getColor(this, R.color.black));
                TextView textView = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(ContextCompat.getColor(this, R.color.white));
                snackbar.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void GoToMainActivity() {
        Intent mainIntent = new Intent(activity, KycActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        activity.finish();
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

   /* @Override
    public void onBackPressed() {
        SharedHelper.putKey(context,"password", "");
        Intent mainIntent = new Intent(activity, ActivityEmail.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        activity.finish();
    }*/

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
    }

    public void GetToken() {
        try {
            if (!SharedHelper.getKey(context, "device_token").equals("") && SharedHelper.getKey(context, "device_token") != null) {
                device_token = SharedHelper.getKey(context, "device_token");
                utils.print(TAG, "GCM Registration Token: " + device_token);
            } else {
                device_token = "COULD NOT GET FCM TOKEN";
                utils.print(TAG, "Failed to complete token refresh: " + device_token);
            }
        } catch (Exception e) {
            device_token = "COULD NOT GET FCM TOKEN";
            utils.print(TAG, "Failed to complete token refresh");
        }

        try {
            device_UDID = android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            utils.print(TAG, "Device UDID:" + device_UDID);
        } catch (Exception e) {
            device_UDID = "COULD NOT GET UDID";
            e.printStackTrace();
            utils.print(TAG, "Failed to complete device UDID");
        }
    }
}
