package com.mridx.share.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mridx.share.R
import com.mridx.share.data.ImageFolder
import java.util.zip.Inflater

class ImageFolderAdapter(private val folderList: List<ImageFolder>): RecyclerView.Adapter<ImageFolderAdapter.myViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.image_folder_view, parent, false)
        return myViewHolder(view)
    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {
        holder.bind(folderList[position])
    }

    override fun getItemCount() = folderList.size

    class myViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
         private val folderName: TextView = itemView.findViewById(R.id.folderTitle)
        private val folderThumb: ImageView = itemView.findViewById(R.id.folderThubm)
        private val folderImageCount: TextView = itemView.findViewById(R.id.noOfImages)

        fun bind(imageFolderList: ImageFolder){
            folderName.text = imageFolderList.folderName
            folderImageCount.text = imageFolderList.imageCount
            Glide.with(itemView.context).load(imageFolderList.folderThumb).into(folderThumb)

        }
    }

}