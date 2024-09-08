package com.yuhtin.lauren.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.entities.Message;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FileUtil {

    public static void cleanUp(Path path) throws IOException {

    }

    public static void writeToZip(File file, ZipOutputStream zipStream) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            ZipEntry zipEntry = new ZipEntry(file.getName());

            zipStream.putNextEntry(zipEntry);
            byte[] bytes = new byte[1024];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                zipStream.write(bytes, 0, length);
            }

            zipStream.closeEntry();
        }
    }

    public static File getAttachment(Message.Attachment attachment) {
        File file = new File("temporary/" + attachment.getFileName());
        try {
            if (!file.createNewFile()) return null;
            return attachment.getProxy().downloadToFile(file).get();
        } catch (Exception exception) {
            return null;
        }
    }

}
