package me.khajiitos.smpessentials.listener;

import me.khajiitos.smpessentials.Packets;
import me.khajiitos.smpessentials.SMPEssentials;
import me.khajiitos.smpessentials.config.Config;
import me.khajiitos.smpessentials.data.PlayerDataInstance;
import me.khajiitos.smpessentials.manager.PVPManager;
import me.khajiitos.smpessentials.manager.PunishmentManager;
import me.khajiitos.smpessentials.packet.RulesPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.projectile.PotionEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.potion.EffectType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.HashMap;
import java.util.List;

public class EventListeners {

    @SubscribeEvent
    public void onTick(TickEvent.ServerTickEvent e) {
        PVPManager.tick();

        for (ServerPlayerEntity player : SMPEssentials.server.getPlayerList().getPlayers()) {
            PlayerDataInstance dataInstance = SMPEssentials.getData().getOrCreate(player.getUUID());

            if (dataInstance.spawnProtectionTicksLeft > 0) {
                dataInstance.spawnProtectionTicksLeft--;
            }

            if (dataInstance.noobProtectionTicksLeft > 0) {
                dataInstance.noobProtectionTicksLeft--;
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onPvp(AttackEntityEvent e) {
        if (!e.isCanceled()) {
            Entity attackerEntity = e.getEntity();
            Entity targetEntity = e.getTarget();

            if (attackerEntity instanceof ServerPlayerEntity) {
                ServerPlayerEntity attacker = (ServerPlayerEntity) attackerEntity;
                if (targetEntity instanceof ServerPlayerEntity) {
                    ServerPlayerEntity target = (ServerPlayerEntity) targetEntity;
                    if (PVPManager.canAttackEachOther(attacker, target)) {
                        PVPManager.processAttack(attacker, target);
                    } else {
                        e.setCanceled(true);
                    }
                } else if (targetEntity instanceof TameableEntity) {
                    TameableEntity tamable = (TameableEntity) targetEntity;
                    if (tamable.getOwnerUUID() != null && !PVPManager.canAttackEachOther(tamable.getOwnerUUID(), attacker.getUUID())) {
                        e.setCanceled(true);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onDeath(LivingDeathEvent e) {
        if (e.getEntity() instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) e.getEntity();
            PVPManager.removeFromCombat(player);

            SMPEssentials.getData().getOrCreate(player.getUUID()).deaths++;
            if (e.getSource().getEntity() instanceof ServerPlayerEntity) {
                ServerPlayerEntity killer = (ServerPlayerEntity) e.getSource().getEntity();
                SMPEssentials.getData().getOrCreate(killer.getUUID()).kills++;
            }
            SMPEssentials.getData().setDirty();
        }
    }

    @SubscribeEvent
    public void onDisconnect(PlayerEvent.PlayerLoggedOutEvent e) {
        if (e.getPlayer() instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) e.getPlayer();
            if (PVPManager.isInCombat(player)) {
                player.hurt(DamageSource.GENERIC, Float.MAX_VALUE);
            }
        }
    }

    @SubscribeEvent
    public void onPlace(BlockEvent.EntityPlaceEvent e) {
        if (e.getEntity() instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) e.getEntity();
            MinecraftServer server = player.getServer();
            if (server != null) {
                for (ServerPlayerEntity otherPlayer : player.getServer().getPlayerList().getPlayers()) {
                    if (otherPlayer == player) {
                        continue;
                    }

                    if (PVPManager.hasPvpEnabled(player) || PVPManager.hasPvpEnabled(otherPlayer)) {
                        if (e.getPos().distSqr(otherPlayer.blockPosition()) < 3.0) {
                            e.setCanceled(true);
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onBucketEmpty(FillBucketEvent e) {
        if (e.getEntity() instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) e.getEntity();
            MinecraftServer server = player.getServer();
            if (server != null) {
                for (ServerPlayerEntity otherPlayer : player.getServer().getPlayerList().getPlayers()) {
                    if (otherPlayer == player) {
                        continue;
                    }

                    if (PVPManager.hasPvpEnabled(player) || PVPManager.hasPvpEnabled(otherPlayer)) {
                        if (e.getTarget() instanceof BlockRayTraceResult) {
                            BlockRayTraceResult blockHitResult = (BlockRayTraceResult) e.getTarget();
                            if (blockHitResult.getBlockPos().relative(blockHitResult.getDirection()).distSqr(otherPlayer.blockPosition()) < 3.0) {
                                e.setCanceled(true);
                            }
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onExplosion(ExplosionEvent.Detonate e) {
        if (e.getExplosion().getExploder() instanceof ServerPlayerEntity ) {
            ServerPlayerEntity player = (ServerPlayerEntity) e.getExplosion().getExploder();
            e.getAffectedEntities().forEach(targetPlayer -> {
                if (targetPlayer instanceof ServerPlayerEntity) {
                    ServerPlayerEntity target = (ServerPlayerEntity) targetPlayer;
                    if (!PVPManager.hasPvpEnabled(player) || !PVPManager.hasPvpEnabled(target)) {
                        e.getAffectedEntities().remove(targetPlayer);
                    }
                }
            });
        }
    }

    @SubscribeEvent
    public void onTeleport(EntityTeleportEvent e) {
        if (e.getEntity() instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) e.getEntity();
            if (PVPManager.isInCombat(player)) {
                e.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onBreak(BlockEvent.BreakEvent e) {
        if (e.getPlayer() instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) e.getPlayer();
            MinecraftServer server = player.getServer();
            if (server != null) {
                for (ServerPlayerEntity otherPlayer : player.getServer().getPlayerList().getPlayers()) {
                    if (otherPlayer == player) {
                        continue;
                    }

                    if (otherPlayer.getLevel() != player.getLevel()) {
                        continue;
                    }

                    if (PVPManager.hasPvpEnabled(player) && PVPManager.hasPvpEnabled(otherPlayer)) {
                        continue;
                    }

                    BlockPos blockBelow = otherPlayer.blockPosition().below();

                    if (e.getPos().equals(blockBelow) && !player.getLevel().getBlockState(blockBelow.below()).entityCanStandOn(player.getLevel(), blockBelow.below(), player)) {
                        e.setCanceled(true);
                        return;
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onJoin(PlayerEvent.PlayerLoggedInEvent e) {
        if (e.getPlayer() instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) e.getPlayer();
            if (SMPEssentials.getData().get(player.getUUID()) == null) {
                SMPEssentials.getData().getOrCreate(player.getUUID()).spawnProtectionTicksLeft = Config.noobProtectionTime;
            }

            SMPEssentials.getData().getOrCreate(player.getUUID()).lastUsername = player.getScoreboardName();
            SMPEssentials.getData().getOrCreate(player.getUUID()).spawnProtectionTicksLeft = Config.spawnProtectionTime;

            PlayerDataInstance.Punishment ban = PunishmentManager.getBan(player);
            if (ban != null) {
                player.connection.disconnect(new StringTextComponent("§cYou have been temporarily banned by Staff for: §4" + ban.reason() + "\n§cTime left: §4" + ban.getTimeLeftStr()));
            } else if (SMPEssentials.getData().getOrCreate(player.getUUID()).acceptedRulesHash != Config.rules.hashCode()) {
                Packets.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new RulesPacket(Config.rules));
            }
        }
    }

    @SubscribeEvent
    public void onChat(ServerChatEvent e) {
        PlayerDataInstance.Punishment mute = PunishmentManager.getMute(e.getPlayer());
        if (mute != null) {
            e.getPlayer().sendMessage(new StringTextComponent("§cYou have been muted by Staff for: §4" + mute.reason()), e.getPlayer().getUUID());
            e.getPlayer().sendMessage(new StringTextComponent("§cTime left: §4" + mute.getTimeLeftStr()), e.getPlayer().getUUID());
            e.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onDamage(LivingAttackEvent e) {
        if (e.getEntity() instanceof ServerPlayerEntity) {
            PlayerDataInstance dataInstance = SMPEssentials.getData().getOrCreate(e.getEntity().getUUID());

            if (dataInstance.spawnProtectionTicksLeft > 0) {
                e.setCanceled(true);
                return;
            } else if (dataInstance.noobProtectionTicksLeft > 0 && e.getSource() != DamageSource.FALL) {
                e.setCanceled(true);
                return;
            }
        }

        if (e.getEntity() instanceof ServerPlayerEntity || e.getEntity() instanceof TameableEntity) {
            if (e.getSource().isProjectile()) {
                if (e.getSource().getDirectEntity() != null && ((ProjectileEntity) e.getSource().getDirectEntity()).getOwner() instanceof ServerPlayerEntity) {
                    ServerPlayerEntity attacker = (ServerPlayerEntity) ((ProjectileEntity) e.getSource().getDirectEntity()).getOwner();
                    if (e.getEntity() instanceof ServerPlayerEntity) {
                        ServerPlayerEntity target = (ServerPlayerEntity) e.getEntity();
                        if (PVPManager.canAttackEachOther(attacker, target)) {
                            PVPManager.processAttack(attacker, target);
                        } else {
                            e.setCanceled(true);
                        }
                    } else if(e.getEntity() instanceof TameableEntity) {
                        TameableEntity tamable = (TameableEntity) e.getEntity();
                        if (tamable.getOwnerUUID() != null && !PVPManager.canAttackEachOther(tamable.getOwnerUUID(), attacker.getUUID())) {
                            e.setCanceled(true);
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onDimensionChange(PlayerEvent.PlayerChangedDimensionEvent e) {
        SMPEssentials.getData().getOrCreate(e.getPlayer().getUUID()).spawnProtectionTicksLeft = Config.spawnProtectionTime;
    }

    @SubscribeEvent
    public void onRespawn(PlayerEvent.PlayerRespawnEvent e) {
        SMPEssentials.getData().getOrCreate(e.getPlayer().getUUID()).spawnProtectionTicksLeft = Config.spawnProtectionTime;
    }


    private HashMap<Vector3d, ServerPlayerEntity> potions = new HashMap<>();

    @SubscribeEvent
    public void onProjectileImpact(ProjectileImpactEvent e) {
        if (e.getEntity() instanceof PotionEntity && e.getRayTraceResult().getType() != RayTraceResult.Type.MISS) {
            PotionEntity potion = (PotionEntity) e.getEntity();
            if (PotionUtils.getPotion(potion.getItem()).getEffects().stream().anyMatch(effectInstance -> effectInstance.getEffect().getCategory() == EffectType.HARMFUL)) {
                if (potion.getOwner() instanceof ServerPlayerEntity) {
                    ServerPlayerEntity attacker = (ServerPlayerEntity) potion.getOwner();

                    AxisAlignedBB axisalignedbb = potion.getBoundingBox().inflate(4.0D, 2.0D, 4.0D);
                    List<LivingEntity> list = potion.level.getEntitiesOfClass(LivingEntity.class, axisalignedbb);
                    if (!list.isEmpty()) {
                        for (LivingEntity livingentity : list) {
                            if (livingentity instanceof ServerPlayerEntity && livingentity != attacker) {
                                ServerPlayerEntity target = (ServerPlayerEntity) livingentity;
                                if (PVPManager.canAttackEachOther(attacker, target)) {
                                    PVPManager.processAttack(attacker, target);
                                } else {
                                    potion.remove();
                                    e.setCanceled(true);
                                }
                            } else if (livingentity instanceof TameableEntity) {
                                TameableEntity tamable = (TameableEntity) livingentity;
                                if (tamable.getOwnerUUID() != null && !PVPManager.canAttackEachOther(tamable.getOwnerUUID(), attacker.getUUID())) {
                                    e.setCanceled(true);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}
