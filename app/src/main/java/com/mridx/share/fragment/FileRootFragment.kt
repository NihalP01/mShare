package com.mridx.share.fragment

import android.os.Bundle
import android.os.StatFs
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.mridx.share.R
import com.mridx.share.adapter.StorageAdapter
import com.mridx.share.data.StorageData
import com.mridx.share.utils.StorageUtil
import kotlinx.android.synthetic.main.file_root_view.*
import kotlinx.android.synthetic.main.files_fragment.*
import java.io.File

class FileRootFragment : Fragment(), (StorageData) -> Unit {

    lateinit var storageAdapter: StorageAdapter

    enum class ROOT_TYPE {
        SD, IN
    }

    var onRootItemClicked: ((StorageData) -> Unit?)? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.file_root_view, container, false);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //fileRootView.visibility = View.VISIBLE

        storageAdapter = StorageAdapter()
        storageAdapter.onStorageClicked = this
        storageHolder.apply {
            layoutManager = LinearLayoutManager(view.context)
            adapter = storageAdapter
        }
        //getData(StorageUtil.getStorageList())
        storageAdapter.setList(getData(StorageUtil.getStorageList()))

    }

    private fun getData(storageList: List<StorageUtil.StorageInfo>): ArrayList<StorageData> {
        val storageDataList: ArrayList<StorageData> = ArrayList()
        for (storageInfo: StorageUtil.StorageInfo in storageList) {
            val file = File(storageInfo.path.replace("/mnt/media_rw/", "/storage/"))
            val size = (StatFs(file.absolutePath).availableBytes / (1024 * 1024 * 1024)).toDouble()
            val total = (StatFs(file.absolutePath).totalBytes / (1024 * 1024 * 1024)).toDouble()

            storageDataList.add(StorageData(storageInfo.displayName, storageInfo.path, size, total))
        }
        return storageDataList
    }

    private fun setupView(storageList: ArrayList<StorageData>) {

    }

    override fun invoke(storageData: StorageData) {
        onRootItemClicked?.invoke(storageData)
    }


}