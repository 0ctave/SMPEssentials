package me.khajiitos.smpessentials.packet.teammanager.c2s.handler;

import me.khajiitos.smpessentials.SMPEssentials;
import me.khajiitos.smpessentials.data.Team;
import me.khajiitos.smpessentials.manager.TeamManager;
import me.khajiitos.smpessentials.packet.teammanager.c2s.UpdateFriendlyFirePacket;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.network.NetworkEvent;


import java.util.function.Supplier;

public class UpdateFriendlyFireHandler {

    public static void handle(UpdateFriendlyFirePacket packet, Supplier<NetworkEvent.Context> ctx) {
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

        if (role.ordinal() < Team.Role.TEAM_MANAGER.ordinal()) {
            return;
        }

        team.friendlyFire = packet.enabled;
        SMPEssentials.getData().setDirty();
    }
}
