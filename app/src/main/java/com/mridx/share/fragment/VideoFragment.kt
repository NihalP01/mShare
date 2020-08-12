package com.mridx.share.fragment

import android.annotation.SuppressLint
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
import com.mridx.share.adapter.VideoAdapter
import com.mridx.share.data.VideoData
import java.text.DecimalFormat

class VideoFragment : Fragment() {

    val MB = (1024*1024).toDouble()
    val KB = 1024.toDouble()
    val df = DecimalFormat("#.##")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.video_fragment, container, false)
        val videoHolder: RecyclerView = view.findViewById(R.id.videoHolder)
        videoHolder.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = VideoAdapter(getAllVideo(context))
        }
        return view
    }

    @SuppressLint("Recycle")
    private fun getAllVideo(context: Context): List<VideoData> {
        val videoList = ArrayList<VideoData>()
        val contentResolver = context.contentResolver
        val uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI

        val cursor = contentResolver.query(
                uri,
                null,
                null,
                null,
                null
        )
        if (cursor != null && cursor.moveToFirst()) {
            do {
                val title = cursor.getColumnIndex(MediaStore.Video.Media.TITLE)
                val videoTitle = cursor.getString(title)

                val videoPath = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA))

                val size = cursor.getColumnIndex(MediaStore.Video.Media.SIZE)
                val videoSize = getVideoSize(cursor.getString(size))

                videoList.add(VideoData(videoTitle, videoPath, videoSize!!))

            } while (cursor.moveToNext())
        }
        return videoList
    }

    private fun getVideoSize(videoSize: String): String?{
        val myValue = videoSize.toDouble()
        return if (myValue > MB){
            df.format(myValue/MB ) + " MB"
        }
        else df.format(myValue/KB)+ " KB"
    }
}