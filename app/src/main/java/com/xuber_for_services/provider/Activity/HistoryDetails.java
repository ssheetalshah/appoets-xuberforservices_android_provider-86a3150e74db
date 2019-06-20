package com.xuber_for_services.provider.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.xuber_for_services.provider.Constant.URLHelper;
import com.xuber_for_services.provider.Helper.AppHelper;
import com.xuber_for_services.provider.Helper.ConnectionHelper;
import com.xuber_for_services.provider.Helper.CustomDialog;
import com.xuber_for_services.provider.Helper.SharedHelper;
import com.xuber_for_services.provider.R;
import com.xuber_for_services.provider.Utils.Utilities;
import com.xuber_for_services.provider.XuberServicesApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.xuber_for_services.provider.XuberServicesApplication.trimMessage;

public class HistoryDetails extends AppCompatActivity {
    Activity activity;
    Context context;
    Boolean isInternet;
    ConnectionHelper helper;
    CustomDialog customDialog;
    TextView tripAmount;
    TextView tripDate;
    TextView paymentType;
    TextView tripComments, lblComments;
    TextView tripProviderName;
    TextView tripSource;
    TextView tripDestination;
    TextView lblTitle;
    TextView priceType;
    ImageView tripImg, tripProviderImg, paymentTypeImg;
    RatingBar tripProviderRating;
    LinearLayout sourceAndDestinationLayout;
    public JSONObject jsonObject;
    ImageView backArrow;
    LinearLayout parentLayout, lnrComments;
    String tag = "", strUserId = "", strServiceType = "";
    TextView lblServiceType, lblServiceAddress;
    Button btnCancelRide;
    Utilities utils = new Utilities();

    String afterImg, beforeImg, afterComment, beforeComment;

