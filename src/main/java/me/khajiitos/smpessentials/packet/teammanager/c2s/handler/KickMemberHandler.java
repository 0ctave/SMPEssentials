package me.khajiitos.smpessentials.packet.teammanager.c2s.handler;

import me.khajiitos.smpessentials.Packets;
import me.khajiitos.smpessentials.SMPEssentials;
import me.khajiitos.smpessentials.data.Team;
import me.khajiitos.smpessentials.manager.TeamManager;
import me.khajiitos.smpessentials.packet.OpenTeamManagerPacket;
import me.khajiitos.smpessentials.packet.teammanager.c2s.KickMemberPacket;
import me.khajiitos.smpessentials.packet.teammanager.s2c.UpdateTeamManagerPacket;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.common.UsernameCache;
import net.minecraftforge.fml.network.NetworkEvent;


import java.util.UUID;
import java.util.function.Supplier;

public class KickMemberHandler {

    public static void handle(KickMemberPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ServerPlayerEntity sender = ctx.get().getSender();

        if (sender == null) {
            return;
        }

        Team team = TeamManager.getTeam(sender);

        if (team == null) {
            return;
        }

        Team.Role role = team.members.get(sender.getUUID());

        for (UUID member : team.members.keySet()) {
            String username = UsernameCache.getLastKnownUsername(member);

            if (username != null && username.equals(packet.member)) {
                Team.Role memberRole = team.members.get(member);

                if (role.ordinal() >  memberRole.ordinal()) {
                    TeamManager.setPlayerTeam(member, null, null);
                    team.broadcast(new StringTextComponent("§c" + username + " §4has been kicked from the team!"));
                    ServerPlayerEntity kickedPlayer = SMPEssentials.server.getPlayerList().getPlayer(member);

                    if (kickedPlayer != null) {
                        kickedPlayer.sendMessage(new StringTextComponent("§cYou have been kicked from your team!"), ChatType.SYSTEM, kickedPlayer.getUUID());
                    }

                    Packets.sendToPlayer(sender, new OpenTeamManagerPacket(true, team.toNbtForGui()));
                }
                break;
            }
        }
    }
}
