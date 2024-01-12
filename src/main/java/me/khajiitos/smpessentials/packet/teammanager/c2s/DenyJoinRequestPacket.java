package me.khajiitos.smpessentials.packet.teammanager.c2s;

import me.khajiitos.smpessentials.packet.teammanager.c2s.handler.DenyJoinRequestHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;


import java.util.UUID;
import java.util.function.Supplier;

public class DenyJoinRequestPacket {

    public final String playerName;

    public DenyJoinRequestPacket(String playerName) {
        this.playerName = playerName;
    }

    public void encode(PacketBuffer buf) {
        buf.writeUtf(this.playerName);
    }

    public static DenyJoinRequestPacket decode(PacketBuffer buf) {
        return new DenyJoinRequestPacket(buf.readUtf());
    }

    public static void handle(DenyJoinRequestPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> DenyJoinRequestHandler.handle(packet, ctx));
        ctx.get().setPacketHandled(true);
    }
}
