package me.khajiitos.smpessentials.listener;

import me.khajiitos.smpessentials.SMPEssentials;
import me.khajiitos.smpessentials.data.PlayerDataInstance;
import me.khajiitos.smpessentials.manager.PVPManager;
import me.khajiitos.smpessentials.manager.TeamManager;
import net.blay09.mods.hardcorerevival.HardcoreRevivalManager;
import net.blay09.mods.hardcorerevival.api.PlayerKnockedOutEvent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashMap;

public class KnockoutListener {

    public HashMap<PlayerEntity, DamageSource> knockoutSource = new HashMap<>();


    @SubscribeEvent
    public void onKnockout(PlayerKnockedOutEvent e) {
        if (e.getPlayer() instanceof ServerPlayerEntity) {
            if (e.getSource().getEntity() instanceof ServerPlayerEntity) {
                knockoutSource.put(e.getPlayer(), e.getSource());
            } else {
                knockoutSource.remove(e.getPlayer());
            }
        }

    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onDeath(LivingDeathEvent e) {
        if (e.getEntity() instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) e.getEntity();
            PVPManager.removeFromCombat(player);

            if (e.getSource() == HardcoreRevivalManager.notRescuedInTime) {
                if (knockoutSource.containsKey(player)) {

                    ServerPlayerEntity killer = (ServerPlayerEntity) knockoutSource.get(player).getEntity();
                    PlayerDataInstance playerData = SMPEssentials.getData().getOrCreate(killer.getUUID());
                    playerData.kills++;

                    if (TeamManager.isAtWar(player, killer)) {
                        TeamManager.getWar(player, killer).addKill(TeamManager.getTeamUuid(TeamManager.getTeam(killer)));
                    }
                }
            }
            SMPEssentials.getData().setDirty();
        }
    }
}
