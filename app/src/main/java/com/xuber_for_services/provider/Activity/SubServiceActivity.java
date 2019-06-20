package com.xuber_for_services.provider.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.xuber_for_services.provider.Adapter.SettingsServiceListAdapter;
import com.xuber_for_services.provider.Adapter.SubServiceAdapter;
import com.xuber_for_services.provider.Constant.URLHelper;
import com.xuber_for_services.provider.Fragments.SettingsFragment;
import com.xuber_for_services.provider.Helper.SharedHelper;
import com.xuber_for_services.provider.Models.ServiceListModel;
import com.xuber_for_services.provider.Models.SubServiceModel;
import com.xuber_for_services.provider.R;
import com.xuber_for_services.provider.Utils.KeyHelper;
import com.xuber_for_services.provider.XuberServicesApplication;

import org.json.JSONArray;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class SubServiceActivity extends AppCompatActivity {
    private RecyclerView subServiceRecyclerview;
    ArrayList<SubServiceModel> subSer_list;
    private SubServiceAdapter subServiceAdapter;
    String vId,Name;
    TextView lblName,subSv;
    ImageView userImg, back_img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subservice);

        subServiceRecyclerview = (RecyclerView) findViewById(R.id.subServiceRecyclerview);
        lblName = (TextView) findViewById(R.id.lblName);
        subSv = (TextView) findViewById(R.id.subSv);
        userImg = (ImageView) findViewById(R.id.profile_image);
        back_img = (ImageView) findViewById(R.id.back_img);

        back_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        setProviderDetails();

        subSer_list = new ArrayList<>();

        if (getIntent() != null) {
            ServiceListModel serviceListModel = (ServiceListModel) getIntent().getSerializableExtra("ServiceListModel");
            vId = serviceListModel.getId();
            Name = serviceListModel.getName();
            //  vDesp = vehicles.getVehicleDescription();
        }
        subSv.setText(Name+" "+"Sub-Services you provide");

        new PostReview().execute();
    }

    //------------------------------------------------

    public class PostReview extends AsyncTask<String, Void, String> {
        ProgressDialog dialog;

        protected void onPreExecute() {
            dialog = new ProgressDialog(SubServiceActivity.this);
            dialog.show();

        }

        protected String doInBackground(String... arg0) {

            try {

                URL url = new URL("http://erapidservice.com/public/api/provider/sub_services");

                JSONObject postDataParams = new JSONObject();
                postDataParams.put("service_id", vId);

                Log.e("postDataParams", postDataParams.toString());

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000 /* milliseconds*/);
                conn.setConnectTimeout(15000  /*milliseconds*/);
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
                    String responce = jsonObject.getString("responce");
                    if (responce.equals("true")) {
                        JSONArray dataArray = jsonObject.getJSONArray("data");
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject dataObj = dataArray.getJSONObject(i);
                            String name = dataObj.getString("name");
                            subSer_list.add(new SubServiceModel(name));
                        }
                        setupRecyclerView();

                    } else {
                        // txtresult.setText("Oops! No Data.");
                        Toast.makeText(SubServiceActivity.this, "Oops! No Data.", Toast.LENGTH_LONG).show();
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
    }

    //----------------------------------------

    private void setupRecyclerView() {
        subServiceAdapter = new SubServiceAdapter(SubServiceActivity.this,subSer_list);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(SubServiceActivity.this, 3);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(SubServiceActivity.this, R.dimen._5sdp);
        subServiceRecyclerview.addItemDecoration(itemDecoration);
        subServiceRecyclerview.setLayoutManager(gridLayoutManager);
        subServiceRecyclerview.setAdapter(subServiceAdapter);
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

    //------------------------------------

    public void setProviderDetails() {
        String name = SharedHelper.getKey(SubServiceActivity.this, KeyHelper.KEY_FIRST_NAME);
        if (name != null && !name.equalsIgnoreCase("null") && name.length() > 0) {
            String lName = SharedHelper.getKey(SubServiceActivity.this, "last_name");
            if (lName != null && !lName.equalsIgnoreCase("null") && lName.length() > 0)
                lblName.setText(name + " " + lName);
            else
                lblName.setText(name);
        }
        if (!SharedHelper.getKey(SubServiceActivity.this, "picture").equalsIgnoreCase("")) {
            Picasso.with(SubServiceActivity.this).load(SharedHelper.getKey(SubServiceActivity.this, "picture"))
                    .fit().centerCrop().memoryPolicy(MemoryPolicy.NO_CACHE).
                    placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(userImg);
        }
    }
}
