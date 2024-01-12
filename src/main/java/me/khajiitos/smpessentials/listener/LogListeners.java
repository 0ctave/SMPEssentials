package me.khajiitos.smpessentials.listener;

import me.khajiitos.smpessentials.config.Config;
import me.khajiitos.smpessentials.manager.LogManager;
import me.khajiitos.smpessentials.manager.PVPManager;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.common.UsernameCache;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class LogListeners {

    @SubscribeEvent
    public void onDeath(LivingDeathEvent e) {
        if (e.getEntity() instanceof TameableEntity) {
            TameableEntity tamable = (TameableEntity) e.getEntity();
            if (Config.logTamedKills && tamable.getOwnerUUID() != null) {
                String owner = UsernameCache.getLastKnownUsername(tamable.getOwnerUUID());
                if (owner == null) {
                    owner = tamable.getOwnerUUID().toString();
                }

                String mobType = tamable.getType().toString();

                String attacker;
                if (e.getSource().getEntity() != null) {
                    if (e.getSource().getEntity() instanceof PlayerEntity) {
                        PlayerEntity player = (PlayerEntity) e.getSource().getEntity();
                        attacker = player.getScoreboardName();
                    } else {
                        attacker = e.getSource().getEntity().getType().toString();
                    }
                } else {
                    attacker = "damage: " + e.getSource().getMsgId();
                }

                LogManager.log(String.format("Tamed %s owned by %s died, killed by %s", mobType, owner, attacker));
            }
        } else if (e.getEntity() instanceof ServerPlayerEntity && e.getSource().getEntity() instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) e.getEntity();
            ServerPlayerEntity attacker = (ServerPlayerEntity) e.getSource().getEntity();
            if (Config.logPlayerKills) {
                LogManager.log(String.format("%s (PvP %s) died, killed by %s (PvP %s)",
                        player.getScoreboardName(),
                        PVPManager.hasPvpEnabled(player) ? "ON" : "OFF",
                        attacker.getScoreboardName(),
                        PVPManager.hasPvpEnabled(player) ? "ON" : "OFF")
                );
            }
        }
    }

    @SubscribeEvent
    public void onLogin(PlayerEvent.PlayerLoggedInEvent e) {
        if (Config.logLoginLogout && e.getPlayer() instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) e.getPlayer();
            Vector3d pos = player.position();
            LogManager.log(String.format("%s logged in at coordinates: %d, %d, %d (%s)", player.getScoreboardName(), (int)Math.floor(pos.x), (int)Math.floor(pos.y), (int)Math.floor(pos.z), player.level.dimension().location()));
        }
    }

    @SubscribeEvent
    public void onLogin(PlayerEvent.PlayerLoggedOutEvent e) {
        if (Config.logLoginLogout && e.getPlayer() instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) e.getPlayer();
            Vector3d pos = player.position();
            LogManager.log(String.format("%s logged out at coordinates: %d, %d, %d (%s)", player.getScoreboardName(), (int)Math.floor(pos.x), (int)Math.floor(pos.y), (int)Math.floor(pos.z), player.level.dimension().location()));
        }
    }
}
