package me.khajiitos.smpessentials.packet.teammanager.c2s.handler;

import me.khajiitos.smpessentials.Packets;
import me.khajiitos.smpessentials.data.Team;
import me.khajiitos.smpessentials.manager.TeamManager;
import me.khajiitos.smpessentials.packet.teammanager.c2s.RequestOpenTeamInfoPacket;
import me.khajiitos.smpessentials.packet.teammanager.s2c.OpenTeamInfoPacket;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.network.NetworkEvent;


import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

public class RequestOpenTeamInfoHandler {

    public static void handle(RequestOpenTeamInfoPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ServerPlayerEntity sender = ctx.get().getSender();

        if (sender == null) {
            return;
        }

        Team senderTeam = TeamManager.getTeam(sender);

        Team team = TeamManager.getTeamByUuid(packet.team);

        if (team == null) {
            return;
        }

        CompoundNBT tag = team.toNbtForOthers();

        if (senderTeam != null) {
            UUID senderTeamUUID = TeamManager.getTeamUuid(senderTeam);
            // status:
            // 0 - not invited and not in war/ally
            // 1 - invited
            // 2 - allies/at war

            int allyStatus = 0;
            int warStatus = 0;

            if (senderTeam.allies.contains(packet.team)) {
                allyStatus = 2;
            } else if (TeamManager.teamAllyInvites.computeIfAbsent(packet.team, uuid -> new HashSet<>()).contains(senderTeamUUID)) {
                allyStatus = 1;
            }

            if (senderTeam.wars.stream().anyMatch(pair -> pair.getFirst().equals(packet.team))) {
                warStatus = 2;
            } else if (TeamManager.teamWarInvites.computeIfAbsent(packet.team, uuid -> new HashSet<>()).contains(senderTeamUUID)) {
                warStatus = 1;
            }

            tag.putInt("allyStatus", allyStatus);
            tag.putInt("warStatus", warStatus);
        } else {
            tag.putBoolean("requestedToJoin", TeamManager.teamRequests.getOrDefault(packet.team, new HashSet<>()).contains(sender.getUUID()));
        }

        Packets.sendToPlayer(sender, new OpenTeamInfoPacket(tag));
    }
}
