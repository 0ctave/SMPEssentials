package me.khajiitos.smpessentials.packet.teammanager.c2s.handler;

import me.khajiitos.smpessentials.Packets;
import me.khajiitos.smpessentials.data.Team;
import me.khajiitos.smpessentials.data.War;
import me.khajiitos.smpessentials.manager.TeamManager;
import me.khajiitos.smpessentials.packet.teammanager.c2s.RequestWarsPacket;
import me.khajiitos.smpessentials.packet.teammanager.s2c.WarsPacket;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.network.NetworkEvent;


import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

public class RequestWarsHandler {

    public static void handle(ServerPlayerEntity sender) {
        if (sender == null) {
            return;
        }

        Team team = TeamManager.getTeam(sender);

        if (team == null) {
            return;
        }

        UUID teamUUID = TeamManager.getTeamUuid(team);

        if (teamUUID == null) {
            return;
        }

        CompoundNBT tag = new CompoundNBT();

        ListNBT activeWars = new ListNBT();
        team.wars.forEach((uuid, askedPeace) -> {
            War war = TeamManager.getWars().get(uuid);

            if (war == null) {
                return;
            }

            Team warTeam = TeamManager.getTeamByUuid(war.getOtherTeam(teamUUID));

            if (warTeam == null) {
                return;
            }

            CompoundNBT warTeamTag = new CompoundNBT();
            warTeamTag.putUUID("uuid", uuid);
            warTeamTag.putString("name", warTeam.name);
            warTeamTag.putString("tag", warTeam.tag);

            warTeamTag.putInt("kills", war.getTeamKills(teamUUID));
            warTeamTag.putInt("HP", war.getTeamHP(war.getOtherTeam(teamUUID)));

            warTeamTag.putBoolean("askedPeace", askedPeace);
            warTeamTag.putBoolean("theyAskedPeace", warTeam.wars.getOrDefault(uuid, false));

            activeWars.add(warTeamTag);
        });
        tag.put("active", activeWars);

        Team.Role senderRole = team.members.getOrDefault(sender.getUUID(), Team.Role.MEMBER);

        if (senderRole.ordinal() >= Team.Role.TEAM_MANAGER.ordinal()) {
            ListNBT invitesTag = new ListNBT();

            Set<UUID> invites = TeamManager.teamWarInvites.get(TeamManager.getTeamUuid(team));

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
        Packets.sendToPlayer(sender, new WarsPacket(tag));

    }

    public static void handle(RequestWarsPacket packet, Supplier<NetworkEvent.Context> ctx) {
        handle(ctx.get().getSender());
    }
}
