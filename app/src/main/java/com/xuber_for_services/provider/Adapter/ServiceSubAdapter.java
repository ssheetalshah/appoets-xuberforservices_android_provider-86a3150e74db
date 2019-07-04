package com.xuber_for_services.provider.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.xuber_for_services.provider.Activity.SubServiceActivity;
import com.xuber_for_services.provider.Helper.AppHelper;
import com.xuber_for_services.provider.Models.ServiceListModel;
import com.xuber_for_services.provider.Models.SubServiceModel;
import com.xuber_for_services.provider.R;

import org.json.JSONArray;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ServiceSubAdapter extends RecyclerView.Adapter<ServiceSubAdapter.ViewHolder> {

    private ArrayList<SubServiceModel> listModels;
    private Context context;
    JSONArray jsonArraylist;
    private RadioButton lastChecked = null;
    BottomSheetBehavior behavior;
    String TAG = "ServiceSubAdapter";
    private int pos;
    private ServiceClickListener serviceClickListener;
    SubServiceModel subServiceModel;
    boolean[] selectedService;
    boolean select;
   // private SubServiceModel serviceListModel;

    public ServiceSubAdapter(ArrayList<SubServiceModel> listModel, Context context) {
        this.listModels = listModel;
        this.context = context;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }

    public interface ServiceClickListener {
        void onServiceClick(boolean[] selectedService, SubServiceModel subServiceModel);

        void onServiceUnSelect(boolean[] selectedService, SubServiceModel subServiceModel);
    }


    public List<SubServiceModel> getServiceListModel() {
        return listModels;
    }

    public void setServiceClickListener(ServiceClickListener serviceClickListener) {
        this.serviceClickListener = serviceClickListener;
    }

    @Override
    public ServiceSubAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.service_list_item, parent, false);
        ServiceSubAdapter.ViewHolder vh = new ServiceSubAdapter.ViewHolder(v);
        return vh;
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView service_list_name, servicePriceTxt;
        ImageView service_image_icon, selectImg;
        CheckBox service_checkbox;
        RecyclerView rll;

        public ViewHolder(View itemView) {
            super(itemView);
            service_image_icon = (ImageView) itemView.findViewById(R.id.service_image_icon);
            service_list_name = (TextView) itemView.findViewById(R.id.service_list_name);
            servicePriceTxt = (TextView) itemView.findViewById(R.id.service_price);
            selectImg = (ImageView) itemView.findViewById(R.id.select);
            service_list_name.setOnClickListener(this);
            service_image_icon.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            pos = (int) v.getTag();
          //  SubServiceModel subServiceModel = listModels.get(pos);
            Log.e("id is  ", "" + listModels.get(pos));
            if (serviceClickListener != null && select) {
                pos = (int) v.getTag();
                SubServiceModel subServiceModel = listModels.get(pos);
                Log.e("id is  ", "" + listModels.get(pos));
                if (!selectedService[pos] && subServiceModel.getAvailable().equalsIgnoreCase("false")) {
                    /*if (serviceListModel.getPricePerHour() != null && !serviceListModel.getPricePerHour().equalsIgnoreCase("null")
                            && serviceListModel.getPricePerHour().length() > 0 &&
                            Integer.parseInt(serviceListModel.getPricePerHour()) > 0)
                        showDialog(serviceListModel.getPricePerHour(), ViewHolder.this);
                    else
                        showDialog(null, ViewHolder.this);*/

                    subServiceModel = listModels.get(pos);
                    subServiceModel.setAvailable("true");
                  //  Log.d(TAG, "onClick: " + subServiceModel.getPricePerHour());
                    serviceClickListener.onServiceClick(selectedService, subServiceModel);
                    selectedService[pos] = true;
                    service_list_name.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
                    selectImg.setVisibility(View.VISIBLE);
                    /*Intent intent = new Intent(context, SubServiceActivity.class);
                    context.startActivity(intent);*/
                } else {
                    subServiceModel = listModels.get(pos);
                    selectedService[pos] = false;
                    service_list_name.setTextColor(ContextCompat.getColor(context, android.R.color.black));
                    serviceClickListener.onServiceUnSelect(selectedService, subServiceModel);
//                    Toast.makeText(context, "sssssssss", Toast.LENGTH_SHORT).show();
                    subServiceModel.setAvailable("false");
                    selectImg.setVisibility(View.GONE);
                    servicePriceTxt.setVisibility(View.GONE);
                }
            } else {
                displayMessage();
            }
        }
    }

    public void displayMessage() {
      /*  SubServiceModel subServiceModel = listModels.get(pos);
        Intent intent = new Intent(context, SubServiceActivity.class);
        intent.putExtra("SubServiceModel", subServiceModel);
        context.startActivity(intent);*/
        Toast.makeText(context, "Please contact admin to update your service", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBindViewHolder(final ServiceSubAdapter.ViewHolder holder, final int position) {

        final SubServiceModel subServiceModel = listModels.get(position);
        selectedService = new boolean[listModels.size()];
        Log.v(TAG, "Response Name " + subServiceModel.getAvailable());
        holder.service_list_name.setText(subServiceModel.getName());
        //Glide.with(context).load(R.drawable.carpentry_select).into(holder.service_image_icon);
        Log.e(TAG, "onBindViewHolder: " + AppHelper.getImageUrl(subServiceModel.getImage()));

        Picasso.with(context).load(AppHelper.getImageUrl(subServiceModel.getImage()))
                .error(R.drawable.grey_bg).placeholder(R.drawable.grey_bg)
                .fit().memoryPolicy(MemoryPolicy.NO_CACHE).centerCrop().into(holder.service_image_icon);

        holder.service_image_icon.setTag(position);
        holder.service_list_name.setTag(position);
//        if (serviceClickListener != null && subServiceModel.getAvailable().equalsIgnoreCase("true")) {
//            holder.service_list_name.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
//            holder.selectImg.setVisibility(View.VISIBLE);
//        } else {
//            holder.service_list_name.setTextColor(ContextCompat.getColor(context, R.color.black));
//            holder.selectImg.setVisibility(View.GONE);
//        }


       /* holder.select_icon.setTag(position);

        if (position == 0 && holder.select_icon.isChecked()) {
            lastChecked = holder.select_icon;
            lastCheckedPos = 0;
        }*/


    }

    public static Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public int getItemCount() {
        return listModels.size();
    }

    private void showDialog(String price, final SettingsServiceListAdapter.ViewHolder viewHolder) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = layoutInflater.inflate(R.layout.get_price_layout, null);
        dialogBuilder.setView(dialogView);

        final EditText editText = (EditText) dialogView.findViewById(R.id.ed_price);
        final Button submitBtn = (Button) dialogView.findViewById(R.id.submit_btn);
        //final ImageView closeImg = (ImageView) dialogView.findViewById(R.id.close_img);
        final AlertDialog alertDialog = dialogBuilder.create();
        if (price != null)
            editText.setText(price);
        alertDialog.show();
       /* closeImg.setVisibility(View.GONE);
        closeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedService[pos] = false;
                alertDialog.dismiss();
            }
        });*/

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getText().length() > 0) {
                    subServiceModel = listModels.get(pos);
                 //   subServiceModel.setPricePerHour(editText.getText().toString());
                //    subServiceModel.setPrice(editText.getText().toString());
                    subServiceModel.setAvailable("true");
                  //  Log.d(TAG, "onClick: " + subServiceModel.getPricePerHour());
                    serviceClickListener.onServiceClick(selectedService, subServiceModel);
                    selectedService[pos] = true;
                    viewHolder.service_list_name.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
                    viewHolder.selectImg.setVisibility(View.VISIBLE);
                    viewHolder.servicePriceTxt.setVisibility(View.VISIBLE);
                    notifyDataSetChanged();
                    alertDialog.dismiss();
                } else {
                    editText.setError(context.getString(R.string.required));
                }
            }
        });
    }
}
