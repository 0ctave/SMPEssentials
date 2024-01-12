package me.khajiitos.smpessentials.packet.teammanager.c2s;

import me.khajiitos.smpessentials.packet.teammanager.c2s.handler.RequestAlliesHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;


import java.util.function.Supplier;

public class RequestAlliesPacket {

    public void encode(PacketBuffer buf) {}

    public static RequestAlliesPacket decode(PacketBuffer buf) {
        return new RequestAlliesPacket();
    }

    public static void handle(RequestAlliesPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> RequestAlliesHandler.handle(packet, ctx));
        ctx.get().setPacketHandled(true);
    }
}
