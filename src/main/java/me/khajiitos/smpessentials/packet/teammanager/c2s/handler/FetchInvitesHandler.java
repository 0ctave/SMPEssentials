package me.khajiitos.smpessentials.packet.teammanager.c2s.handler;

import me.khajiitos.smpessentials.Packets;
import me.khajiitos.smpessentials.data.Team;
import me.khajiitos.smpessentials.manager.TeamManager;
import me.khajiitos.smpessentials.packet.teammanager.c2s.FetchInvitesPacket;
import me.khajiitos.smpessentials.packet.teammanager.s2c.InvitesPacket;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.network.NetworkEvent;


import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

public class FetchInvitesHandler {

    public static void handle(FetchInvitesPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ServerPlayerEntity sender = ctx.get().getSender();

        if (sender == null) {
            return;
        }

        ListNBT invites = new ListNBT();
        Set<UUID> teams = TeamManager.playerInvites.get(sender.getUUID());
        if (teams != null) {
            for (UUID team : teams) {
                Team teamObj = TeamManager.getTeamByUuid(team);
                if (teamObj != null) {
                    CompoundNBT teamTag = new CompoundNBT();
                    teamTag.putUUID("uuid", team);
                    teamTag.putString("name", teamObj.name);
                    teamTag.putString("tag", teamObj.tag);
                    invites.add(teamTag);
                }
            }
        }
        Packets.sendToPlayer(sender, new InvitesPacket(invites));
    }
}
