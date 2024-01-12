package me.khajiitos.smpessentials.packet.teammanager.c2s;

import me.khajiitos.smpessentials.packet.teammanager.c2s.handler.RequestJoinTeamHandler;
import me.khajiitos.smpessentials.packet.teammanager.c2s.handler.RequestOpenTeamInfoHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;


import java.util.UUID;
import java.util.function.Supplier;

public class RequestJoinTeamPacket {
    public final UUID team;

    public RequestJoinTeamPacket(UUID team) {
        this.team = team;
    }

    public void encode(PacketBuffer buf) {
        buf.writeUUID(team);
    }

    public static RequestJoinTeamPacket decode(PacketBuffer buf) {
        return new RequestJoinTeamPacket(buf.readUUID());
    }

    public static void handle(RequestJoinTeamPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> RequestJoinTeamHandler.handle(packet, ctx));
        ctx.get().setPacketHandled(true);
    }
}
