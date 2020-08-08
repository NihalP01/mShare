package com.mridx.share.utils;

import com.mridx.share.data.FileData;

import java.io.File;
import java.io.FileFilter;
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
        File[] files = new File(path).listFiles();
        for (File file : files) {
            fileDataList.add(getFileData(file));
        }
        return fileDataList;
    }

    private static FileData getFileData(File file) {
        return new FileData(
                file.getPath(),
                file.getName(),
                file.getName().substring(file.getName().lastIndexOf(".") + 1),
                FileType.getFileType(file),
                getFileSize(file.length()),
                file.isDirectory() ? file.listFiles().length : 0
        );
    }

    private static Double getFileSize(long length) {
        return Double.parseDouble(String.valueOf(length)) / (1024 * 1024);
    }

}
