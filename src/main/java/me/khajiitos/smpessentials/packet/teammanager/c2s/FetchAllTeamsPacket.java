package me.khajiitos.smpessentials.packet.teammanager.c2s;

import me.khajiitos.smpessentials.packet.teammanager.c2s.handler.FetchAllTeamsHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;


import java.util.function.Supplier;

public class FetchAllTeamsPacket {

    public void encode(PacketBuffer buf) {}

    public static FetchAllTeamsPacket decode(PacketBuffer buf) {
        return new FetchAllTeamsPacket();
    }

    public static void handle(FetchAllTeamsPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> FetchAllTeamsHandler.handle(packet, ctx));
        ctx.get().setPacketHandled(true);
    }
}
