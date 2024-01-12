package me.khajiitos.smpessentials.packet.teammanager.c2s;

import me.khajiitos.smpessentials.packet.teammanager.c2s.handler.RequestEndWarHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;


import java.util.UUID;
import java.util.function.Supplier;

public class RequestEndWarPacket {
    public final UUID team;

    public RequestEndWarPacket(UUID team) {
        this.team = team;
    }

    public void encode(PacketBuffer buf) {
        buf.writeUUID(team);
    }

    public static RequestEndWarPacket decode(PacketBuffer buf) {
        return new RequestEndWarPacket(buf.readUUID());
    }

    public static void handle(RequestEndWarPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> RequestEndWarHandler.handle(packet, ctx));
        ctx.get().setPacketHandled(true);
    }
}
