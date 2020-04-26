package logger.data;

import logger.Logger;
import lombok.Getter;

import java.io.*;
import java.time.LocalDateTime;

@Getter
public class LoggerDataSource {

    private final File file;
    private final BufferedWriter bufferedWriter;

    public LoggerDataSource() throws IOException {
        LocalDateTime now = LocalDateTime.now();
        int month = now.getMonthValue();
        int day = now.getDayOfMonth();
        int i = 1;
        File file = new File("logs/log-" + day + "-" + month + "-" + i + ".log");
        while (file.exists()) {
            file = new File("logs/log-" + day + "-" + month + "-" + i + ".log");
            i++;
        }
        this.file = file;
        try {
            if (!file.createNewFile()) Logger.log("ERROR Can't create a log file");
        } catch (Exception exception) {
            toFile("ERROR An error ocurred on create a log file");
        }
        bufferedWriter = new BufferedWriter(new FileWriter(file.getAbsolutePath()));
        now = LocalDateTime.now();
        bufferedWriter.write("Starting log at " + now.getHour() + "h " + now.getMinute() + "m " + now.getSecond() + "s");
        bufferedWriter.newLine();
        bufferedWriter.flush();
        System.out.println("Registering logs to " + file.getName());
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
