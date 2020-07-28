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
import com.mridx.share.callback.AppAdapterClicked;
import com.mridx.share.data.AppData;

import java.util.ArrayList;

public class AppAdapter extends RecyclerView.Adapter<AppAdapter.MyViewHolder> {

    private ArrayList<AppData> appList = new ArrayList<>();
    private ArrayList<AppData> selectedAppList = new ArrayList<>();

    private int SELECTED = 0, NORMAL = 1;

    AppAdapterClicked appAdapterClicked;

    public void setAppAdapterClicked(AppAdapterClicked appAdapterClicked) {
        this.appAdapterClicked = appAdapterClicked;
    }

    public AppAdapter() {
    }

    @NonNull
    @Override
    public AppAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == SELECTED)
            return MyViewHolder.newInstance(LayoutInflater.from(parent.getContext()).inflate(R.layout.app_view_selected, null));
        else
            return MyViewHolder.newInstance(LayoutInflater.from(parent.getContext()).inflate(R.layout.app_view, null));
    }

    @Override
    public void onBindViewHolder(@NonNull AppAdapter.MyViewHolder holder, int position) {
        holder.appNameView.setText(appList.get(position).getAppName());
        holder.appSizeView.setText(appList.get(position).getApkSize());
        holder.appIconView.setImageDrawable(appList.get(position).getAppIcon());
        holder.itemView.setOnClickListener(view -> {
            if (appList.get(position).isSelected()) {
                appList.get(position).setSelected(false);
            } else {
                appList.get(position).setSelected(true);
            }
            //appList.get(position).setSelected(!appList.get(position).isSelected());
            notifyDataSetChanged();
            sendSelectedApp();
        });
    }

    private void sendSelectedApp() {
        selectedAppList.clear();
        for (int i = 0; i < appList.size(); i++) {
            AppData appData = appList.get(i);
            if (appData.isSelected())
                selectedAppList.add(appData);
        }
        appAdapterClicked.onClicked(selectedAppList);
    }

    public void setAllChecked(boolean b) {
        selectedAppList.clear();
        for (int i = 0; i < appList.size(); i++) {
            AppData appData = appList.get(i);
            appData.setSelected(b);
            selectedAppList.add(appData);
        }
        notifyDataSetChanged();
        if (!b) selectedAppList.clear();
        appAdapterClicked.onClicked(selectedAppList);
    }

    public void setAppList(ArrayList<AppData> appList) {
        this.appList = appList;
    }

    @Override
    public int getItemCount() {
        return appList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (appList.get(position).isSelected())
            return SELECTED;
        else
            return NORMAL;
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
