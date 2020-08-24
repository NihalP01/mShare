package com.mridx.share.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mridx.share.R
import com.mridx.share.data.PhotoData

class PhotoAdapter(private val photoList: List<PhotoData>) : RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.photo_view, parent, false)
        return PhotoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        return holder.bind(photoList[position])
    }

    override fun getItemCount() = photoList.size

    class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val image: ImageView = itemView.findViewById(R.id.folderThubm)
        private val title: TextView = itemView.findViewById(R.id.folderTitle)
        private val size: TextView = itemView.findViewById(R.id.noOfImages)

        fun bind(imageList: PhotoData) {
            title.text = imageList.title
            size.text = imageList.size

            val imagePath = imageList.image
            Log.d("priya", imagePath)
            Glide
                    .with(itemView.context)
                    .load(imagePath)
                    .into(image)
        }
    }
}