package me.khajiitos.smpessentials.packet.teammanager.c2s.handler;

import me.khajiitos.smpessentials.Packets;
import me.khajiitos.smpessentials.SMPEssentials;
import me.khajiitos.smpessentials.data.Team;
import me.khajiitos.smpessentials.manager.TeamManager;
import me.khajiitos.smpessentials.packet.OpenTeamManagerPacket;
import me.khajiitos.smpessentials.packet.teammanager.c2s.InvitePacket;
import me.khajiitos.smpessentials.packet.teammanager.s2c.ErrorPacket;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.network.NetworkEvent;


import java.util.function.Supplier;

public class InviteHandler {

    public static void handle(InvitePacket packet, Supplier<NetworkEvent.Context> ctx) {
        ServerPlayerEntity sender = ctx.get().getSender();

        if (sender == null) {
            return;
        }

        Team team = TeamManager.getTeam(sender);

        if (team == null) {
            return;
        }

        Team.Role role = team.members.get(sender.getUUID());

        if (role == null) {
            return;
        }

        if (role.ordinal() >= Team.Role.TEAM_MANAGER.ordinal()) {
            ServerPlayerEntity target = SMPEssentials.server.getPlayerList().getPlayers().stream().filter(player -> player.getScoreboardName().equals(packet.username)).findFirst().orElse(null);

            if (target == sender) {
                Packets.sendToPlayer(sender, new ErrorPacket("You can't invite yourself!"));
            } else if (!team.wars.isEmpty()){
                Packets.sendToPlayer(sender, new ErrorPacket("Your team is at war, you can't invite anyone until the war ended!"));
            } else if (target != null) {
                Team targetTeam = TeamManager.getTeam(target);
                if (targetTeam != team) {
                    if (targetTeam != null && !targetTeam.wars.isEmpty()){
                        Packets.sendToPlayer(sender, new ErrorPacket("This player's team is at war, you can't invite anyone until the war ended!"));
                    } else if (TeamManager.invitePlayer(target, team)) {
                        target.sendMessage(new StringTextComponent("§2You have been invited to join §a" + team.name + "§2!"), ChatType.SYSTEM, target.getUUID());
                        team.broadcast(new StringTextComponent("§a" + sender.getScoreboardName() + " §2invited §a" + target.getScoreboardName() + " §2to join the team!"));
                        Packets.sendToPlayer(sender, new OpenTeamManagerPacket(true, team.toNbtForGui()));
                    } else {
                        Packets.sendToPlayer(sender, new ErrorPacket("This player is already invited!"));
                    }
                } else {
                    Packets.sendToPlayer(sender, new ErrorPacket("This player is already in your team!"));
                }
            } else {
                Packets.sendToPlayer(sender, new ErrorPacket("This player isn't online!"));
            }
        }
    }
}
