package com.xuber_for_services.provider.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xuber_for_services.provider.Models.SubServiceModel;
import com.xuber_for_services.provider.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SubServiceAdapter extends RecyclerView.Adapter<SubServiceAdapter.ViewHolder> {

    private static final String TAG = "SubServiceAdapter";
    private ArrayList<SubServiceModel> subSerlist;
    public Context context;
    String resId = "";
    String finalStatus = "";
    private String prID;

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView sbser_list_name, email,address,ratting;
        CircleImageView service_image_icon1;
        CardView card;
        ImageView recpdeleteBtn;
        int pos;

        public ViewHolder(View view) {
            super(view);

            service_image_icon1 = (CircleImageView) view.findViewById(R.id.service_image_icon1);
            sbser_list_name = (TextView) view.findViewById(R.id.sbser_list_name);
        }
    }

    public static Context mContext;

    public SubServiceAdapter(Context mContext, ArrayList<SubServiceModel> subSer_list) {
        context = mContext;
        subSerlist = subSer_list;

    }

    @Override
    public SubServiceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sub_service_row, parent, false);

        return new SubServiceAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final SubServiceAdapter.ViewHolder viewHolder, final int position) {
        SubServiceModel subServiceModel = subSerlist.get(position);
        viewHolder.sbser_list_name.setText(subServiceModel.getName());

        viewHolder.service_image_icon1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Please contact admin to update your service", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return subSerlist.size();
    }
}
