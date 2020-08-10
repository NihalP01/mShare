package com.mridx.share.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.mridx.share.R;
import com.mridx.share.data.FileData;
import com.mridx.share.fragment.FilesListFragment;
import com.mridx.share.ui.MainUI;
import com.mridx.share.utils.FileType;

import java.io.File;
import java.util.ArrayList;

public class FilesListAdapter extends RecyclerView.Adapter<FilesListAdapter.ViewHolder> {

    private ArrayList<FileData> fileList = new ArrayList<>();

    OnAdapterItemClicked onAdapterItemClicked;

    public interface OnAdapterItemClicked {
        void onClicked(FileData fileData);
    }

    public void setOnAdapterItemClicked(OnAdapterItemClicked onAdapterItemClicked) {
        this.onAdapterItemClicked = onAdapterItemClicked;
    }

    @NonNull
    @Override
    public FilesListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.file_view, null, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FilesListAdapter.ViewHolder holder, int position) {
        holder.bind(fileList.get(position));
    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }

    public void setFileList(ArrayList<FileData> fileList) {
        this.fileList = fileList;
        //setHasStableIds(true);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ShapeableImageView fileIconView, fileCheckbox;
        private MaterialTextView nameView, folderView, sizeView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameView = itemView.findViewById(R.id.nameTextView);
            folderView = itemView.findViewById(R.id.folderTextView);
            sizeView = itemView.findViewById(R.id.totalSizeTextView);
            fileIconView = itemView.findViewById(R.id.fileIconView);
            fileCheckbox = itemView.findViewById(R.id.fileCheckbox);
        }

        public void bind(FileData fileData) {
            nameView.setText(fileData.getName());
            if (fileData.getFileType() == FileType.FOLDER) {
                folderView.setVisibility(View.VISIBLE);
                sizeView.setVisibility(View.GONE);
                folderView.setText(fileData.getSubFiles() + " files");
            } else {
                folderView.setVisibility(View.GONE);
                sizeView.setVisibility(View.VISIBLE);
                sizeView.setText(String.format("%.1f", fileData.getSizeInMB()) + "MB");
                int res = getIcon(fileData.getExt());
                //fileIconView.setImageResource(res);
                Glide.with(itemView.getContext()).asBitmap().load(new File(fileData.getPath())).placeholder(res).into(fileIconView);
            }
            int icon = fileData.isSelected() ? R.drawable.ic_selected : R.drawable.custom_checkbox;
            fileCheckbox.setImageResource(icon);
            itemView.findViewById(R.id.checkView).setOnClickListener(view -> {
                fileData.setSelected(true);
                notifyDataSetChanged();
            });
            itemView.setOnClickListener(view -> onAdapterItemClicked.onClicked(fileData));

        }
    }

    private int getIcon(String ext) {
        ext = ext.toLowerCase();
        if (ext.equalsIgnoreCase("apk")) return R.drawable.ic_app;
        if (ext.equalsIgnoreCase("pdf")) return R.drawable.ic_pdf;
        if (ext.equalsIgnoreCase("zip")) return R.drawable.ic_zip;
        if (ext.equalsIgnoreCase("mp3")) return R.drawable.ic_music;
        return R.drawable.ic_file;
    }
}
