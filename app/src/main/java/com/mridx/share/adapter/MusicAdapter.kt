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
import com.mridx.share.data.MusicData

class MusicAdapter(private val musicList: List<MusicData>) : RecyclerView.Adapter<MusicAdapter.myViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.music_view, parent, false)
        return myViewHolder(view)
    }

    override fun getItemCount(): Int {
        Log.d("myTag", musicList.size.toString())
        return musicList.size
    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {
        return holder.bind(musicList[position])
    }

    class myViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.musicNameView)
        private val albumArt: ImageView = itemView.findViewById(R.id.musicThumbView)
        private val size: TextView = itemView.findViewById(R.id.audioSize)

        fun bind(musicInfo: MusicData) {
            title.text = musicInfo.title
            size.text = musicInfo.audioSize

            Log.d("myTag", musicInfo.title)

            Glide.with(itemView.context).load(musicInfo.albumArt).into(albumArt)
        }
    }
}