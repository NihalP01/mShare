package com.mridx.share.utils;

import java.io.File;

public enum FileType {

    FILE, FOLDER;

    public static FileType getFileType(File file) {
        return file.isDirectory() ? FOLDER : FILE;
    }

}
