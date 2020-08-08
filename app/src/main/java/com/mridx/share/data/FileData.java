package com.mridx.share.data;

import com.mridx.share.utils.FileType;

public class FileData {

    private String path, name, ext = " ";
    private FileType fileType;
    private Double sizeInMB;
    private int subFiles = 0;

    public FileData(String path, String name, String ext, FileType fileType, Double sizeInMB, int subFiles) {
        this.path = path;
        this.name = name;
        this.ext = ext;
        this.fileType = fileType;
        this.sizeInMB = sizeInMB;
        this.subFiles = subFiles;
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    public String getExt() {
        return ext;
    }

    public FileType getFileType() {
        return fileType;
    }

    public Double getSizeInMB() {
        return sizeInMB;
    }

    public int getSubFiles() {
        return subFiles;
    }
}
