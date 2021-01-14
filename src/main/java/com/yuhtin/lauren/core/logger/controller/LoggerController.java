package com.yuhtin.lauren.core.logger.controller;


import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.yuhtin.lauren.core.logger.Logger;
import com.yuhtin.lauren.models.enums.LogType;
import lombok.Data;
import lombok.Getter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

@Data
@Singleton
public class LoggerController {

    private File file;
    private BufferedWriter bufferedWriter;

    @Inject private Logger logger;

    public void create() {

        // create a infinite log archives
        String prefix = "logs/log-";
        int i = 1;

        File file = new File(prefix + "1.zip");
        while (file.exists()) {
            ++i;
            file = new File(prefix + i + ".zip");
        }

        file = new File(prefix + i + ".log");
        this.file = file;

        try {
            // try to create log file
            file.createNewFile();

            // starting log
            bufferedWriter = new BufferedWriter(new FileWriter(file.getAbsolutePath()));

        } catch (Exception exception) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        this.logger.log("Registering logs to " + file.getName(), LogType.STARTUP);
        this.logger.log("Lauren is now registering logs", LogType.STARTUP);
        this.logger.log("Starting log at " + now.getHour() + "h " + now.getMinute() + "m " + now.getSecond() + "s", LogType.STARTUP);
    }

    public void toFile(String log) {
        try {
            bufferedWriter.write(log);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException exception) {
            this.logger.warning("Attemp to save log: " + log);
        }
    }
}
