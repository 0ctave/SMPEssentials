package me.khajiitos.smpessentials.packet.handler;

import me.khajiitos.smpessentials.Packets;
import me.khajiitos.smpessentials.data.Team;
import me.khajiitos.smpessentials.manager.TeamManager;
import me.khajiitos.smpessentials.packet.OpenTeamManagerPacket;
import me.khajiitos.smpessentials.packet.RequestOpenTeamManagerPacket;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.network.NetworkEvent;


import java.util.function.Supplier;

public class RequestOpenTeamManagerHandler {

    public static void handle(RequestOpenTeamManagerPacket packet, Supplier<NetworkEvent.Context> ctx) {

        ServerPlayerEntity player = ctx.get().getSender();

        if (player == null) {
            return;
        }

        Team playerTeam = TeamManager.getTeam(player);

        CompoundNBT tag = null;
        if (playerTeam != null) {
            tag = playerTeam.toNbtForGui();
        }

        Packets.sendToPlayer(ctx.get().getSender(), new OpenTeamManagerPacket(playerTeam != null, tag));
    }
}
