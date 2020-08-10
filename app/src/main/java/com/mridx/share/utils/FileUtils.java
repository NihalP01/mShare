package com.mridx.share.utils;

import android.util.Log;

import com.mridx.share.data.FileData;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.net.FileNameMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileUtils {


    public static List<File> getFilesFromPath(String path, boolean showHidden, boolean onlyFolder) {
        File file = new File(path);
        /*return file.listFiles().filter{ showHiddenFiles || !it.name.startsWith(".") }
            .filter { !onlyFolders || it.isDirectory }
            .toList()*/
        return Arrays.asList(file.listFiles());
    }

    public static ArrayList<FileData> getFileModelsFromFiles(String path) {
        ArrayList<FileData> fileDataList = new ArrayList<>();
        ArrayList<FileData> fileDataDirList = new ArrayList<>();
        File[] files = new File(path).listFiles();
                /*.listFiles(file -> {
                    if (file.isHidden() || file.isDirectory()) return false;
                    if (file.isHidden() || file.isFile()) return false;
                    return true;
                });*/

        for (File file : files) {
            if (!file.getName().startsWith(".")) {
                if (file.isDirectory()) {
                    fileDataDirList.add(getFileData(file));
                } else {
                    fileDataList.add(getFileData(file));
                }
            }
        }
        Log.d("kaku", "getFileModelsFromFiles: " + fileDataDirList.size());
        Log.d("kaku", "getFileModelsFromFiles: " + fileDataList.size());
        fileDataDirList.addAll(fileDataList);
        Log.d("kaku", "getFileModelsFromFiles: " + fileDataDirList.size());
        return fileDataDirList;
    }

    private static FileData getFileData(File file) {
        return new FileData(
                file.getPath(),
                file.getName(),
                file.isFile() ? file.getName().substring(file.getName().lastIndexOf(".") + 1) : "",
                FileType.getFileType(file),
                getFileSize(file.length()),
                file.isDirectory() ? file.listFiles().length : 0,
                false
        );
    }

    private static Double getFileSize(long length) {
        return Double.parseDouble(String.valueOf(length)) / (1024 * 1024);
    }

}