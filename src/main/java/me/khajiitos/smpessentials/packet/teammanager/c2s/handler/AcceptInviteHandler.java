package me.khajiitos.smpessentials.packet.teammanager.c2s.handler;

import me.khajiitos.smpessentials.Packets;
import me.khajiitos.smpessentials.data.Team;
import me.khajiitos.smpessentials.manager.TeamManager;
import me.khajiitos.smpessentials.packet.OpenTeamManagerPacket;
import me.khajiitos.smpessentials.packet.teammanager.c2s.AcceptInvitePacket;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.network.NetworkEvent;


import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

public class AcceptInviteHandler {

    public static void handle(AcceptInvitePacket packet, Supplier<NetworkEvent.Context> ctx) {
        ServerPlayerEntity sender = ctx.get().getSender();

        if (sender == null) {
            return;
        }

        Team team = TeamManager.getTeamByUuid(packet.teamUUID);

        if (team != null) {
            TeamManager.setPlayerTeam(sender, team, Team.Role.MEMBER);
            Set<UUID> invites = TeamManager.playerInvites.get(sender.getUUID());
            if (invites != null) {
                invites.remove(packet.teamUUID);
            }
            team.broadcast(new StringTextComponent("§a" + sender.getScoreboardName() + " §2joined the team!"));
            Packets.sendToPlayer(sender, new OpenTeamManagerPacket(true, team.toNbtForGui()));
        }
    }
}
