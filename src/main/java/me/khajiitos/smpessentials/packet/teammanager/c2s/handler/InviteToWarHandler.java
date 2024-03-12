package me.khajiitos.smpessentials.packet.teammanager.c2s.handler;

import me.khajiitos.smpessentials.data.Team;
import me.khajiitos.smpessentials.manager.TeamManager;
import me.khajiitos.smpessentials.packet.teammanager.c2s.InviteToWarPacket;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.network.NetworkEvent;


import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

public class InviteToWarHandler {


    public static void handle(InviteToWarPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ServerPlayerEntity sender = ctx.get().getSender();

        if (sender == null) {
            return;
        }

        Team team = TeamManager.getTeam(sender);

        if (team == null) {
            return;
        }

        Team invitedTeam = TeamManager.getTeamByUuid(packet.teamUuid);

        if (invitedTeam == null) {
            return;
        }

        Team.Role role = team.members.getOrDefault(sender.getUUID(), Team.Role.MEMBER);

        if (role.ordinal() != Team.Role.TEAM_LEADER.ordinal()) {
            return;
        }

        // Don't allow inviting to war if the teams are allies
        if (team.allies.contains(packet.teamUuid)) {
            return;
        }

        // The teams are already at war
        if (team.wars.containsKey(packet.teamUuid)) {
            return;
        }

        UUID teamUUID = TeamManager.getTeamUuid(team);

        Set<UUID> ourInviters = TeamManager.teamWarInvites.get(teamUUID);
        Set<UUID> theirInviters = TeamManager.teamWarInvites.get(packet.teamUuid);

        // Check if the invited team already has a pending invite
        if (ourInviters != null && ourInviters.contains(packet.teamUuid)) {
            team.wars.put(packet.teamUuid, false);
            invitedTeam.wars.put(teamUUID, false);

            invitedTeam.broadcast(new StringTextComponent("§4Your team is now at war with §c" + team.name + "§4!"));
            team.broadcast(new StringTextComponent("§4Your team is now at war with §c" + invitedTeam.name + "§4!"));
        } else if (theirInviters == null || !theirInviters.contains(teamUUID)) {
            TeamManager.teamWarInvites.computeIfAbsent(packet.teamUuid, uuid -> new HashSet<>()).add(teamUUID);
            invitedTeam.broadcast(new StringTextComponent("§4Team §c" + team.name + " §4invited your team to have a war!"));
        }
    }
}
