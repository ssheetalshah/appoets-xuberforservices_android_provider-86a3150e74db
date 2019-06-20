package com.xuber_for_services.provider.Fragments;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.xuber_for_services.provider.Activity.ActivityPassword;
import com.xuber_for_services.provider.Constant.URLHelper;
import com.xuber_for_services.provider.Helper.CustomDialog;
import com.xuber_for_services.provider.Helper.SharedHelper;
import com.xuber_for_services.provider.R;
import com.xuber_for_services.provider.XuberServicesApplication;

import org.json.JSONObject;

import java.util.HashMap;

import static com.xuber_for_services.provider.XuberServicesApplication.trimMessage;

public class Help extends Fragment implements View.OnClickListener {

    public static final String TAG = "Help";

    ImageView backImg;
    ImageView phoneImg;
    ImageView webImg;
    ImageView mailImg;

    TextView helpTxt, titleTxt;

    String phone, email, help, title;

    public Help() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_help, container, false);
        findViewByIdAndInitialize(view);
        setClickListeners();
        getHelp();
        return view;
    }

    private void setClickListeners() {
        backImg.setOnClickListener(this);
        mailImg.setOnClickListener(this);
        webImg.setOnClickListener(this);
        phoneImg.setOnClickListener(this);
    }

    public void findViewByIdAndInitialize(View view) {
        backImg = (ImageView) view.findViewById(R.id.backArrow);
        phoneImg = (ImageView) view.findViewById(R.id.img_phone);
        webImg = (ImageView) view.findViewById(R.id.img_web);
        mailImg = (ImageView) view.findViewById(R.id.img_mail);
        helpTxt = (TextView) view.findViewById(R.id.help_txt);
        titleTxt = (TextView) view.findViewById(R.id.title_txt);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backArrow:
                getFragmentManager().popBackStack();
                break;
            case R.id.img_mail:
                String to = email;
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/html");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{to});
                intent.putExtra(Intent.EXTRA_TEXT, "Hello team");
                startActivity(Intent.createChooser(intent, "Send Email"));
                break;
            case R.id.img_phone:
                showCallDialog();

                break;
            case R.id.img_web:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(URLHelper.HELP_REDIRECT_URL));
                startActivity(browserIntent);
                break;
        }

    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    //Toast.makeText(SignInActivity.this, "PERMISSION_GRANTED", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + phone));
                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    startActivity(intent);
                } else {
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1);
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void showCallDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(getString(R.string.call_confirm));

        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1);
                } else {
                    Intent intentCall = new Intent(Intent.ACTION_CALL);
                    intentCall.setData(Uri.parse("tel:" + phone));
                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    startActivity(intentCall);
                }
            }
        });

        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Reset to previous seletion menu in navigation
                dialog.dismiss();
            }
        });
        builder.setCancelable(false);
        final AlertDialog dialog = builder.create();
        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
            }
        });
        dialog.show();
    }

    public void getHelp() {
        final CustomDialog customDialog = new CustomDialog(getActivity());
        customDialog.setCancelable(false);
        customDialog.show();
        JSONObject object = new JSONObject();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URLHelper.GET_HELP_DETAILS, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                customDialog.dismiss();
                phone = response.optString("contact_number");
                email = response.optString("contact_email");
                help = response.optString("contact_text");
                title = response.optString("contact_title");
                Log.d(TAG, "onResponse: " + title);
                if (help != null && !help.equalsIgnoreCase("null") && help.length() > 0)
                    helpTxt.setText(help);
                if (title != null && !title.equalsIgnoreCase("null") && title.length() > 0)
                    titleTxt.setText(title + " " + getString(R.string.help));
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
                                e.printStackTrace();
                            }
                        } else if (response.statusCode == 401) {
                            GoToBeginActivity();
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
                        e.printStackTrace();
                    }

                } else {
                    displayMessage(getString(R.string.please_try_again));
                }
            }
        }) {
            @Override
            public java.util.Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "Bearer " + SharedHelper.getKey(getContext(), "access_token"));
                Log.e("", "Access_Token" + SharedHelper.getKey(getContext(), "access_token"));
                return headers;
            }
        };
        XuberServicesApplication.getInstance().addToRequestQueue(jsonObjectRequest);
    }


    public void displayMessage(String toastString) {
        Snackbar snackbar = Snackbar.make(getView(), toastString, Snackbar.LENGTH_SHORT);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.black));
        TextView textView = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        snackbar.show();
    }

    public void GoToBeginActivity() {
        Toast.makeText(getContext(),getString(R.string.session_timeout),Toast.LENGTH_SHORT).show();
        SharedHelper.putKey(getContext(), "current_status", "");
        SharedHelper.putKey(getContext(), "loggedIn", getString(R.string.False));
        Intent mainIntent = new Intent(getContext(), ActivityPassword.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        getActivity().finish();
    }

}
