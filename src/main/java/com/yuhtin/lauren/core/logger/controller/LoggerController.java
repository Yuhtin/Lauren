package com.yuhtin.lauren.core.logger.controller;

import com.yuhtin.lauren.core.logger.Logger;
import com.yuhtin.lauren.models.enums.LogType;
import lombok.Getter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

@Getter
public class LoggerController {

    private static final LoggerController INSTANCE = new LoggerController();

    private final File file;
    private BufferedWriter bufferedWriter;

    public LoggerController() {
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

        // try to create log file
        try {
            file.createNewFile();
        } catch (Exception exception) {
            Logger.log("Cannot create a log file");
        }

        // starting log
        LocalDateTime now = LocalDateTime.now();

        try {
            bufferedWriter = new BufferedWriter(new FileWriter(file.getAbsolutePath()));
        } catch (Exception exception) {
            Logger.log("Cannot instantiate BufferedWriter");
        }

        Logger.log("Registering logs to " + file.getName(), LogType.STARTUP);
        Logger.log("Lauren is now registering logs", LogType.STARTUP).save();
        Logger.log("Starting log at " + now.getHour() + "h " + now.getMinute() + "m " + now.getSecond() + "s", LogType.STARTUP).save();
    }

    public static LoggerController get() {
        return INSTANCE;
    }

    public void toFile(String log) {
        LocalDateTime now = LocalDateTime.now();
        String result = "[" + now.getHour() + ":" + now.getMinute() + ":" + now.getSecond() + "] " + log;
        try {
            bufferedWriter.write(result);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException exception) {
            Logger.log("Attemp to save log: " + log, LogType.WARN);
        }
    }
}
