package me.khajiitos.smpessentials.manager;

import me.khajiitos.smpessentials.SMPEssentials;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.*;

public class LogManager {
    private static final Logger logger = Logger.getLogger("SMPEssentials_log");
    private static Handler loggerHandler;

    public static void init() {
        logger.setUseParentHandlers(false);
        logger.setLevel(Level.ALL);

        onNewDay();

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                onNewDay();
            }
        }, 86400000 - LocalTime.now().toSecondOfDay() * 1000L, 86400000);
    }

    private static String getFileName() {
        return String.format("logs/smpessentials/log-%s.txt", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
    }

    private static void clearOldFiles() {
        File folderFile = new File("logs/smpessentials");
        if (!folderFile.exists() || !folderFile.isDirectory()) {
            return;
        }

        File[] files = folderFile.listFiles();

        if (files == null) {
            return;
        }

        for (File file : files) {
            try {
                BasicFileAttributes attributes = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
                // 604800 - 7 days
                if (Duration.between(attributes.creationTime().toInstant(), Instant.now()).getSeconds() > 604800L) {
                    if (file.delete()) {
                        SMPEssentials.LOGGER.info("Deleting log file " + file.getName() + " because it was older than 7 days");
                    } else {
                        SMPEssentials.LOGGER.error("Failed to delete file " + file.getAbsolutePath());
                    }
                }
            } catch (IOException e) {
                SMPEssentials.LOGGER.error("Failed to read attributes for file " + file.getAbsolutePath());
            }
        }
    }

    private static void onNewDay() {
        try {
            if (loggerHandler != null) {
                logger.removeHandler(loggerHandler);
            }
            clearOldFiles();
            String fileName = getFileName();
            File file = new File(fileName);

            if (!file.getParentFile().exists() && !file.getParentFile().mkdirs()) {
                SMPEssentials.LOGGER.warn("Failed to create necessary directories for the log file");
            }

            loggerHandler = new FileHandler(fileName);
            loggerHandler.setFormatter(new SimplerFormatter());
            logger.addHandler(loggerHandler);
        } catch (IOException e) {
            SMPEssentials.LOGGER.error("Error while initializing log file", e);
        }
    }

    public static void log(String info) {
        logger.info(info);
    }

    public static class SimplerFormatter extends Formatter {
        private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        @Override
        public String format(LogRecord record) {
            return String.format("[%s] %s\n", LocalDateTime.ofInstant(GregorianCalendar.getInstance().toInstant() , ZoneId.systemDefault()).format(formatter), record.getMessage());
        }
    }
}