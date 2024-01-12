package me.khajiitos.smpessentials.packet;

import me.khajiitos.smpessentials.packet.handler.CloseRulesHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;


import java.util.function.Supplier;

public class CloseRulesPacket {

    public void encode(PacketBuffer buf) {

    }

    public static CloseRulesPacket decode(PacketBuffer buf) {
        return new CloseRulesPacket();
    }

    public static void handle(CloseRulesPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> CloseRulesHandler.handle(packet, ctx));
        ctx.get().setPacketHandled(true);
    }
}
