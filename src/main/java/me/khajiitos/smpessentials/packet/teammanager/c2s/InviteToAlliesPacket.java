package me.khajiitos.smpessentials.packet.teammanager.c2s;

import me.khajiitos.smpessentials.packet.teammanager.c2s.handler.InviteToAlliesHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;


import java.util.UUID;
import java.util.function.Supplier;

public class InviteToAlliesPacket {
    public InviteToAlliesPacket(UUID teamUuid) {
        this.teamUuid = teamUuid;
    }

    public final UUID teamUuid;

    public void encode(PacketBuffer buf) {
        buf.writeUUID(teamUuid);
    }

    public static InviteToAlliesPacket decode(PacketBuffer buf) {
        return new InviteToAlliesPacket(buf.readUUID());
    }

    public static void handle(InviteToAlliesPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> InviteToAlliesHandler.handle(packet, ctx));
        ctx.get().setPacketHandled(true);
    }
}
