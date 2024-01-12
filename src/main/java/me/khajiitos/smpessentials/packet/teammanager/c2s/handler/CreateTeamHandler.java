package me.khajiitos.smpessentials.packet.teammanager.c2s.handler;

import me.khajiitos.smpessentials.Packets;
import me.khajiitos.smpessentials.SMPEssentials;
import me.khajiitos.smpessentials.data.Team;
import me.khajiitos.smpessentials.manager.TeamManager;
import me.khajiitos.smpessentials.packet.OpenTeamManagerPacket;
import me.khajiitos.smpessentials.packet.teammanager.c2s.CreateTeamPacket;
import me.khajiitos.smpessentials.packet.teammanager.s2c.ErrorPacket;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.network.NetworkEvent;

import org.apache.commons.lang3.CharUtils;

import java.util.function.Supplier;

public class CreateTeamHandler {

    public static void handle(CreateTeamPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ServerPlayerEntity sender = ctx.get().getSender();

        if (sender == null) {
            return;
        }

        Team team = TeamManager.getTeam(sender);

        if (team != null) {
            return;
        }

        String name = packet.getName();
        String tag = packet.getTag();

        for (Team otherTeam : SMPEssentials.getData().getTeams().values()) {
            if (otherTeam.tag.equals(tag)) {
                Packets.sendToPlayer(sender, new ErrorPacket("Team named '" + otherTeam.name + "' already uses this tag!"));
                return;
            }
        }

        for (int i = 0; i < tag.length(); i++) {
            if (!CharUtils.isAsciiAlpha(tag.charAt(i))) {
                Packets.sendToPlayer(sender, new ErrorPacket("Your team tag has illegal characters!"));
                return;
            }
        }

        if (name.length() < 4 || name.length() > 32) {
            Packets.sendToPlayer(sender, new ErrorPacket("Team name must be between 4 and 32 characters!"));
            return;
        }

        if (tag.length() < 2 || tag.length() > 4) {
            Packets.sendToPlayer(sender, new ErrorPacket("Team tag must be between 2 and 4 characters!"));
            return;
        }

        Team newTeam = TeamManager.createTeam();
        newTeam.name = name;
        newTeam.tag = tag.toUpperCase();
        TeamManager.setPlayerTeam(sender, newTeam, Team.Role.TEAM_LEADER);
        Packets.sendToPlayer(sender, new OpenTeamManagerPacket(true, newTeam.toNbtForGui()));
    }
}
