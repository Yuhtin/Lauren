package com.yuhtin.lauren.core.logger.controller;

import com.yuhtin.lauren.core.logger.Logger;
import lombok.Getter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

@Getter
public class LoggerController {

    private static LoggerController INSTANCE;

    private final File file;
    private final BufferedWriter bufferedWriter;

    public LoggerController(String logName) throws IOException {
        INSTANCE = this;
        LocalDateTime now = LocalDateTime.now();
        int year = now.getYear();
        int month = now.getMonthValue();
        int day = now.getDayOfMonth();

        // create a infinite log archives
        String prefix = "logs/" + logName + "-" + day + "-" + month + "-" + year + "-";
        int i = 1;
        File file = new File(prefix + "1.zip");
        while (file.exists()) {
            file = new File(prefix + i + ".zip");
            i++;
        }
        file = new File(prefix + i + ".log");
        this.file = file;

        // try to create log file
        file.createNewFile();

        // starting log
        now = LocalDateTime.now();

        bufferedWriter = new BufferedWriter(new FileWriter(file.getAbsolutePath()));
        bufferedWriter.write("Starting log at " + now.getHour() + "h " + now.getMinute() + "m " + now.getSecond() + "s");
        bufferedWriter.newLine();
        bufferedWriter.flush();

        Logger.log("Registering logs to " + file.getName());
        Logger.log("Lauren is now registering logs").save();
    }

    public void toFile(String log) {
        LocalDateTime now = LocalDateTime.now();
        String result = "[" + now.getHour() + ":" + now.getMinute() + ":" + now.getSecond() + "] " + log;
        try {
            bufferedWriter.write(result);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException exception) {
            Logger.log("Attemp to save log: " + log);
        }
    }

    public static LoggerController get() {
        return INSTANCE;
    }
}
