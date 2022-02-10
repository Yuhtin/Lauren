package com.yuhtin.lauren.utils;

import java.io.File;

public class InfinityFiles {

    private final String filePrefix;
    private final String folder;
    private final String extension;
    private final String finalFileExtension;

    /**
     * Create instance of class
     * @param filePrefix used to create files, if the prefix is 'log', the files will be 'log-1', 'log-2', 'log-3' ...
     * @param folder that used to create files
     * @param extension file extension
     * @param finalFileExtension extension that the file gets after falling out of use
     */
    public InfinityFiles(String filePrefix, String folder, String extension, String finalFileExtension) {
        this.filePrefix = filePrefix;
        this.folder = folder;
        this.extension = extension;
        this.finalFileExtension = finalFileExtension;
    }

    /**
     *
     * If the extension is .log, returns .log file
     * But, if this .log becomes a .zip file after, the finalFileExtension should be .zip
     *
     * @return the next file name
     */
    public String getNextFile() {

        String prefix = folder + File.separator + filePrefix + "-";

        int fileNumber = 1;

        File file = new File(prefix + "1" + finalFileExtension);
        while (file.exists()) {
            ++fileNumber;
            file = new File(prefix + fileNumber + finalFileExtension);
        }

        return prefix + fileNumber + extension;

    }

}
