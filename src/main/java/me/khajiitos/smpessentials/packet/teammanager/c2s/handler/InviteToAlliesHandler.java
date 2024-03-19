package me.khajiitos.smpessentials.packet.teammanager.c2s.handler;

import me.khajiitos.smpessentials.data.Team;
import me.khajiitos.smpessentials.manager.TeamManager;
import me.khajiitos.smpessentials.packet.teammanager.c2s.InviteToAlliesPacket;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.network.NetworkEvent;


import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

public class InviteToAlliesHandler {

    public static void handle(InviteToAlliesPacket packet, Supplier<NetworkEvent.Context> ctx) {
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

        if (role.ordinal() < Team.Role.TEAM_MANAGER.ordinal()) {
            return;
        }

        // Don't allow inviting to allies if the teams are at war
        if (!team.wars.isEmpty() || !invitedTeam.wars.isEmpty()) {
            return;
        }

        // The teams are already allies
        if (team.allies.contains(packet.teamUuid)) {
            return;
        }

        UUID teamUUID = TeamManager.getTeamUuid(team);

        Set<UUID> ourInviters = TeamManager.teamAllyInvites.get(teamUUID);
        Set<UUID> theirInviters = TeamManager.teamAllyInvites.get(packet.teamUuid);

        // Check if the invited team already has a pending invite
        if (ourInviters != null && ourInviters.contains(packet.teamUuid)) {
            team.allies.add(packet.teamUuid);
            invitedTeam.allies.add(teamUUID);
            team.broadcast(new StringTextComponent("§2Your team is now allies with §a" + invitedTeam.name + "§2!"));
            invitedTeam.broadcast(new StringTextComponent("§2Your team is now allies with §a" + team.name + "§2!"));
        } else if (theirInviters == null || !theirInviters.contains(teamUUID)) {
            TeamManager.teamAllyInvites.computeIfAbsent(packet.teamUuid, uuid -> new HashSet<>()).add(teamUUID);
            invitedTeam.broadcast(new StringTextComponent("§2Team §a" + team.name + " §2invited your team to become allies!"));
        }

    }
}
