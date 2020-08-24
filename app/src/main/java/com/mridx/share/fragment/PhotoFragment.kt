package com.mridx.share.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mridx.share.R
import com.mridx.share.adapter.PhotoAdapter
import com.mridx.share.data.PhotoData
import java.text.DecimalFormat

class PhotoFragment : Fragment() {

    val MB = (1024 * 1024).toDouble()
    val KB = 1024.toDouble()
    val df = DecimalFormat("#.##")

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.photo_fragment, container, false)
        val photoRecycler: RecyclerView = view.findViewById(R.id.folderHolder)
        photoRecycler.apply {
            setHasFixedSize(true)
            adapter = PhotoAdapter(getPhotos(context))
            layoutManager = LinearLayoutManager(context)
        }
        return view
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("Recycle")
    private fun getPhotos(context: Context): List<PhotoData> {
        val photoList = ArrayList<PhotoData>()
        val contentResolver = context.contentResolver
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(MediaStore.MediaColumns.DATA, MediaStore.Images.Media.TITLE, MediaStore.Images.Media.SIZE, MediaStore.Images.Media.DATE_TAKEN, MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
        val orderBy: String = MediaStore.MediaColumns.DATE_TAKEN + " DESC"

        val cursor = contentResolver.query(
                uri,
                projection,
                null,
                null,
                orderBy
        )
        if (cursor != null && cursor.moveToFirst()) {
            do {

                val imagePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA))
                val folderName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME))
                Log.d("myTag", "$folderName")

                val imageTitle = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.TITLE))
                val imageSize = getImageSize(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)))

                photoList.add(PhotoData(imagePath, imageTitle, imageSize!!))
            } while (cursor.moveToNext())
        }
        return photoList
    }

    private fun getImageSize(imageSize: String): String? {
        val myValue = imageSize.toDouble()
        return if (myValue > MB) {
            df.format(myValue / MB) + " MB"
        } else df.format(myValue / KB) + " KB"
    }
}