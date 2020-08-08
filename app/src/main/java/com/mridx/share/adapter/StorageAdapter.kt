package com.mridx.share.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mridx.share.R
import com.mridx.share.data.StorageData
import kotlinx.android.synthetic.main.storage_view.view.*
import java.util.*
import kotlin.collections.ArrayList

class StorageAdapter : RecyclerView.Adapter<StorageAdapter.ViewHolder>() {

    private var storageList: ArrayList<StorageData> = ArrayList()

    var onStorageClicked: ((StorageData) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.storage_view, null))
    }

    override fun getItemCount(): Int {
        return storageList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(storageList[position])
    }

    fun setList(storageList: ArrayList<StorageData>) {
        this.storageList = storageList
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(storageData: StorageData) {
            itemView.storageNameView.text = storageData.name
            itemView.storageTotalView.text = "Total : ${storageData.totalSize} GB"
            itemView.storageAvailableView.text = "Available : ${storageData.availableSize} GB"
            var drawable: Int = R.drawable.ic_sdcard
            if (storageData.name.toLowerCase(Locale.ROOT) == "internal storage")
                drawable = R.drawable.ic_phone
            itemView.storageIconView.setImageResource(drawable)
            itemView.setOnClickListener { onStorageClicked?.invoke(storageData) }
        }
    }
}