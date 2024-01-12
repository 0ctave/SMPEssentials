package me.khajiitos.smpessentials.packet.teammanager.c2s.handler;

import me.khajiitos.smpessentials.Packets;
import me.khajiitos.smpessentials.data.Team;
import me.khajiitos.smpessentials.manager.TeamManager;
import me.khajiitos.smpessentials.packet.teammanager.c2s.RequestAlliesPacket;
import me.khajiitos.smpessentials.packet.teammanager.s2c.AlliesPacket;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.network.NetworkEvent;


import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

public class RequestAlliesHandler {

    public static void handle(ServerPlayerEntity sender) {
        if (sender == null) {
            return;
        }

        Team team = TeamManager.getTeam(sender);

        if (team == null) {
            return;
        }

        CompoundNBT tag = new CompoundNBT();

        ListNBT activeAllies = new ListNBT();
        team.allies.forEach(uuid -> {
            Team allyTeam = TeamManager.getTeamByUuid(uuid);

            if (allyTeam == null) {
                return;
            }

            CompoundNBT allyTeamTag = new CompoundNBT();
            allyTeamTag.putUUID("uuid", uuid);
            allyTeamTag.putString("name", allyTeam.name);
            allyTeamTag.putString("tag", allyTeam.tag);

            activeAllies.add(allyTeamTag);
        });
        tag.put("active", activeAllies);

        Team.Role senderRole = team.members.getOrDefault(sender.getUUID(), Team.Role.MEMBER);

        if (senderRole.ordinal() >= Team.Role.TEAM_MANAGER.ordinal()) {
            ListNBT invitesTag = new ListNBT();

            Set<UUID> invites = TeamManager.teamAllyInvites.get(TeamManager.getTeamUuid(team));

            if (invites != null) {
                invites.forEach(uuid -> {
                    Team invitingTeam = TeamManager.getTeamByUuid(uuid);

                    if (invitingTeam == null) {
                        return;
                    }

                    CompoundNBT invitingTeamTag = new CompoundNBT();
                    invitingTeamTag.putUUID("uuid", uuid);
                    invitingTeamTag.putString("name", invitingTeam.name);
                    invitingTeamTag.putString("tag", invitingTeam.tag);

                    invitesTag.add(invitingTeamTag);
                });
            }

            tag.put("invites", invitesTag);
        }
        Packets.sendToPlayer(sender, new AlliesPacket(tag));
    }

    public static void handle(RequestAlliesPacket packet, Supplier<NetworkEvent.Context> ctx) {
        handle(ctx.get().getSender());
    }
}
