package me.khajiitos.smpessentials.packet.teammanager.c2s;

import me.khajiitos.smpessentials.packet.teammanager.c2s.handler.RequestWarsHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;


import java.util.function.Supplier;

public class RequestWarsPacket {

    public void encode(PacketBuffer buf) {}

    public static RequestWarsPacket decode(PacketBuffer buf) {
        return new RequestWarsPacket();
    }

    public static void handle(RequestWarsPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> RequestWarsHandler.handle(packet, ctx));
        ctx.get().setPacketHandled(true);
    }
}
