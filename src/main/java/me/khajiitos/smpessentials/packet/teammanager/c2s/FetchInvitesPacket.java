package me.khajiitos.smpessentials.packet.teammanager.c2s;

import me.khajiitos.smpessentials.packet.teammanager.c2s.handler.FetchInvitesHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;


import java.util.function.Supplier;

public class FetchInvitesPacket {

    public void encode(PacketBuffer buf) {}

    public static FetchInvitesPacket decode(PacketBuffer buf) {
        return new FetchInvitesPacket();
    }

    public static void handle(FetchInvitesPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> FetchInvitesHandler.handle(packet, ctx));
        ctx.get().setPacketHandled(true);
    }
}
