package me.khajiitos.smpessentials.packet.teammanager.c2s;

import me.khajiitos.smpessentials.packet.teammanager.c2s.handler.AcceptJoinRequestHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;


import java.util.UUID;
import java.util.function.Supplier;

public class AcceptJoinRequestPacket {

    public final String playerName;

    public AcceptJoinRequestPacket(String playerName) {
        this.playerName = playerName;
    }

    public void encode(PacketBuffer buf) {
        buf.writeUtf(this.playerName);
    }

    public static AcceptJoinRequestPacket decode(PacketBuffer buf) {
        return new AcceptJoinRequestPacket(buf.readUtf());
    }

    public static void handle(AcceptJoinRequestPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> AcceptJoinRequestHandler.handle(packet, ctx));
        ctx.get().setPacketHandled(true);
    }
}
