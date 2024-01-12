package me.khajiitos.smpessentials.listener;

import com.google.gson.JsonElement;
import me.khajiitos.smpessentials.config.Config;
import me.khajiitos.smpessentials.config.ConfigEntry;
import me.khajiitos.smpessentials.manager.PVPManager;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.entity.player.ServerPlayerEntity;

import net.minecraft.world.World;
import net.minecraftforge.event.entity.item.ItemEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.commons.lang3.reflect.TypeUtils;

import javax.annotation.Nullable;

public class AntiGriefListeners {
    public static ServerPlayerEntity gorgonHeadUser = null;
    public static ServerPlayerEntity cockatriceScepterUser = null;

    private @Nullable Class<?> getDragonBaseEntityClass() {
        try {
            return Class.forName("com.github.alexthe666.iceandfire.entity.EntityDragonBase");
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent.EntityInteract e) {
        Item item = e.getItemStack().getItem();
        ResourceLocation registryName = item.getRegistryName();

        if (registryName == null) {
            return;
        }

        if (e.getWorld().dimension() == World.NETHER) {
            Class<?> dragonClass = getDragonBaseEntityClass();
            if (dragonClass != null && TypeUtils.isAssignable(e.getTarget().getClass(), dragonClass)) {
                if (!e.getWorld().isClientSide() && e.getHand() == Hand.MAIN_HAND) {
                    e.getEntity().sendMessage(new StringTextComponent("You are not allowed to use dragons in the nether!").withStyle(TextFormatting.RED), e.getEntity().getUUID());
                }
                e.setCancellationResult(ActionResultType.FAIL);
                return;
            }
        }

        if (registryName.toString().equals("iceandfire:chain") || registryName.toString().equals("iceandfire:chain_sticky")) {
            if (Config.preventChains && e.getTarget() instanceof PlayerEntity) {
                e.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onInteractAnything(PlayerInteractEvent e) {
        if (e.getPlayer() instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) e.getPlayer();
            if (PVPManager.isInCombat(serverPlayer)) {
                ResourceLocation resLoc = e.getItemStack().getItem().getRegistryName();
                if (resLoc == null) {
                    return;
                }
                for (JsonElement element : Config.preventedItemsInCombat) {
                    if (element.getAsString().equals(resLoc.toString())) {
                        e.setCanceled(true);
                        break;
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void startUsingItem(LivingEntityUseItemEvent.Start e) {
        Item item = e.getItem().getItem();
        ResourceLocation registryName = item.getRegistryName();

        if (registryName == null) {
            return;
        }

        if (e.getEntity() instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) e.getEntity();
            if (PVPManager.isInCombat(player))
            for (JsonElement element : Config.preventedItemsInCombat) {
                if (element.getAsString().equals(registryName.toString())) {
                    e.setCanceled(true);
                    break;
                }
            }
        }
    }

    @SubscribeEvent
    public void onInteractBlock(PlayerInteractEvent.RightClickBlock e) {
        Item item = e.getItemStack().getItem();
        ResourceLocation registryName = item.getRegistryName();

        if (registryName == null) {
            return;
        }

        if (e.getPlayer() instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) e.getPlayer();
            if (PVPManager.isInCombat(player)) {
                BlockState blockState = e.getWorld().getBlockState(e.getPos());
                ResourceLocation resLoc = blockState.getBlock().getRegistryName();

                if (resLoc == null) {
                    return;
                }

                for (JsonElement element : Config.preventedBlocksInCombat) {
                    if (element.getAsString().equals(resLoc.toString())) {
                        e.setCanceled(true);
                        return;
                    }
                }
            }
        }

        if (registryName.toString().equals("iceandfire:dragon_horn")) {
            if (Config.preventFlyingDragonsInNether && e.getEntity().level.dimension() == World.NETHER && e.getFace() == Direction.UP) {
                e.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onReleaseUsing(LivingEntityUseItemEvent.Stop e) {
        Item item = e.getItem().getItem();
        ResourceLocation registryName = item.getRegistryName();

        if (registryName == null) {
            return;
        }

        if (e.getEntity() instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) e.getEntity();
            if (Config.preventGorgonHead && registryName.toString().equals("iceandfire:gorgon_head")) {
                gorgonHeadUser = player;
            }
        }
    }

    @SubscribeEvent
    public void onUsingTick(LivingEntityUseItemEvent.Tick e) {
        Item item = e.getItem().getItem();
        ResourceLocation registryName = item.getRegistryName();

        if (registryName == null) {
            return;
        }

        if (e.getEntity() instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) e.getEntity();
            if (registryName.toString().equals("iceandfire:cockatrice_scepter")) {
                cockatriceScepterUser = player;
            }
        }

    }
}
