package me.khajiitos.smpessentials.packet.teammanager.c2s.handler;

import me.khajiitos.smpessentials.Packets;
import me.khajiitos.smpessentials.SMPEssentials;
import me.khajiitos.smpessentials.data.Team;
import me.khajiitos.smpessentials.manager.TeamManager;
import me.khajiitos.smpessentials.packet.teammanager.c2s.UpdateBannerPacket;
import me.khajiitos.smpessentials.packet.teammanager.s2c.ErrorPacket;
import me.khajiitos.smpessentials.packet.teammanager.s2c.RefreshCurrentTabPacket;
import net.minecraft.item.BannerItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Hand;

import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.NetworkEvent;


import java.util.function.Supplier;

public class UpdateBannerHandler {

    public static void handle(UpdateBannerPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ServerPlayerEntity sender = ctx.get().getSender();

        if (sender == null) {
            return;
        }

        Team team = TeamManager.getTeam(sender);

        if (team == null) {
            return;
        }

        if (sender.getItemInHand(Hand.MAIN_HAND).getItem() instanceof BannerItem) {
            BannerItem bannerItem = (BannerItem) sender.getItemInHand(Hand.MAIN_HAND).getItem();
            CompoundNBT bannerTag = new CompoundNBT();
            bannerTag.putInt("Color", bannerItem.getColor().getId());

            CompoundNBT blockEntityData = sender.getItemInHand(Hand.MAIN_HAND).getTagElement("BlockEntityTag");

            if (blockEntityData != null && blockEntityData.contains("Patterns")) {
                bannerTag.put("Patterns", blockEntityData.getList("Patterns", Constants.NBT.TAG_COMPOUND));
            } else {
                bannerTag.put("Patterns", new ListNBT());
            }

            team.banner = bannerTag;
            SMPEssentials.getData().setDirty();
            Packets.sendToPlayer(sender, new RefreshCurrentTabPacket(team.toNbtForGui()));
        } else {
            Packets.sendToPlayer(sender, new ErrorPacket("You need to be holding a banner!"));
        }
    }
}
