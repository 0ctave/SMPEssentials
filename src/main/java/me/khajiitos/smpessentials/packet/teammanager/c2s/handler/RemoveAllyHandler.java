package me.khajiitos.smpessentials.packet.teammanager.c2s.handler;

import me.khajiitos.smpessentials.SMPEssentials;
import me.khajiitos.smpessentials.data.Team;
import me.khajiitos.smpessentials.manager.TeamManager;
import me.khajiitos.smpessentials.packet.teammanager.c2s.RemoveAllyPacket;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.network.NetworkEvent;


import java.util.UUID;
import java.util.function.Supplier;

public class RemoveAllyHandler {

    public static void handle(RemoveAllyPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ServerPlayerEntity sender = ctx.get().getSender();

        if (sender == null) {
            return;
        }

        Team team = TeamManager.getTeam(sender);

        if (team == null) {
            return;
        }

        UUID teamUUID = TeamManager.getTeamUuid(team);

        Team.Role role = team.members.getOrDefault(sender.getUUID(), Team.Role.MEMBER);

        if (role.ordinal() < Team.Role.TEAM_MANAGER.ordinal()) {
            return;
        }

        Team removedTeam = TeamManager.getTeamByUuid(packet.team);

        if (removedTeam == null) {
            return;
        }

        if (team.allies.contains(packet.team)) {
            team.allies.remove(packet.team);
            removedTeam.allies.remove(teamUUID);
            team.broadcast(new StringTextComponent("§4Your team is no longer an ally with §c" + removedTeam.name + "§4!"));
            removedTeam.broadcast(new StringTextComponent("§4Your team is no longer an ally with §c" + team.name + "§4!"));

            SMPEssentials.getData().setDirty();
            RequestAlliesHandler.handle(sender);

        }
    }
}
