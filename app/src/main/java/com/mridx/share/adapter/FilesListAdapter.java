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
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ShapeableImageView fileIconView;
        private MaterialTextView nameView, folderView, sizeView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameView = itemView.findViewById(R.id.nameTextView);
            folderView = itemView.findViewById(R.id.folderTextView);
            sizeView = itemView.findViewById(R.id.totalSizeTextView);
            fileIconView = itemView.findViewById(R.id.fileIconView);
        }

        public void bind(FileData fileData) {
            nameView.setText(fileData.getName());
            if (fileData.getFileType() == FileType.FOLDER) {
                folderView.setVisibility(View.VISIBLE);
                sizeView.setVisibility(View.GONE);
                folderView.setText(fileData.getSubFiles() + " files");
            } else {
                fileIconView.setImageDrawable(itemView.getContext().getDrawable(R.drawable.ic_file));
                folderView.setVisibility(View.GONE);
                sizeView.setVisibility(View.VISIBLE);
                sizeView.setText(String.format("%.2f", fileData.getSizeInMB()) + "mb");
                //MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                //mediaMetadataRetriever.setDataSource(itemView.getContext(), Uri.fromFile(new File(fileData.getPath())));
                //byte[] data = mediaMetadataRetriever.getEmbeddedPicture();
                //Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                //fileIconView.setImageBitmap(bitmap);
                Glide.with(itemView.getContext()).asBitmap().load(new File(fileData.getPath())).placeholder(R.drawable.ic_file).into(fileIconView);
            }

            itemView.setOnClickListener(view -> onAdapterItemClicked.onClicked(fileData));

        }
    }
}
