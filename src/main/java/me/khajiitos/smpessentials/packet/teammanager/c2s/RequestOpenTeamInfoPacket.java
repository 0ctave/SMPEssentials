package me.khajiitos.smpessentials.packet.teammanager.c2s;

import me.khajiitos.smpessentials.packet.teammanager.c2s.handler.RequestOpenTeamInfoHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;


import java.util.UUID;
import java.util.function.Supplier;

public class RequestOpenTeamInfoPacket {
    public final UUID team;

    public RequestOpenTeamInfoPacket(UUID team) {
        this.team = team;
    }

    public void encode(PacketBuffer buf) {
        buf.writeUUID(team);
    }

    public static RequestOpenTeamInfoPacket decode(PacketBuffer buf) {
        return new RequestOpenTeamInfoPacket(buf.readUUID());
    }

    public static void handle(RequestOpenTeamInfoPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> RequestOpenTeamInfoHandler.handle(packet, ctx));
        ctx.get().setPacketHandled(true);
    }
}
