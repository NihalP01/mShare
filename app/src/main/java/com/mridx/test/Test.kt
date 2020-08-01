package com.mridx.test

import android.content.Context
import android.os.Bundle
import android.os.PersistableBundle
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class Test() : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)


    }

    class AudioModel(val aPath: String?, val aName: String?, val aAlbum: String?, val aArtist: String?)


    fun getAllAudioFromDevice(context: Context): List<AudioModel> {
        var audioList = ArrayList<AudioModel>()
        val contentResolver = context.contentResolver
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val count = 0
        val songs: ArrayList<String> = ArrayList()
        val cursor = contentResolver.query(
                uri,  // Uri
                null,
                null,
                null,
                null
        )
        if (cursor != null && cursor.moveToFirst()) {
            do {
                val Title = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
                val songTitle = cursor.getString(Title)

                Log.d("myTag", "getAllAudioFromDevice: $songTitle")
                audioList.add(AudioModel("",songTitle,"",""))
                val  audioModel = audioList[0]
            } while (cursor.moveToNext())
        }
        Log.d("myTag", "getAllAudioFromDevice: " + songs.size)
        return audioList
    }
}