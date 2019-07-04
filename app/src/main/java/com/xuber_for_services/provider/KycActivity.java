package com.xuber_for_services.provider;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.StrictMode;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.xuber_for_services.provider.Activity.ActivityPassword;
import com.xuber_for_services.provider.Activity.ForgetPassword;
import com.xuber_for_services.provider.Activity.Home;
import com.xuber_for_services.provider.Activity.OTPActivity;
import com.xuber_for_services.provider.Constant.URLHelper;
import com.xuber_for_services.provider.Helper.CustomDialog;
import com.xuber_for_services.provider.Helper.SharedHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class KycActivity extends AppCompatActivity {
    EditText adhar, pan, bank, account, ifsc, branch;
    Button update;
    CustomDialog customDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }
        setContentView(R.layout.activity_kyc);


        if (Build.VERSION.SDK_INT > 15) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        adhar = (EditText) findViewById(R.id.adhar);
        pan = (EditText) findViewById(R.id.pan);
        bank = (EditText) findViewById(R.id.bank);
        account = (EditText) findViewById(R.id.account);
        ifsc = (EditText) findViewById(R.id.ifsc);
        branch = (EditText) findViewById(R.id.branch);

        update = (Button) findViewById(R.id.update);

        pan.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        ifsc.setFilters(new InputFilter[]{new InputFilter.AllCaps()});

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!adhar.getText().toString().equals("")) {
                    if (!pan.getText().toString().equals("")) {
                        if (!bank.getText().toString().equals("")) {
                            if (!account.getText().toString().equals("")) {
                                if (!ifsc.getText().toString().equals("")) {
                                    if (!branch.getText().toString().equals("")) {
                                        KYCRegister();
                                    } else {
                                        branch.setError("Please Enter Branch Name");
                                        branch.requestFocus();
                                    }
                                } else {
                                    ifsc.setError("Please Enter IFSC Code");
                                    ifsc.requestFocus();
                                }
                            } else {
                                account.setError("Please Enter Account");
                                account.requestFocus();
                            }
                        } else {
                            bank.setError("Please Enter Bank Name");
                            bank.requestFocus();
                        }
                    } else {
                        pan.setError("Please Enter Pan No");
                        pan.requestFocus();
                    }
                } else {
                    adhar.setError("Please Enter Aadhar");
                    adhar.requestFocus();
                }

               /* if (adhar.getText().toString().equals("") || adhar.getText().toString().equalsIgnoreCase(getString(R.string.sample_number))) {
                    displayMessage(getString(R.string.mobile_number_empty));
                } else {

                }*/
            }
        });
    }

    //--------------------------------------------------------

    private void KYCRegister() {
        customDialog = new CustomDialog(KycActivity.this);
        customDialog.setCancelable(false);
        customDialog.show();
        JSONObject object = new JSONObject();
        try {

            //    SharedHelper.putKey(KycActivity.this, "email", email.getText().toString());
            object.put("adhar_no", adhar.getText().toString());
            object.put("pan_card", pan.getText().toString());
            object.put("bank_ac_no", account.getText().toString());
            object.put("ifsc_code", ifsc.getText().toString());
            object.put("branch", branch.getText().toString());
            object.put("bank_name", bank.getText().toString());
            Log.e("InputToLoginAPI", "" + object);
            Log.e("InputToLoginAPI", "" + object);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.KYC_REGISTER, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                customDialog.dismiss();
                Log.e("responce", response.toString());
                //displayMessage("" + response.optString("message"));

                //JSONObject userObj = response.optJSONObject("provider");
                JSONObject id = response.optJSONObject("id");

                // SharedHelper.putKey(KycActivity.this, "reset_id", "" + userObj.optInt("id"));

                //  SharedHelper.putKey(KycActivity.this, "otp", "" + userObj.optInt("otp"));

                Intent resetIntent = new Intent(KycActivity.this, Home.class);
                startActivity(resetIntent);
                finish();

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
//                Log.e("MyTestError1", "" + response.statusCode);
                if (response != null && response.data != null) {
                    try {
                        JSONObject errorObj = new JSONObject(new String(response.data));

                        if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                            try {
                                displayMessage(errorObj.optString("message"));
                            } catch (Exception e) {
                                displayMessage("Something went wrong.");
                            }
                        } else if (response.statusCode == 401) {
                            try {
                                if (errorObj.optString("message").equalsIgnoreCase("invalid_token")) {
                                    //Call Refresh token
                                } else {
                                    displayMessage(errorObj.optString("message"));
                                }
                            } catch (Exception e) {
                                displayMessage("Something went wrong.");
                            }

                        } else if (response.statusCode == 422) {

                            json = XuberServicesApplication.trimMessage(new String(response.data));
                            if (json != "" && json != null) {
                                displayMessage(json);
                            } else {
                                displayMessage("Please try again.");
                            }

                        } else {
                            displayMessage("Please try again.");
                        }

                    } catch (Exception e) {
                        displayMessage("Something went wrong.");
                    }
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "Bearer" + " " + SharedHelper.getKey(KycActivity.this, "access_token"));
                Log.e("Authoization", "Bearer" + " "
                        + SharedHelper.getKey(KycActivity.this, "access_token"));
                return headers;
            }
        };

        XuberServicesApplication.getInstance().addToRequestQueue(jsonObjectRequest);

    }

    public void displayMessage(String toastString) {
        Snackbar snackbar = Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(ContextCompat.getColor(this, R.color.black));
        TextView textView = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(ContextCompat.getColor(this, R.color.white));
        snackbar.show();
    }


    //---------------------------------------------------------

  /*  public class PostFeedback extends AsyncTask<String, Void, String> {
        ProgressDialog dialog;

        protected void onPreExecute() {
            dialog = new ProgressDialog(
                    KycActivity.this);
            dialog.show();

        }

        protected String doInBackground(String... arg0) {

            try {

                URL url = new URL("http://heightsmegamart.com/CPEPSI/api/Add_feedback");

                JSONObject postDataParams = new JSONObject();
               *//* postDataParams.put("customer_id", AppPreference.getId(FeedbackForm.this));
                postDataParams.put("name", FeedName);
                postDataParams.put("email", FeedEmail);
                postDataParams.put("contactno", FeedContact);
                postDataParams.put("address", FeedAddress);
                postDataParams.put("provider_id", Provider_id);
                postDataParams.put("pr_id", Prov_id);
                postDataParams.put("service", Rating_point);*//*

                Log.e("postDataParams", postDataParams.toString());

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000 *//* milliseconds*//*);
                conn.setConnectTimeout(15000  *//*milliseconds*//*);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));

                writer.flush();
                writer.close();
                os.close();

                int responseCode = conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {

                    BufferedReader in = new BufferedReader(new
                            InputStreamReader(
                            conn.getInputStream()));

                    StringBuffer sb = new StringBuffer("");
                    String line = "";

                    while ((line = in.readLine()) != null) {

                        StringBuffer Ss = sb.append(line);
                        Log.e("Ss", Ss.toString());
                        sb.append(line);
                        break;
                    }

                    in.close();
                    return sb.toString();

                } else {
                    return new String("false : " + responseCode);
                }
            } catch (Exception e) {
                return new String("Exception: " + e.getMessage());
            }

        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                dialog.dismiss();

                JSONObject jsonObject = null;
                String s = result.toString();
                try {
                    jsonObject = new JSONObject(result);
                    String data = jsonObject.getString("data");
                    String responce = jsonObject.getString("responce");

                    if (responce.equals("true")) {

                    } else {
                        Toast.makeText(KycActivity.this, "Some Problem", Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        public String getPostDataString(JSONObject params) throws Exception {

            StringBuilder result = new StringBuilder();
            boolean first = true;

            Iterator<String> itr = params.keys();

            while (itr.hasNext()) {

                String key = itr.next();
                Object value = params.get(key);

                if (first)
                    first = false;
                else
                    result.append("&");

                result.append(URLEncoder.encode(key, "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(value.toString(), "UTF-8"));

            }
            return result.toString();
        }
    }*/
}
