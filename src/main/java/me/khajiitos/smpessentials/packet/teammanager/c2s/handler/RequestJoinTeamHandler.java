package me.khajiitos.smpessentials.packet.teammanager.c2s.handler;

import me.khajiitos.smpessentials.data.Team;
import me.khajiitos.smpessentials.manager.TeamManager;
import me.khajiitos.smpessentials.packet.teammanager.c2s.RequestJoinTeamPacket;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.network.NetworkEvent;


import java.util.function.Supplier;

public class RequestJoinTeamHandler {
    public static void handle(RequestJoinTeamPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ServerPlayerEntity sender = ctx.get().getSender();

        if (sender == null) {
            return;
        }

        Team team = TeamManager.getTeamByUuid(packet.team);

        if (team == null) {
            return;
        }

        Team senderTeam = TeamManager.getTeam(sender);

        if (senderTeam != null) {
            return;
        }

        if (TeamManager.requestToJoinTeam(sender, team)) {
            team.broadcast(new StringTextComponent("§a" + sender.getScoreboardName() + " §2has requested to join your team!"));
        }
    }
}
