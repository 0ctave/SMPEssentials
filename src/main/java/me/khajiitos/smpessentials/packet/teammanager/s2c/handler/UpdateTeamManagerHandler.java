package me.khajiitos.smpessentials.packet.teammanager.s2c.handler;

import me.khajiitos.smpessentials.packet.teammanager.s2c.UpdateTeamManagerPacket;
import me.khajiitos.smpessentials.screen.TeamManagerScreen;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.network.NetworkEvent;


import java.util.function.Supplier;

public class UpdateTeamManagerHandler {

    public static void handle(UpdateTeamManagerPacket packet, Supplier<NetworkEvent.Context> ctx) {
        if (Minecraft.getInstance().screen instanceof TeamManagerScreen) {
            TeamManagerScreen teamManagerScreen = (TeamManagerScreen) Minecraft.getInstance().screen;
            teamManagerScreen.data = packet.data;
            teamManagerScreen.hasTeam = packet.hasTeam;
            teamManagerScreen.refresh();
        }
    }
}
