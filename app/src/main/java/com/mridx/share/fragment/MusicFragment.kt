package com.mridx.share.fragment

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mridx.share.R
import com.mridx.share.adapter.MusicAdapter
import com.mridx.share.data.MusicData


class MusicFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.music_fragment, container, false)
        val musicHolder: RecyclerView = view.findViewById(R.id.musicHolder)
        musicHolder.apply {
            setHasFixedSize(true)
            adapter = MusicAdapter(getAllAudio(context))
            layoutManager = LinearLayoutManager(context)
        }
        return view
    }

    @SuppressLint("Recycle")
    private fun getAllAudio(context: Context): List<MusicData> {
        val audioList = ArrayList<MusicData>()
        val contentResolver = context.contentResolver
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val cursor = contentResolver.query(
                uri,
                null,
                null,
                null,
                null
        )

        if (cursor != null && cursor.moveToFirst()) {
            do {
                val title = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
                val size: Int = cursor.getColumnIndex(MediaStore.Audio.Media.SIZE)
                val songSize = cursor.getString(size)
                val songTitle = cursor.getString(title)
                val albumArt = getAlbumart(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)))
                audioList.add(MusicData(songTitle, albumArt!!, songSize))
            } while (cursor.moveToNext())
        }
        return audioList

    }

    private fun getAlbumart(albumId: String): String? {
        val albumArtUri: Uri = Uri.parse("content://media/external/audio/albumart")
        val uri: Uri = ContentUris.withAppendedId(albumArtUri, albumId.toLong())
        return uri.toString()
    }

}



