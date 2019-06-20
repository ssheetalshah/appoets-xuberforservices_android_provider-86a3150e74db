package com.xuber_for_services.provider.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.xuber_for_services.provider.Adapter.ServiceListAdapter;
import com.xuber_for_services.provider.Constant.URLHelper;
import com.xuber_for_services.provider.Helper.CustomDialog;
import com.xuber_for_services.provider.Helper.SharedHelper;
import com.xuber_for_services.provider.Models.ServiceListModel;
import com.xuber_for_services.provider.R;
import com.xuber_for_services.provider.Utils.KeyHelper;
import com.xuber_for_services.provider.View.TBarView;
import com.xuber_for_services.provider.XuberServicesApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.xuber_for_services.provider.XuberServicesApplication.trimMessage;

public class SettingsStartActivity extends Activity implements ServiceListAdapter.ServiceClickListener {

    public static final String TAG = "SettingsFgmt";
    ServiceListAdapter serviceListAdapter;
    private RecyclerView recyclerView;
    Activity mActivity;
    Context context;
    Button btnSave;
    SeekBar seekBar;
    TextView lblCount;
    int availableService = 0;
    CustomDialog loadingDialog;
    ArrayList<String> lstSettings = new ArrayList<>();
    ArrayList<ServiceListModel> serviceListModels;
    ImageView userImg;
    TextView lblName;
    ArrayList<Services> selectedServices = new ArrayList<>();
    boolean[] selectedService;

    @Override
    public void onServiceClick(boolean[] selectedService, ServiceListModel serviceListModel) {
        this.selectedService = selectedService;
        Services services = new Services();
        services.setId(serviceListModel.getId());
        services.setPrice(serviceListModel.getPrice());
        selectedServices.add(services);
        seekBar.setProgress(++availableService);
        lblCount.setText(availableService + "/" + serviceListModels.size() + "");
        btnSave.setBackgroundColor(ContextCompat.getColor(context, R.color.btn_color));
    }

    @Override
    public void onServiceUnSelect(boolean[] selectedService, ServiceListModel serviceListModel) {
        this.selectedService = selectedService;
        Services services = new Services();
        services.setId(serviceListModel.getId());
        services.setPrice(serviceListModel.getPrice());
        selectedServices.remove(services);
        seekBar.setProgress(--availableService);
        lblCount.setText(availableService + "/" + serviceListModels.size() + "");
        if (selectedServices.size() == 0) {
            btnSave.setBackgroundColor(ContextCompat.getColor(context, R.color.offline));
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }
        setContentView(R.layout.settings_start);
        context = this;
        findViewById();

    }


