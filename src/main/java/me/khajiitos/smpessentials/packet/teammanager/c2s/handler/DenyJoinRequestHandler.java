package me.khajiitos.smpessentials.packet.teammanager.c2s.handler;

import me.khajiitos.smpessentials.Packets;
import me.khajiitos.smpessentials.Utils;
import me.khajiitos.smpessentials.data.Team;
import me.khajiitos.smpessentials.manager.TeamManager;
import me.khajiitos.smpessentials.packet.teammanager.c2s.DenyJoinRequestPacket;
import me.khajiitos.smpessentials.packet.teammanager.s2c.UpdateTeamManagerPacket;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.common.UsernameCache;
import net.minecraftforge.fml.network.NetworkEvent;


import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

public class DenyJoinRequestHandler {
    public static void handle(DenyJoinRequestPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ServerPlayerEntity sender = ctx.get().getSender();

        if (sender == null) {
            return;
        }

        Team team = TeamManager.getTeam(sender);

        if (team == null) {
            return;
        }

        Team.Role role = team.members.getOrDefault(sender.getUUID(), Team.Role.MEMBER);

        if (role.ordinal() < Team.Role.TEAM_MANAGER.ordinal()) {
            return;
        }

        Set<UUID> requests = TeamManager.teamRequests.get(TeamManager.getTeamUuid(team));

        if (requests != null) {
            UUID playerUUID = Utils.getPlayerUUID(packet.playerName);

            if (playerUUID == null) {
                return;
            }

            requests.remove(playerUUID);
        }

        Packets.sendToPlayer(sender, new UpdateTeamManagerPacket(true, team.toNbtForGui()));
    }
}
