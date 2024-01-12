package me.khajiitos.smpessentials.packet.handler;

import me.khajiitos.smpessentials.packet.RulesPacket;
import me.khajiitos.smpessentials.screen.RulesScreen;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.network.NetworkEvent;


import java.util.function.Supplier;

public class RulesHandler {

    public static void handle(RulesPacket packet, Supplier<NetworkEvent.Context> ctx) {
        Minecraft.getInstance().setScreen(new RulesScreen(packet.getRules()));
    }
}
