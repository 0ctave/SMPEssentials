package me.khajiitos.smpessentials.packet.handler;

import me.khajiitos.smpessentials.packet.CloseRulesPacket;
import me.khajiitos.smpessentials.screen.RulesScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.fml.network.NetworkEvent;


import java.util.function.Supplier;

public class CloseRulesHandler {

    public static void handle(CloseRulesPacket packet, Supplier<NetworkEvent.Context> ctx) {
        if (Minecraft.getInstance().screen instanceof RulesScreen) {
            Minecraft.getInstance().setScreen(null);
            if (Minecraft.getInstance().player != null) {
                Minecraft.getInstance().player.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 1.f, 1.25f);
            }
        }
    }
}
