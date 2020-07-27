package com.mridx.share.adapter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.mridx.share.R;
import com.mridx.share.data.AppData;

import java.util.ArrayList;

public class AppAdapter extends RecyclerView.Adapter<AppAdapter.MyViewHolder> {

    private ArrayList<AppData> appList = new ArrayList<>();

    public AppAdapter() {
    }

    @NonNull
    @Override
    public AppAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.app_view, null));
    }

    @Override
    public void onBindViewHolder(@NonNull AppAdapter.MyViewHolder holder, int position) {
        holder.appNameView.setText(appList.get(position).getAppName());
        holder.appSizeView.setText(appList.get(position).getApkSize());
        holder.appIconView.setImageDrawable(appList.get(position).getAppIcon());
    }

    public void setAppList(ArrayList<AppData> appList) {
        this.appList = appList;
    }

    @Override
    public int getItemCount() {
        return appList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private MaterialTextView appNameView, appSizeView;
        private ShapeableImageView appIconView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            appNameView = itemView.findViewById(R.id.appName);
            appSizeView = itemView.findViewById(R.id.appSize);
            appIconView = itemView.findViewById(R.id.appIconView);
        }

        public static MyViewHolder newInstance(View view) {
            return new MyViewHolder(view);
        }
    }
}
