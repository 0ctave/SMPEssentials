package me.khajiitos.smpessentials.manager;

import me.khajiitos.smpessentials.SMPEssentials;
import me.khajiitos.smpessentials.data.PlayerDataInstance;
import me.khajiitos.smpessentials.data.Team;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.entity.player.ServerPlayerEntity;

import java.util.*;

public class PVPManager {
    private static final HashMap<UUID, Integer> combatTicksLeft = new HashMap<>();
    private static final HashMap<UUID, Long> lastSwitchedPvpStatus = new HashMap<>();

    public static boolean isInCombat(ServerPlayerEntity serverPlayer) {
        return combatTicksLeft.getOrDefault(serverPlayer.getUUID(), 0) > 0;
    }

    public static boolean hasPvpEnabled(ServerPlayerEntity serverPlayer) {
        return hasPvpEnabled(serverPlayer.getUUID());
    }

    public static boolean canAttackEachOther(UUID player1, UUID player2) {
        Team team1 = TeamManager.getTeam(player1);
        Team team2 = TeamManager.getTeam(player2);

        if (team1 == null || team2 == null) {
            return hasPvpEnabled(player1) && hasPvpEnabled(player2);
        }

        if (team1 == team2) {
            return hasPvpEnabled(player1) && hasPvpEnabled(player2) && team1.friendlyFire;
        }

        if (team1.wars.containsKey(TeamManager.getTeamUuid(team2)) || team2.wars.containsKey(TeamManager.getTeamUuid(team1))) {
            return true;
        }

        for (UUID ally : team1.allies) {
            if (team2.wars.containsKey(ally)) {
                return true;
            }
        }

        for (UUID ally : team2.allies) {
            if (team1.wars.containsKey(ally)) {
                return true;
            }
        }
        return hasPvpEnabled(player1) && hasPvpEnabled(player2);
    }

    public static boolean canAttackEachOther(ServerPlayerEntity player1, ServerPlayerEntity player2) {
        return canAttackEachOther(player1.getUUID(), player2.getUUID());
    }


    public static void processAttack(ServerPlayerEntity attacker, ServerPlayerEntity target) {
        PVPManager.setInCombat(attacker);
        PVPManager.setInCombat(target);

        PlayerDataInstance attackerData = SMPEssentials.getData().getOrCreate(attacker.getUUID());
        attackerData.spawnProtectionTicksLeft = 0;
        attackerData.noobProtectionTicksLeft = 0;
        SMPEssentials.getData().setDirty();

        if (!hasPvpEnabled(attacker) || !hasPvpEnabled(target)) {
            SMPEssentials.LOGGER.warn(attacker.getDisplayName().getString() + " attacked " + target.getDisplayName().getString() + " but it should not have been able to");
        }
    }

    public static boolean hasPvpEnabled(UUID playerUUID) {
        return SMPEssentials.getData().getOrCreate(playerUUID).pvp;
    }

    public static boolean canSwitchPvpStatus(ServerPlayerEntity serverPlayer) {
        return canSwitchPvpStatusIn(serverPlayer) <= 0;
    }

    public static long canSwitchPvpStatusIn(ServerPlayerEntity serverPlayer) {
        return Math.max(-1, 30000L - (System.currentTimeMillis() - lastSwitchedPvpStatus.getOrDefault(serverPlayer.getUUID(), 0L)));
    }

    public static void setPvpEnabled(ServerPlayerEntity serverPlayer, boolean enabled) {
        SMPEssentials.getData().getOrCreate(serverPlayer.getUUID()).pvp = enabled;
        SMPEssentials.getData().setDirty();
        lastSwitchedPvpStatus.put(serverPlayer.getUUID(), System.currentTimeMillis());
        PlayerSynchedDataManager.setPvpOnIfOnline(serverPlayer.getUUID(), enabled);
    }

    public static void setInCombat(ServerPlayerEntity serverPlayer) {
        if (!isInCombat(serverPlayer)) {
            serverPlayer.sendMessage(new StringTextComponent("You are now in combat! Do not log off or you will be killed.").withStyle(TextFormatting.RED), serverPlayer.getUUID());
        }
        combatTicksLeft.put(serverPlayer.getUUID(), 600);
    }

    public static void removeFromCombat(ServerPlayerEntity serverPlayer) {
        combatTicksLeft.remove(serverPlayer.getUUID());
    }

    public static void tick() {
        List<UUID> uuidsToRemove = new ArrayList<>();
        for (Map.Entry<UUID, Integer> entry : combatTicksLeft.entrySet()) {
            int newTicks = entry.getValue() - 1;

            if (newTicks <= 0) {
                uuidsToRemove.add(entry.getKey());
                ServerPlayerEntity player = SMPEssentials.server.getPlayerList().getPlayer(entry.getKey());
                if (player != null) {
                    player.sendMessage(new StringTextComponent("You are no longer in combat.").withStyle(TextFormatting.RED), entry.getKey());
                }
            } else {
                entry.setValue(newTicks);
            }
        }

        uuidsToRemove.forEach(combatTicksLeft::remove);
    }
}
