package me.khajiitos.smpessentials.manager;

import me.khajiitos.smpessentials.CustomPlayerSynchedData;
import me.khajiitos.smpessentials.SMPEssentials;
import net.minecraft.entity.player.ServerPlayerEntity;

import java.util.UUID;

public class PlayerSynchedDataManager {

    private static ServerPlayerEntity getPlayer(UUID uuid) {
        return SMPEssentials.server.getPlayerList().getPlayer(uuid);
    }

    public static void refreshDisplayName(ServerPlayerEntity player) {
        player.refreshDisplayName();
    }

    public static void setStaffIfOnline(UUID uuid, boolean staff) {
        ServerPlayerEntity player = getPlayer(uuid);

        if (player != null) {
            CustomPlayerSynchedData data = (CustomPlayerSynchedData) player;
            data.setStaff(staff);
        }
    }

    public static void setPvpOnIfOnline(UUID uuid, boolean pvpOn) {
        ServerPlayerEntity player = getPlayer(uuid);

        if (player != null) {
            CustomPlayerSynchedData data = (CustomPlayerSynchedData) player;
            data.setPvpOn(pvpOn);
        }
    }

    public static void setTeamTagIfOnline(UUID uuid, String teamTag) {
        ServerPlayerEntity player = getPlayer(uuid);

        if (player != null) {
            CustomPlayerSynchedData data = (CustomPlayerSynchedData) player;
            data.setTeamTag(teamTag);
        }
    }
}
