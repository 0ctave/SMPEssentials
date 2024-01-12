package me.khajiitos.smpessentials.packet.teammanager.c2s.handler;

import me.khajiitos.smpessentials.Packets;
import me.khajiitos.smpessentials.SMPEssentials;
import me.khajiitos.smpessentials.data.Team;
import me.khajiitos.smpessentials.manager.TeamManager;
import me.khajiitos.smpessentials.packet.OpenTeamManagerPacket;
import me.khajiitos.smpessentials.packet.teammanager.c2s.LeaveTeamPacket;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.network.NetworkEvent;


import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

public class LeaveTeamHandler {

    public static void handle(LeaveTeamPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ServerPlayerEntity sender = ctx.get().getSender();

        if (sender == null) {
            return;
        }

        Team team = TeamManager.getTeam(sender);

        if (team == null) {
            return;
        }

        Team.Role role = team.members.get(sender.getUUID());

        TeamManager.setPlayerTeam(sender, null, null);

        team.broadcast(new StringTextComponent("§c" + sender.getScoreboardName() + " §4left the team!"));

        if (role == Team.Role.TEAM_LEADER) {
            UUID grantLeaderTo = null;
            int grantLeaderToPreviousRole = -1;

            for (Map.Entry<UUID, Team.Role> uuidRoleEntry : team.members.entrySet()) {
                UUID uuid = uuidRoleEntry.getKey();
                Team.Role memberRole = uuidRoleEntry.getValue();
                if (grantLeaderToPreviousRole < memberRole.ordinal()) {
                    grantLeaderTo = uuid;
                    grantLeaderToPreviousRole = memberRole.ordinal();
                }
            }

            if (grantLeaderTo != null) {
                team.members.put(grantLeaderTo, Team.Role.TEAM_LEADER);
                SMPEssentials.getData().setDirty();
            } else {
                // No one else in the team, delete it
                TeamManager.deleteTeam(team);
            }
        }

        Packets.sendToPlayer(sender, new OpenTeamManagerPacket(false, null));
    }
}
