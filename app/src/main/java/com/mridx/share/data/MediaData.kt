package com.mridx.share.data

data class MusicData(
        var title: String,
        var albumArt: String,
        var audioSize: String
)

data class VideoData(
        var title: String,
        var thumbnail: String,
        var videoSize: String
)
data class PhotoData(
        val image: String,
        val title: String,
        val size: String
)
data class ImageFolder(
        val folderName: String,
        val folderThumb: String,
        val imageCount: String
)