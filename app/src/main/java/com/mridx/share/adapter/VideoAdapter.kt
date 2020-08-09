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
import com.mridx.share.data.VideoData

class VideoAdapter(private val videoList: List<VideoData>) : RecyclerView.Adapter<VideoAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.video_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        return holder.bind(videoList[position])
    }

    override fun getItemCount() = videoList.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.videoName)
        private val size: TextView = itemView.findViewById(R.id.videoSize)
        private val thumb: ImageView = itemView.findViewById(R.id.videoThumb)

        fun bind(videolist: VideoData) {
            title.text = videolist.title
            size.text = videolist.videoSize

            val filePath = videolist.thumbnail
            Log.d("nihal1", filePath)

            Glide
                    .with(itemView.context)
                    .asBitmap()
                    .load(filePath)
                    .into(thumb)
        }
    }
}