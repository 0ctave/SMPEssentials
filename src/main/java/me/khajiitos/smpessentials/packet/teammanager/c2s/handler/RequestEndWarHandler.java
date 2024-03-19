package me.khajiitos.smpessentials.packet.teammanager.c2s.handler;

import me.khajiitos.smpessentials.data.Team;
import me.khajiitos.smpessentials.data.War;
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

        War war = TeamManager.getWars().get(packet.warID);

        if (war == null) {
            return;
        }

        Team enemyTeam = TeamManager.getTeamByUuid(war.getOtherTeam(TeamManager.getTeamUuid(requestingTeam)));

        Team.Role role = requestingTeam.members.getOrDefault(sender.getUUID(), Team.Role.MEMBER);

        if (role != Team.Role.TEAM_LEADER) {
            return;
        }


        if (enemyTeam.wars.containsKey(packet.warID) && enemyTeam.wars.get(packet.warID)) {

            requestingTeam.wars.remove(packet.warID);
            enemyTeam.wars.remove(packet.warID);

            TeamManager.getWars().remove(packet.warID);

            requestingTeam.broadcast(new StringTextComponent("§4Your team ended war with §c" + enemyTeam.name + "§4!"));
            enemyTeam.broadcast(new StringTextComponent("§4Your team ended war with §c" + requestingTeam.name + "§4!"));
        }

        if (requestingTeam.wars.containsKey(packet.warID) && !requestingTeam.wars.get(packet.warID)) {

            requestingTeam.wars.replace(packet.warID, true);

            requestingTeam.broadcast(new StringTextComponent("§cYour team requested to end the war with §4" + enemyTeam.name + "§c!"));
            enemyTeam.broadcast(new StringTextComponent("§c" + requestingTeam.name + " §4requested to end the war with your team!"));

        }

        RequestWarsHandler.handle(sender);
        //Packets.sendToPlayer(sender, new OpenTeamInfoPacket(tag));
    }
}
