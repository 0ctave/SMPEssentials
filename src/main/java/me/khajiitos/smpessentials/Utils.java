package me.khajiitos.smpessentials;

import net.minecraftforge.common.UsernameCache;

import java.util.Map;
import java.util.UUID;

public class Utils {

    public static String timeToStr(long millis) {
        long secondsTotal = millis / 1000;
        long seconds = secondsTotal % 60;
        long minutes = (secondsTotal / 60) % 60;
        long hours = (secondsTotal / 60 / 60) % 24;
        long days = secondsTotal / 60 / 60 / 24;

        StringBuilder builder = new StringBuilder();

        if (days != 0) {
            builder.append(days).append("d ");
        }

        if (hours != 0) {
            builder.append(hours).append("h ");
        }

        if (minutes != 0) {
            builder.append(minutes).append("m ");
        }

        builder.append(seconds).append("s");
        return builder.toString();
    }

    public static UUID getPlayerUUID(String playerName) {
        for (Map.Entry<UUID, String> entry : UsernameCache.getMap().entrySet()) {
            if (entry.getValue().equals(playerName)) {
                return entry.getKey();
            }
        }

        return null;
    }
}
