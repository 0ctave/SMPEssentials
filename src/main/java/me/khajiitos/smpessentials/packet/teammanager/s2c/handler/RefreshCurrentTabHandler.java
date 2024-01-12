package me.khajiitos.smpessentials.packet.teammanager.s2c.handler;

import me.khajiitos.smpessentials.packet.teammanager.s2c.RefreshCurrentTabPacket;
import me.khajiitos.smpessentials.screen.TeamManagerScreen;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.network.NetworkEvent;


import java.util.function.Supplier;

public class RefreshCurrentTabHandler {

    public static void handle(RefreshCurrentTabPacket packet, Supplier<NetworkEvent.Context> ctx) {
        if (Minecraft.getInstance().screen instanceof TeamManagerScreen) {
            TeamManagerScreen screen = (TeamManagerScreen) Minecraft.getInstance().screen;
            screen.onPacket(packet);
        }
    }
}
