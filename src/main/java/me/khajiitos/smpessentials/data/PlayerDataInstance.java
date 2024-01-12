package me.khajiitos.smpessentials.data;

import me.khajiitos.smpessentials.Utils;
import net.minecraft.nbt.CompoundNBT;

import java.util.UUID;

public class PlayerDataInstance {

    public static class Punishment {

        private final String reason;
        private final long activeUntil;

        public Punishment(String reason, long activeUntil) {
            this.reason = reason;
            this.activeUntil = activeUntil;
        }

        public CompoundNBT save() {
            CompoundNBT tag = new CompoundNBT();

            if (this.reason != null) {
                tag.putString("reason", this.reason);
            }

            tag.putLong("activeUntil", this.activeUntil);
            return tag;
        }

        public static Punishment load(CompoundNBT tag) {
            return new Punishment(tag.getString("reason"), tag.getLong("activeUntil"));
        }

        public String getTimeLeftStr() {
            return Utils.timeToStr(activeUntil() - System.currentTimeMillis());
        }

        public long activeUntil() {
            return this.activeUntil;
        }

        public String reason() {
            return this.reason;
        }

    }

    public String lastUsername;
    public Punishment activeBan;
    public Punishment activeMute;
    public boolean staff = false;
    public boolean pvp = false;
    public int acceptedRulesHash = 0;

    public int spawnProtectionTicksLeft = 0;
    public int noobProtectionTicksLeft = 0;
    public UUID team = null;
    public int kills, deaths;

    public CompoundNBT save() {
        CompoundNBT tag = new CompoundNBT();

        tag.putBoolean("staff", this.staff);
        tag.putBoolean("pvp", this.pvp);
        tag.putInt("acceptedRulesHash", this.acceptedRulesHash);

        if (lastUsername != null) {
            tag.putString("lastUsername", lastUsername);
        }

        if (activeBan != null) {
            tag.put("activeBan", activeBan.save());
        }

        if (activeMute != null) {
            tag.put("activeMute", activeMute.save());
        }

        if (team != null) {
            tag.putUUID("team", team);
        }

        if (spawnProtectionTicksLeft > 0) {
            tag.putInt("spawnProtectionTicksLeft", this.spawnProtectionTicksLeft);
        }

        if (noobProtectionTicksLeft > 0) {
            tag.putInt("noobProtectionTicksLeft", this.noobProtectionTicksLeft);
        }

        tag.putInt("kills", this.kills);
        tag.putInt("deaths", this.deaths);

        return tag;
    }

    public static PlayerDataInstance load(CompoundNBT nbt) {
        PlayerDataInstance data = new PlayerDataInstance();
        data.staff = nbt.getBoolean("staff");
        data.pvp = nbt.getBoolean("pvp");
        data.acceptedRulesHash = nbt.getInt("acceptedRulesHash");
        data.lastUsername = nbt.getString("lastUsername");
        if (nbt.contains("activeBan")) {
            data.activeBan = Punishment.load(nbt.getCompound("activeBan"));
        }
        if (nbt.contains("activeMute")) {
            data.activeMute = Punishment.load(nbt.getCompound("activeMute"));
        }

        if (nbt.contains("team")) {
            data.team = nbt.getUUID("team");
        }

        if (nbt.contains("spawnProtectionTicksLeft")) {
            data.spawnProtectionTicksLeft = nbt.getInt("spawnProtectionTicksLeft");
        }
        if (nbt.contains("noobProtectionTicksLeft")) {
            data.noobProtectionTicksLeft = nbt.getInt("noobProtectionTicksLeft");
        }

        data.kills = nbt.getInt("kills");
        data.deaths = nbt.getInt("deaths");
        return data;
    }
}
