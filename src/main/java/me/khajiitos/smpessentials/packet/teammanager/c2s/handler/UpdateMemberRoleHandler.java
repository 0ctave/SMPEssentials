package me.khajiitos.smpessentials.packet.teammanager.c2s.handler;

import me.khajiitos.smpessentials.Packets;
import me.khajiitos.smpessentials.SMPEssentials;
import me.khajiitos.smpessentials.data.Team;
import me.khajiitos.smpessentials.manager.TeamManager;
import me.khajiitos.smpessentials.packet.teammanager.c2s.UpdateMemberRolePacket;
import me.khajiitos.smpessentials.packet.teammanager.s2c.UpdateTeamManagerPacket;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.common.UsernameCache;
import net.minecraftforge.fml.network.NetworkEvent;


import java.util.UUID;
import java.util.function.Supplier;

public class UpdateMemberRoleHandler {

    public static void handle(UpdateMemberRolePacket packet, Supplier<NetworkEvent.Context> ctx) {
        ServerPlayerEntity sender = ctx.get().getSender();

        if (sender == null) {
            return;
        }

        Team team = TeamManager.getTeam(sender);

        if (team == null) {
            return;
        }

        Team.Role role = team.members.get(sender.getUUID());

        if (role != Team.Role.TEAM_LEADER) {
            return;
        }

        for (UUID member : team.members.keySet()) {
            String lastKnownName = UsernameCache.getLastKnownUsername(member);
            if (lastKnownName != null && lastKnownName.equals(packet.memberName)) {
                team.members.put(member, packet.role);
                SMPEssentials.getData().setDirty();
                Packets.sendToPlayer(sender, new UpdateTeamManagerPacket(true, team.toNbtForGui()));
                break;
            }
        }
    }
}
