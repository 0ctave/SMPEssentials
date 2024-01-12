package me.khajiitos.smpessentials.manager;

import me.khajiitos.smpessentials.SMPEssentials;
import me.khajiitos.smpessentials.data.PlayerDataInstance;
import me.khajiitos.smpessentials.data.SMPData;
import net.minecraft.network.play.server.STitlePacket;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.StringTextComponent;

import net.minecraft.entity.player.ServerPlayerEntity;

import java.util.UUID;

public class PunishmentManager {
    public static PlayerDataInstance.Punishment getBan(ServerPlayerEntity player) {
        SMPData data = SMPEssentials.getData();
        PlayerDataInstance playerData = data.get(player.getUUID());
        if (playerData.activeBan != null) {
            if (playerData.activeBan.activeUntil() >= System.currentTimeMillis()) {
                return playerData.activeBan;
            } else {
                playerData.activeBan = null;
            }
        }
        return null;
    }

    public static PlayerDataInstance.Punishment getMute(ServerPlayerEntity player) {
        SMPData data = SMPEssentials.getData();
        PlayerDataInstance playerData = data.get(player.getUUID());
        if (playerData.activeMute != null) {
            if (playerData.activeMute.activeUntil() > System.currentTimeMillis()) {
                return playerData.activeMute;
            } else {
                playerData.activeMute = null;
            }
        }
        return null;
    }

    public static void mutePlayer(ServerPlayerEntity player, ServerPlayerEntity by, long length, String reason) {
        SMPData data = SMPEssentials.getData();
        PlayerDataInstance playerData = data.getOrCreate(player.getUUID());
        playerData.activeMute = new PlayerDataInstance.Punishment(reason, System.currentTimeMillis() + length);

        CompoundNBT logTag = new CompoundNBT();
        logTag.putString("type", "mute");
        logTag.putString("reason", reason);
        logTag.putLong("at", System.currentTimeMillis());
        logTag.putLong("length", length);
        logTag.putString("by", by != null ? by.getUUID().toString() : "console");
        logTag.putString("for", player.getUUID().toString());
        data.punishmentLog.add(logTag);

        data.setDirty();
    }

    public static void banPlayer(ServerPlayerEntity player, ServerPlayerEntity by, long length, String reason) {
        SMPData data = SMPEssentials.getData();
        PlayerDataInstance playerData = data.getOrCreate(player.getUUID());
        playerData.activeBan = new PlayerDataInstance.Punishment(reason, System.currentTimeMillis() + length);

        CompoundNBT logTag = new CompoundNBT();
        logTag.putString("type", "ban");
        logTag.putString("reason", reason);
        logTag.putLong("at", System.currentTimeMillis());
        logTag.putLong("length", length);
        logTag.putString("by", by != null ? by.getUUID().toString() : "console");
        logTag.putString("for", player.getUUID().toString());
        data.punishmentLog.add(logTag);

        data.setDirty();
        player.connection.disconnect(new StringTextComponent("§cYou have been temporarily banned by Staff for: §4" + playerData.activeBan.reason() + "\n§cTime left: §4" + playerData.activeBan.getTimeLeftStr()));
    }

    public static boolean unbanPlayer(ServerPlayerEntity player) {
        SMPData data = SMPEssentials.getData();
        PlayerDataInstance playerData = data.get(player.getUUID());

        if (playerData == null || playerData.activeBan == null) {
            return false;
        }

        playerData.activeBan = null;
        data.setDirty();
        return true;
    }

    public static boolean unbanPlayer(UUID playerUUID) {
        SMPData data = SMPEssentials.getData();
        PlayerDataInstance playerData = data.get(playerUUID);

        if (playerData == null || playerData.activeBan == null) {
            return false;
        }

        playerData.activeBan = null;
        data.setDirty();
        return true;
    }

    public static boolean unmutePlayer(ServerPlayerEntity player) {
        SMPData data = SMPEssentials.getData();
        PlayerDataInstance playerData = data.get(player.getUUID());

        if (playerData == null || playerData.activeMute == null) {
            return false;
        }

        playerData.activeMute = null;
        data.setDirty();
        return true;
    }

    public static void warnPlayer(ServerPlayerEntity player, ServerPlayerEntity by, String reason) {
        SMPData data = SMPEssentials.getData();

        IFormattableTextComponent header = new StringTextComponent("You have been warned!").withStyle(TextFormatting.RED);
        IFormattableTextComponent below = new StringTextComponent("Reason: ").withStyle(TextFormatting.GOLD).append(new StringTextComponent(reason).withStyle(TextFormatting.YELLOW));
        player.sendMessage(header, player.getUUID());
        player.sendMessage(below, player.getUUID());

        player.connection.send(new STitlePacket(STitlePacket.Type.TITLE, header));
        player.connection.send(new STitlePacket(STitlePacket.Type.SUBTITLE, below));
        player.playSound(SoundEvents.ANVIL_PLACE, 0.5f, 0.5f);

        CompoundNBT logTag = new CompoundNBT();
        logTag.putString("type", "warn");
        logTag.putString("reason", reason);
        logTag.putLong("at", System.currentTimeMillis());
        logTag.putString("by", by != null ? by.getUUID().toString() : "console");
        logTag.putString("for", player.getUUID().toString());
        data.punishmentLog.add(logTag);
        data.setDirty();
    }
}