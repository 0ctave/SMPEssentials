package me.khajiitos.smpessentials.manager;

import me.khajiitos.smpessentials.SMPEssentials;
import me.khajiitos.smpessentials.data.SMPData;

import java.util.UUID;

public class StaffManager {
    public static boolean isStaff(UUID uuid) {
        return SMPEssentials.getData().getOrCreate(uuid).staff;
    }

    public static void addStaff(UUID uuid) {
        SMPData data = SMPEssentials.getData();
        data.getOrCreate(uuid).staff = true;
        data.setDirty();

        PlayerSynchedDataManager.setStaffIfOnline(uuid, true);
    }

    public static void removeStaff(UUID uuid) {
        SMPData data = SMPEssentials.getData();
        data.getOrCreate(uuid).staff = false;
        data.setDirty();

        PlayerSynchedDataManager.setStaffIfOnline(uuid, false);
    }
}