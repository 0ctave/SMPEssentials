package me.khajiitos.smpessentials.packet.teammanager.c2s;

import me.khajiitos.smpessentials.packet.teammanager.c2s.handler.InviteToWarHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;


import java.util.UUID;
import java.util.function.Supplier;

public class InviteToWarPacket {

    public InviteToWarPacket(UUID teamUuid) {
        this.teamUuid = teamUuid;
    }

    public final UUID teamUuid;

    public void encode(PacketBuffer buf) {
        buf.writeUUID(teamUuid);
    }

    public static InviteToWarPacket decode(PacketBuffer buf) {
        return new InviteToWarPacket(buf.readUUID());
    }

    public static void handle(InviteToWarPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> InviteToWarHandler.handle(packet, ctx));
        ctx.get().setPacketHandled(true);
    }
}