    ImageView infoImg;
    CardView ServiceAddressCardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_details);
        findViewByIdAndInitialize();
        try {
            Intent intent = getIntent();
            String post_details = intent.getExtras().getString("post_value");
            tag = intent.getExtras().getString("tag");
            jsonObject = new JSONObject(post_details);
        } catch (Exception e) {
            jsonObject = null;
            e.printStackTrace();
        }

        if (jsonObject != null) {

            if (tag.equalsIgnoreCase("past_trips")) {
                btnCancelRide.setVisibility(View.GONE);
                lnrComments.setVisibility(View.VISIBLE);
                getRequestDetails();
                priceType.setText(getString(R.string.total_amount));
                lblTitle.setText("Past Services");
            } else {
                btnCancelRide.setVisibility(View.VISIBLE);
                lnrComments.setVisibility(View.GONE);
                priceType.setText(getString(R.string.hourly_fare));
                getUpcomingDetails();
                lblTitle.setText("Upcoming Services");
                infoImg.setVisibility(View.GONE);
                tripProviderRating.setVisibility(View.GONE);
            }
        }
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        tripProviderImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent showProfileIntent = new Intent(context, ShowProfile.class);
                showProfileIntent.putExtra("user_id", "" + strUserId);
                showProfileIntent.putExtra("service_type", "" + strServiceType);
                startActivity(showProfileIntent);
            }
        });
    }

    public void findViewByIdAndInitialize() {
        activity = HistoryDetails.this;
        context = HistoryDetails.this;
        helper = new ConnectionHelper(activity);
        isInternet = helper.isConnectingToInternet();
        parentLayout = (LinearLayout) findViewById(R.id.parentLayout);
        parentLayout.setVisibility(View.GONE);
        tripAmount = (TextView) findViewById(R.id.tripAmount);
        tripDate = (TextView) findViewById(R.id.tripDate);
        paymentType = (TextView) findViewById(R.id.paymentType);
        lblServiceType = (TextView) findViewById(R.id.lblServiceType);
        lblServiceAddress = (TextView) findViewById(R.id.lblServiceAddress);
        paymentTypeImg = (ImageView) findViewById(R.id.paymentTypeImg);
        tripProviderImg = (ImageView) findViewById(R.id.tripProviderImg);
        tripImg = (ImageView) findViewById(R.id.tripImg);
        tripComments = (TextView) findViewById(R.id.tripComments);
        lblComments = (TextView) findViewById(R.id.lblComments);
        tripProviderName = (TextView) findViewById(R.id.tripProviderName);
        tripProviderRating = (RatingBar) findViewById(R.id.tripProviderRating);
        tripSource = (TextView) findViewById(R.id.tripSource);
        tripDestination = (TextView) findViewById(R.id.tripDestination);
        lblTitle = (TextView) findViewById(R.id.lblTitle);
        sourceAndDestinationLayout = (LinearLayout) findViewById(R.id.sourceAndDestinationLayout);
        btnCancelRide = (Button) findViewById(R.id.btnCancelRide);
        lnrComments = (LinearLayout) findViewById(R.id.lnrComments);
        backArrow = (ImageView) findViewById(R.id.backArrow);
        priceType = (TextView) findViewById(R.id.price_type);
        infoImg = (ImageView) findViewById(R.id.info_img);
        ServiceAddressCardView = (CardView) findViewById(R.id.ServiceAddressCardView);


        LayerDrawable drawable = (LayerDrawable) tripProviderRating.getProgressDrawable();
        drawable.getDrawable(0).setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);
        drawable.getDrawable(1).setColorFilter(Color.parseColor("#FFAB00"), PorterDuff.Mode.SRC_ATOP);
        drawable.getDrawable(2).setColorFilter(Color.parseColor("#FFAB00"), PorterDuff.Mode.SRC_ATOP);


        btnCancelRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(getString(R.string.cencel_request))
                        .setCancelable(false)
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                cancelRequest();
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        infoImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HistoryDetails.this, HistoryService.class);
                intent.putExtra("after_comment", afterComment);
                intent.putExtra("before_comment", beforeComment);
                intent.putExtra("after_image", afterImg);
                intent.putExtra("before_image", beforeImg);
                startActivity(intent);
            }
        });
    }

    public void getRequestDetails() {

        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        customDialog.show();

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URLHelper.GET_HISTORY_DETAILS_API + "?request_id=" + jsonObject.optString("id"), new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(final JSONArray response) {

                if (response != null && response.length() > 0) {
                    Glide.with(activity).load(response.optJSONObject(0).optString("static_map")).placeholder(R.drawable.placeholder).error(R.drawable.placeholder).into(tripImg);
                    if (!response.optJSONObject(0).optString("payment").equalsIgnoreCase("null")) {
                        Log.d("", "onResponse: " + response.optJSONObject(0).optJSONObject("payment").optString("total"));
                        tripAmount.setText(SharedHelper.getKey(context, "currency") + "" + response.optJSONObject(0).optJSONObject("payment").optString("total"));
                    }
                    String form = response.optJSONObject(0).optString("assigned_at");
                    try {
                        tripDate.setText(getDate(form) + " " + getMonth(form) + " " + getYear(form) + " at " + getTime(form));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    afterComment = response.optJSONObject(0).optString("after_comment");
                    afterImg = response.optJSONObject(0).optString("after_image");
                    beforeComment = response.optJSONObject(0).optString("before_comment");
                    beforeImg = response.optJSONObject(0).optString("before_image");

                    strUserId = response.optJSONObject(0).optString("user_id");

                    try {
                        if (!response.optJSONObject(0).optString("s_address").equalsIgnoreCase("")) {
                            lblServiceAddress.setText(response.getJSONObject(0).optString("s_address"));
                        } else {
                            Double lat = null;
                            Double lng = null;
                            if (!response.optJSONObject(0).optString("s_latitude").equalsIgnoreCase("")) {
                                lat = Double.parseDouble(response.getJSONObject(0).optString("s_latitude"));
                            }
                            if (!response.optJSONObject(0).optString("s_longitude").equalsIgnoreCase("")) {
                                lng = Double.parseDouble(response.getJSONObject(0).optString("s_longitude"));
                            }
                            if (lat != null && lng != null) {
                                lblServiceAddress.setText(getAddress(lat, lng));
                            } else {
                                lblServiceAddress.setText(response.getJSONObject(0).optString("s_latitude") + ", " + response.getJSONObject(0).optString("s_longitude"));
                            }
                        }
                        //Go to Google Map and show loacation
                        ServiceAddressCardView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Uri naviUri2 = Uri.parse("http://maps.google.com/maps?"+ "q=loc:" + response.optJSONObject(0).optString("s_latitude") + "," + response.optJSONObject(0).optString("s_longitude"));
                                Intent intentMap = new Intent(Intent.ACTION_VIEW, naviUri2);
                                intentMap.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                                startActivity(intentMap);
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    paymentType.setText(response.optJSONObject(0).optString("payment_mode"));
                    if (response.optJSONObject(0).optString("payment_mode").equalsIgnoreCase("CASH")) {
                        paymentTypeImg.setImageResource(R.drawable.payment_icon);
                    } else {
                        paymentTypeImg.setImageResource(R.drawable.visa);
                    }

                    Glide.with(activity).load(AppHelper.getImageUrl(response.optJSONObject(0).optJSONObject("user")
                            .optString("picture"))).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).dontAnimate().into(tripProviderImg);
                    if (!response.optJSONObject(0).optString("rating").equalsIgnoreCase("null")) {
                        if (!response.optJSONObject(0).optJSONObject("rating").optString("provider_comment").equalsIgnoreCase("")) {
                            tripComments.setText(response.optJSONObject(0).optJSONObject("rating").optString("provider_comment"));
                        } else {
                            tripComments.setText(getResources().getString(R.string.no_comments));
                        }
                        tripProviderRating.setRating(Float.parseFloat(response.optJSONObject(0).optJSONObject("rating").optString("user_rating")));
                    } else {
                    }
                    tripProviderName.setText(response.optJSONObject(0).optJSONObject("user").optString("first_name") + " " + response.optJSONObject(0).optJSONObject("user").optString("last_name"));
                    if (response.optJSONObject(0).optString("s_address") == null || response.optJSONObject(0).optString("d_address") == null || response.optJSONObject(0).optString("d_address").equals("") || response.optJSONObject(0).optString("s_address").equals("")) {
                        sourceAndDestinationLayout.setVisibility(View.GONE);
                    } else {
                        tripSource.setText(response.optJSONObject(0).optString("s_address"));
                        tripDestination.setText(response.optJSONObject(0).optString("d_address"));
                    }
                    parentLayout.setVisibility(View.VISIBLE);

                    try {
                        JSONObject serviceObj = response.optJSONObject(0).optJSONObject("service_type");
                        if (serviceObj != null) {
                            lblServiceType.setText(serviceObj.optString("name"));
                            strServiceType = serviceObj.optString("name");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                customDialog.dismiss();

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
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "Bearer " + SharedHelper.getKey(context, "access_token"));
                utils.print("Token", "" + SharedHelper.getKey(context, "access_token"));
                return headers;
            }
        };

        XuberServicesApplication.getInstance().addToRequestQueue(jsonArrayRequest);
    }


    public void cancelRequest() {

        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        customDialog.show();
        JSONObject object = new JSONObject();
        try {
            object.put("id", jsonObject.optString("id"));
            utils.print("", "request_id" + jsonObject.optString("id"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.CANCEL_REQUEST_API, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                utils.print("CancelRequestResponse", response.toString());
                customDialog.dismiss();
                finish();
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
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "Bearer " + SharedHelper.getKey(context, "access_token"));
                utils.print("", "Access_Token" + SharedHelper.getKey(context, "access_token"));
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

    public void GoToBeginActivity() {
        SharedHelper.putKey(activity, "loggedIn", getString(R.string.False));
        Intent mainIntent = new Intent(activity, ActivityPassword.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        activity.finish();
    }

    public void getUpcomingDetails() {

        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        customDialog.show();

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URLHelper.UPCOMING_TRIP_DETAILS + "?request_id=" + jsonObject.optString("id"), new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(final JSONArray response) {

                utils.print("Get Upcoming Details", response.toString());
                if (response != null && response.length() > 0) {
                    Glide.with(activity).load(response.optJSONObject(0).optString("static_map")).placeholder(R.drawable.placeholder).error(R.drawable.placeholder).into(tripImg);
                    paymentType.setText(response.optJSONObject(0).optString("payment_mode"));
                    String form = response.optJSONObject(0).optString("schedule_at");
                    try {
                        tripDate.setText(getDate(form) + "th " + getMonth(form) + " " + getYear(form) + "" + getTime(form));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if (response.optJSONObject(0).optString("payment_mode").equalsIgnoreCase("CASH")) {
                        paymentTypeImg.setImageResource(R.drawable.payment_icon);
                    } else {
                        paymentTypeImg.setImageResource(R.drawable.visa);
                    }

                    tripProviderName.setText(response.optJSONObject(0).optJSONObject("user").optString("first_name") + " " + response.optJSONObject(0).optJSONObject("user").optString("last_name"));
                    Glide.with(activity).load(AppHelper.getImageUrl(response.optJSONObject(0).optJSONObject("user")
                            .optString("picture"))).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).dontAnimate().into(tripProviderImg);
                    if (response.optJSONObject(0).optString("s_address") == null || response.optJSONObject(0).optString("d_address") == null || response.optJSONObject(0).optString("d_address").equals("") || response.optJSONObject(0).optString("s_address").equals("")) {
                        sourceAndDestinationLayout.setVisibility(View.GONE);
                    } else {
                        tripSource.setText(response.optJSONObject(0).optString("s_address"));
                        tripDestination.setText(response.optJSONObject(0).optString("d_address"));
                    }

                    strUserId = response.optJSONObject(0).optString("user_id");

                    try {
                        JSONObject serviceObj = response.optJSONObject(0).optJSONObject("service_type");
                        if (serviceObj != null) {
//                            holder.car_name.setText(serviceObj.optString("name"));
                            tripAmount.setText(SharedHelper.getKey(context, "currency") + serviceObj.optString("price"));

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        if (!response.optJSONObject(0).optString("s_address").equalsIgnoreCase("")) {
                            lblServiceAddress.setText(response.getJSONObject(0).optString("s_address"));
                        } else {
                            Double lat = null;
                            Double lng = null;
                            if (!response.optJSONObject(0).optString("s_latitude").equalsIgnoreCase("")) {
                                lat = Double.parseDouble(response.getJSONObject(0).optString("s_latitude"));
                            }
                            if (!response.optJSONObject(0).optString("s_longitude").equalsIgnoreCase("")) {
                                lng = Double.parseDouble(response.getJSONObject(0).optString("s_longitude"));
                            }
                            if (lat != null && lng != null) {
                                lblServiceAddress.setText(getAddress(lat, lng));
                            } else {
                                lblServiceAddress.setText(response.getJSONObject(0).optString("s_latitude") + ", " + response.getJSONObject(0).optString("s_longitude"));
                            }
                        }

                        //Go to Google Map and show loacation
                        ServiceAddressCardView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Uri naviUri2 = Uri.parse("http://maps.google.com/maps?"+ "saddr=&daddr=" + response.optJSONObject(0).optString("s_latitude") + "," + response.optJSONObject(0).optString("s_longitude"));
                                Intent intentMap = new Intent(Intent.ACTION_VIEW, naviUri2);
                                intentMap.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                                startActivity(intentMap);
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        JSONObject serviceObj = response.optJSONObject(0).optJSONObject("service_type");
                        if (serviceObj != null) {
                            lblServiceType.setText(serviceObj.optString("name"));
                            strServiceType = serviceObj.optString("name");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                customDialog.dismiss();
                parentLayout.setVisibility(View.VISIBLE);

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
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "Bearer " + SharedHelper.getKey(context, "access_token"));
                return headers;
            }
        };

        XuberServicesApplication.getInstance().addToRequestQueue(jsonArrayRequest);
    }


    private String getMonth(String date) throws ParseException {
        Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        String monthName = new SimpleDateFormat("MMM").format(cal.getTime());
        return monthName;
    }

    private String getDate(String date) throws ParseException {
        Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        String dateName = new SimpleDateFormat("dd").format(cal.getTime());
        return dateName;
    }

    private String getYear(String date) throws ParseException {
        Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        String yearName = new SimpleDateFormat("yyyy").format(cal.getTime());
        return yearName;
    }

    private String getTime(String date) throws ParseException {
        Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        String timeName = new SimpleDateFormat("hh:mm a").format(cal.getTime());
        String str = timeName.replace("a.m", "AM").replace("p.m", "PM");
        return str;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public String getAddress(double latitude, double longitude) {
        StringBuilder strReturnedAddress = null;
        try {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && addresses.size() > 0) {
                Address returnedAddress = addresses.get(0);
                strReturnedAddress = new StringBuilder();
                for (int j = 0; j < returnedAddress.getMaxAddressLineIndex(); j++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(j)).append("");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Can't able to get the address!.Please try again", Toast.LENGTH_SHORT).show();
        }

        String strAddress = "";
        if (strReturnedAddress != null) {
            strAddress = strReturnedAddress.toString();
        }
        return strAddress;
    }
}
