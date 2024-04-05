package com.yuhtin.lauren.util;

import java.io.*;
import java.util.Date;
import java.util.logging.*;

public class LoggerUtil {

    public static Logger getLogger() {
        return Logger.getLogger("Lauren");
    }

    public static void printException(Throwable exception) {
        Writer buffer = new StringWriter();

        PrintWriter pw = new PrintWriter(buffer);
        exception.printStackTrace(pw);

        getLogger().severe(buffer.toString());
    }

    public static void formatLogger(boolean debugMode) {
        Logger logger = getLogger();

        Date date = new Date();
        int year = date.getYear() + 1900;
        int month = date.getMonth() + 1;
        int day = date.getDate();

        int threashold = 1;
        String basePattern = "logs/" + year + "-" + month + "-" + day + "-";

        String fileName = basePattern + threashold + ".log";
        while (new File(fileName).exists()) {
            threashold++;
            fileName = basePattern + threashold + ".log";
        }

        logger.setUseParentHandlers(false);

        ConsoleHandler formatHandler = new ConsoleHandler();
        formatHandler.setLevel(Level.CONFIG);
        String finalFileName = fileName;
        formatHandler.setFormatter(new SimpleFormatter() {
            private static final String format = "[%1$tT] [%2$s] [%3$s] %4$s %n";

            @Override
            public synchronized String format(LogRecord record) {
                String[] split = record.getSourceClassName().split("\\.");
                String formatted = String.format(format,
                        new Date(record.getMillis()),
                        record.getLevel().getLocalizedName(),
                        split[split.length - 1],
                        record.getMessage()
                );


                File file = new File(finalFileName);

                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
                    writer.write(formatted);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                return formatted;
            }
        });

        formatHandler.setFilter(record -> debugMode || record.getLevel() != Level.CONFIG);

        Logger.getAnonymousLogger().addHandler(formatHandler);
        logger.addHandler(formatHandler);
        logger.setLevel(Level.CONFIG);
    }
}
