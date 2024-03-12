package me.khajiitos.smpessentials.packet.teammanager.c2s.handler;

import me.khajiitos.smpessentials.data.Team;
import me.khajiitos.smpessentials.manager.TeamManager;
import me.khajiitos.smpessentials.packet.teammanager.c2s.RequestEndWarPacket;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.network.NetworkEvent;


import java.util.UUID;
import java.util.function.Supplier;

public class RequestEndWarHandler {

    public static void handle(RequestEndWarPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ServerPlayerEntity sender = ctx.get().getSender();

        if (sender == null) {
            return;
        }

        Team requestingTeam = TeamManager.getTeam(sender);

        if (requestingTeam == null) {
            return;
        }

        Team team = TeamManager.getTeamByUuid(packet.team);

        if (team == null) {
            return;
        }

        Team.Role role = requestingTeam.members.getOrDefault(sender.getUUID(), Team.Role.MEMBER);

        if (role != Team.Role.TEAM_LEADER) {
            return;
        }

        UUID requestingTeamUuid = TeamManager.getTeamUuid(requestingTeam);


        if (team.wars.containsKey(requestingTeamUuid) && team.wars.get(requestingTeamUuid)) {
            requestingTeam.wars.remove(packet.team);
            team.wars.remove(requestingTeamUuid);

            requestingTeam.broadcast(new StringTextComponent("§4Your team ended war with §c" + team.name + "§4!"));
            team.broadcast(new StringTextComponent("§4Your team ended war with §c" + requestingTeam.name + "§4!"));
        }

        if (requestingTeam.wars.containsKey(packet.team) && !requestingTeam.wars.get(packet.team)) {

            requestingTeam.wars.replace(packet.team, true);

            requestingTeam.broadcast(new StringTextComponent("§cYour team requested to end the war with §4" + team.name + "§c!"));
            team.broadcast(new StringTextComponent("§c" + requestingTeam.name + " §4requested to end the war with your team!"));

        }

        RequestWarsHandler.handle(sender);
        //Packets.sendToPlayer(sender, new OpenTeamInfoPacket(tag));
    }
}
