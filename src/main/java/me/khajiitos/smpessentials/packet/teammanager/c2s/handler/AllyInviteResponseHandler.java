package me.khajiitos.smpessentials.packet.teammanager.c2s.handler;

import me.khajiitos.smpessentials.SMPEssentials;
import me.khajiitos.smpessentials.data.Team;
import me.khajiitos.smpessentials.manager.TeamManager;
import me.khajiitos.smpessentials.packet.teammanager.c2s.AllyInviteResponsePacket;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.network.NetworkEvent;


import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

public class AllyInviteResponseHandler {

    public static void handle(AllyInviteResponsePacket packet, Supplier<NetworkEvent.Context> ctx) {
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
        Set<UUID> teamsInvited = TeamManager.teamAllyInvites.get(teamUUID);

        if (teamsInvited == null) {
            return;
        }

        if (!packet.accept) {
            teamsInvited.remove(packet.teamUuid);
            return;
        }

        if (team.allies.contains(packet.teamUuid) || TeamManager.areTeamsAtWar(teamUUID, packet.teamUuid)) {
            teamsInvited.remove(packet.teamUuid);
            return;
        }

        if (teamsInvited.contains(packet.teamUuid)) {
            teamsInvited.remove(packet.teamUuid);

            Set<UUID> teamsInvitedInviter = TeamManager.teamAllyInvites.get(packet.teamUuid);

            if (teamsInvitedInviter != null) {
                teamsInvitedInviter.remove(teamUUID);
            }

            team.allies.add(packet.teamUuid);
            inviter.allies.add(teamUUID);
            team.broadcast(new StringTextComponent("§2Your team is now allies with §a" + inviter.name + "§2!"));
            inviter.broadcast(new StringTextComponent("§2Your team is now allies with §a" + team.name + "§2!"));

            // Remove war invites

            Set<UUID> teamsInvitedToWar = TeamManager.teamWarInvites.get(packet.teamUuid);
            if (teamsInvitedToWar != null) {
                teamsInvitedToWar.remove(teamUUID);
            }

            Set<UUID> teamsInvitedToWarOur = TeamManager.teamWarInvites.get(teamUUID);
            if (teamsInvitedToWarOur != null) {
                teamsInvitedToWarOur.remove(packet.teamUuid);
            }

            // Dirty way to refresh the allies list
            RequestAlliesHandler.handle(sender);
            SMPEssentials.getData().setDirty();
        }
    }
}