    public void findViewById() {


        btnSave = (Button) findViewById(R.id.btnSave);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        lblCount = (TextView) findViewById(R.id.lblCount);
        userImg= (ImageView) findViewById(R.id.profile_image);
        lblName= (TextView) findViewById(R.id.lblName);
        setProviderDetails();
        selectedServices = new ArrayList<>();

        recyclerView = (RecyclerView) findViewById(R.id.selectServiceRecyclerview);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendServiceUpdateRequest();
            }
        });

        loadingDialog = new CustomDialog(context);
        loadingDialog.setCancelable(false);
        setupRecyclerView();
        getServices();
    }

    public void setProviderDetails() {
        String name = SharedHelper.getKey(context, KeyHelper.KEY_FIRST_NAME);
        if (name != null &&!name.equalsIgnoreCase("null")&& name.length() > 0)
            lblName.setText(name);
        Picasso.with(context).load(SharedHelper.getKey(context,"picture"))
                .fit().centerCrop().memoryPolicy(MemoryPolicy.NO_CACHE).
                placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(userImg);
    }

    private void sendServiceUpdateRequest() {
        loadingDialog.show();
        JSONObject object = new JSONObject();
        List<ServiceListModel> service = serviceListAdapter.getServiceListModel();
        selectedServices = new ArrayList<>();
        for (ServiceListModel services : service) {
            if (services.getAvailable().equalsIgnoreCase("true")) {
                Services select = new Services();
                select.setId(services.getId());
                select.setPrice(services.getPricePerHour());
                selectedServices.add(select);
            }
        }

        ArrayList<String> serviceArraylist = new ArrayList<>();
        for (int i = 0; i < selectedServices.size(); i++) {
            HashMap<String, String> services = new HashMap<String, String>();
            serviceArraylist.add(selectedServices.get(i).getId());
        }
        Log.e(TAG, "services_arraylist: " + serviceArraylist);
        try {
            JSONArray jsonarray = new JSONArray(serviceArraylist);
            object.put("services", jsonarray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e(TAG, "sendServiceUpdateRequest: Req body" + object.toString());
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URLHelper.UPDATE_SERVICE, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                loadingDialog.dismiss();
                Toast.makeText(context, getResources().getString(R.string.services_updated), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onResponse: ");
                    SharedHelper.putKey(context, "settings","yes");
                    Intent mainIntent = new Intent(context, Home.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(mainIntent);
                    finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingDialog.dismiss();
                errorResponse(error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "" + "Bearer" + " " + SharedHelper.getKey(context,"access_token"));
                return headers;
            }
        };
        XuberServicesApplication.getInstance().addToRequestQueue(request);
    }


    private void setupToolbar(View view) {
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        TBarView tBarView = new TBarView(SettingsStartActivity.this, toolbar);
        tBarView.setupToolbar(R.drawable.ic_nav_menu, getString(R.string.menu_settings), false, false);
    }

    private void setupRecyclerView() {
        serviceListModels = new ArrayList<>();
        serviceListAdapter = new ServiceListAdapter(serviceListModels, context);
        serviceListAdapter.setServiceClickListener(this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 3);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(context, R.dimen._5sdp);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(serviceListAdapter);
    }

    private class ItemOffsetDecoration extends RecyclerView.ItemDecoration {

        private int mItemOffset;

        ItemOffsetDecoration(int itemOffset) {
            mItemOffset = itemOffset;
        }

        ItemOffsetDecoration(@NonNull Context context, @DimenRes int itemOffsetId) {
            this(context.getResources().getDimensionPixelSize(itemOffsetId));
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.set(mItemOffset, mItemOffset, mItemOffset, mItemOffset);
        }
    }

    private void getServices() {
        loadingDialog.show();
        final JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET,
                URLHelper.GET_SERVICES, new JSONArray(), new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (response != null && response.length() > 0)
                    loadRecyclerView(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingDialog.dismiss();
                errorResponse(error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "" + "Bearer" + " " + SharedHelper.getKey(context, KeyHelper.KEY_ACCESS_TOKEN));
                return headers;
            }
        };
        XuberServicesApplication.getInstance().addToRequestQueue(jsonArrayRequest);
    }

    private void loadRecyclerView(JSONArray jsonArray) {
        int cnt = jsonArray.length();
        try {
            seekBar.setMax(cnt);
            availableService = 0;
            for (int i = 0; i < cnt; i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                ServiceListModel serviceListModel = new ServiceListModel();
                serviceListModel.setId(jsonObject.optString("id"));
                serviceListModel.setName(jsonObject.optString("name"));
                serviceListModel.setImage(jsonObject.optString("image"));
                serviceListModel.setDescription(jsonObject.optString("description"));
                serviceListModel.setAvailable(jsonObject.optString("available"));
                serviceListModel.setPricePerHour(jsonObject.optString("price_per_hour"));
                if (serviceListModel.getAvailable().equalsIgnoreCase("true"))
                    availableService++;
                serviceListModels.add(serviceListModel);
            }
            loadingDialog.dismiss();
            recyclerView.getAdapter().notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        seekBar.setProgress(availableService);
        lblCount.setText(availableService + "/" + cnt + "");
    }

    private void errorResponse(VolleyError error) {
        String json;
        NetworkResponse response = error.networkResponse;
        if (response != null && response.data != null) {
            Log.e("MyTest", "" + error);
            Log.e("MyTestError", "" + error.networkResponse);
            Log.e("MyTestError1", "" + response.statusCode);
            try {
                JSONObject errorObj = new JSONObject(new String(response.data));
                Log.d(TAG, "onErrorResponse: " + errorObj);

                if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500 || response.statusCode == 401) {
                    try {
                        if (errorObj.optString("error") != null && errorObj.optString("error").length() > 0)
                            displayMessage(errorObj.optString("error"));
                        else
                            displayMessage(getString(R.string.something_went_wrong));
                    } catch (Exception e) {
                        displayMessage(getString(R.string.something_went_wrong));
                    }
                } else if (response.statusCode == 422) {
                    json = trimMessage(new String(response.data));
                    if (json != null && !json.equals("")) {
                        displayMessage("You must select any one service to continue!");
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

    public void displayMessage(String toastString) {
        Snackbar snackbar = Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(ContextCompat.getColor(this, R.color.black));
        TextView textView = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(ContextCompat.getColor(this, R.color.white));
        snackbar.show();
    }


    private class Services {
        String id, price;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }
    }


}
