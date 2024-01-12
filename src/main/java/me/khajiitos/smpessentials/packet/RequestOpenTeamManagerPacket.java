package me.khajiitos.smpessentials.packet;

import me.khajiitos.smpessentials.packet.handler.RequestOpenTeamManagerHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;


import java.util.function.Supplier;

public class RequestOpenTeamManagerPacket {

    public void encode(PacketBuffer buf) {

    }

    public static RequestOpenTeamManagerPacket decode(PacketBuffer buf) {
        return new RequestOpenTeamManagerPacket();
    }

    public static void handle(RequestOpenTeamManagerPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> RequestOpenTeamManagerHandler.handle(packet, ctx));
        ctx.get().setPacketHandled(true);
    }
}
