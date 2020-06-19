package com.yuhtin.lauren.utils.serialization;

import com.yuhtin.lauren.core.logger.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class CompressLog {

    public static void zipDirectory(File inputDir, File outputZipFile) {
        String inputDirPath = inputDir.getAbsolutePath();
        byte[] buffer = new byte[1024];

        FileOutputStream fileOs = null;
        ZipOutputStream zipOs = null;
        try {

            fileOs = new FileOutputStream(outputZipFile);
            zipOs = new ZipOutputStream(fileOs);
            String filePath = inputDir.getAbsolutePath();

            Logger.log("Compactando as logs");
            String entryName = filePath.substring(inputDirPath.length() + 1);

            ZipEntry ze = new ZipEntry(entryName);
            zipOs.putNextEntry(ze);
            FileInputStream fileIs = new FileInputStream(filePath);

            int len;
            while ((len = fileIs.read(buffer)) > 0) {
                zipOs.write(buffer, 0, len);
            }
            fileIs.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeQuite(zipOs);
            closeQuite(fileOs);
        }
    }

    private static void closeQuite(OutputStream out) {
        try {
            out.close();
        } catch (Exception e) {
        }
    }
}
