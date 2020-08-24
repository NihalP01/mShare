package com.mridx.share.fragment

import android.content.Context
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mridx.share.R
import com.mridx.share.adapter.ImageFolderAdapter
import com.mridx.share.data.ImageFolder
import java.util.*

class ImageFragment : Fragment(){
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.image_folder_fragment, container, false)
        val folderRecycler: RecyclerView = view.findViewById(R.id.folderHolder)

        folderRecycler.apply {
            setHasFixedSize(true)
            adapter = ImageFolderAdapter(getAllImagefolder(context))
            layoutManager = LinearLayoutManager(context)
        }

        return view
    }

    private fun getAllImagefolder(context: Context): List<ImageFolder>{
        val picFolders: ArrayList<ImageFolder> = ArrayList<ImageFolder>()
        val picPaths = ArrayList<String>()
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(MediaStore.Images.Media.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
        val contentResolver = context.contentResolver
        val cursor = contentResolver.query(
                uri,
                projection,
                null,
                null,
                null
        )
        if (cursor != null && cursor.moveToFirst()) {
            do {
               val folderName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME))
                val dataPath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA))
                var folderPath = dataPath.substring(0, dataPath.lastIndexOf(folderName + "/"))
                folderPath = folderPath + folderName + "/"
                if(!picPaths.contains(folderPath)){
                    picPaths.add(folderPath)
                    picFolders.add(ImageFolder(folderName, dataPath, "45"))
                }


            }while (cursor.moveToNext())
        }

        return picFolders
    }
}