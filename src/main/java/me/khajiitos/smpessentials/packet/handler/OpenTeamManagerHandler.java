package me.khajiitos.smpessentials.packet.handler;

import me.khajiitos.smpessentials.packet.OpenTeamManagerPacket;
import me.khajiitos.smpessentials.screen.TeamManagerScreen;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.network.NetworkEvent;


import java.util.function.Supplier;

public class OpenTeamManagerHandler {

    public static void handle(OpenTeamManagerPacket packet, Supplier<NetworkEvent.Context> ctx) {
        Minecraft.getInstance().setScreen(new TeamManagerScreen(packet.hasTeam, packet.data));
    }
}