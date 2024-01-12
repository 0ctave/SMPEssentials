package me.khajiitos.smpessentials.packet.teammanager.c2s.handler;

import me.khajiitos.smpessentials.Packets;
import me.khajiitos.smpessentials.SMPEssentials;
import me.khajiitos.smpessentials.data.SMPData;
import me.khajiitos.smpessentials.packet.teammanager.c2s.FetchAllTeamsPacket;
import me.khajiitos.smpessentials.packet.teammanager.s2c.AllTeamsPacket;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.network.NetworkEvent;


import java.util.function.Supplier;

public class FetchAllTeamsHandler {

    public static void handle(FetchAllTeamsPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ServerPlayerEntity sender = ctx.get().getSender();

        if (sender == null) {
            return;
        }

        SMPData data = SMPEssentials.getData();
        ListNBT listTag = new ListNBT();
        data.getTeams().forEach(((uuid, team) -> {
            CompoundNBT tag = new CompoundNBT();
            tag.putUUID("uuid", uuid);
            tag.putString("name", team.name);
            tag.putString("tag", team.tag);
            if (team.banner != null) {
                tag.put("banner", team.banner);
            }
            listTag.add(tag);
        }));

        Packets.sendToPlayer(sender, new AllTeamsPacket(listTag));
    }
}
