package me.khajiitos.smpessentials.packet.teammanager.c2s.handler;

import me.khajiitos.smpessentials.data.Team;
import me.khajiitos.smpessentials.manager.TeamManager;
import me.khajiitos.smpessentials.packet.teammanager.c2s.WarInviteResponsePacket;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.network.NetworkEvent;


import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

public class WarInviteResponseHandler {

    public static void handle(WarInviteResponsePacket packet, Supplier<NetworkEvent.Context> ctx) {
        ServerPlayerEntity sender = ctx.get().getSender();

        if (sender == null) {
            return;
        }

        Team team = TeamManager.getTeam(sender);

        if (team == null) {
            return;
        }

        Team inviter = TeamManager.getTeamByUuid(packet.teamUuid);

        if (inviter == null) {
            return;
        }

        UUID teamUUID = TeamManager.getTeamUuid(team);
        Set<UUID> teamsInvited = TeamManager.teamWarInvites.get(teamUUID);

        if (teamsInvited == null) {
            return;
        }

        if (!packet.accept) {
            teamsInvited.remove(packet.teamUuid);
            return;
        }

        if (teamsInvited.contains(packet.teamUuid)) {
            teamsInvited.remove(packet.teamUuid);
            team.wars.put(packet.teamUuid, false);
            inviter.wars.put(teamUUID, false);
            team.broadcast(new StringTextComponent("§4Your team is now at war with §c" + inviter.name + "§4!"));
            inviter.broadcast(new StringTextComponent("§4Your team is at war with §c" + team.name + "§4!"));

            // Remove ally invites

            Set<UUID> teamsInvitedToAlly = TeamManager.teamAllyInvites.get(packet.teamUuid);
            if (teamsInvitedToAlly != null) {
                teamsInvitedToAlly.remove(teamUUID);
            }

            Set<UUID> teamsInvitedToAllyOur = TeamManager.teamAllyInvites.get(teamUUID);
            if (teamsInvitedToAllyOur != null) {
                teamsInvitedToAllyOur.remove(packet.teamUuid);
            }

            // Dirty way to refresh the war list
            RequestWarsHandler.handle(sender);
        }
    }
}
