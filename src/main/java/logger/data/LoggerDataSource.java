package logger.data;

import application.Lauren;
import logger.Logger;
import lombok.Getter;

import java.io.*;
import java.time.LocalDateTime;

@Getter
public class LoggerDataSource {

    private final File file;
    private final BufferedWriter bufferedWriter;

    public LoggerDataSource(String logName) throws IOException {
        LocalDateTime now = LocalDateTime.now();
        int month = now.getMonthValue();
        int day = now.getDayOfMonth();

        // create a infinite log archives
        int i = 1;
        File file = new File("logs/" + logName + "-" + day + "-" + month + "-" + i + ".log");
        while (file.exists()) {
            file = new File("logs/" + logName + "-" + day + "-" + month + "-" + i + ".log");
            i++;
        }
        this.file = file;

        // try to create log file
        try {
            if (!file.createNewFile()) Logger.log("ERROR Can't create a log file");
        } catch (Exception exception) {
            Logger.log("ERROR An error ocurred on create a log file");
        }

        // starting log
        now = LocalDateTime.now();

        bufferedWriter = new BufferedWriter(new FileWriter(file.getAbsolutePath()));
        bufferedWriter.write("Starting log at " + now.getHour() + "h " + now.getMinute() + "m " + now.getSecond() + "s");
        bufferedWriter.newLine();
        bufferedWriter.flush();

        Logger.log("Registering logs to " + file.getName());
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
}
