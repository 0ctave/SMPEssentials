package me.khajiitos.smpessentials.packet.teammanager.c2s;

import me.khajiitos.smpessentials.packet.teammanager.c2s.handler.WarInviteResponseHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;


import java.util.UUID;
import java.util.function.Supplier;

public class WarInviteResponsePacket {

    public final UUID teamUuid;
    public final boolean accept;

    public WarInviteResponsePacket(UUID teamUuid, boolean accept) {
        this.teamUuid = teamUuid;
        this.accept = accept;
    }

    public void encode(PacketBuffer buf) {
        buf.writeUUID(this.teamUuid);
        buf.writeBoolean(this.accept);
    }

    public static WarInviteResponsePacket decode(PacketBuffer buf) {
        return new WarInviteResponsePacket(buf.readUUID(), buf.readBoolean());
    }

    public static void handle(WarInviteResponsePacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> WarInviteResponseHandler.handle(packet, ctx));
        ctx.get().setPacketHandled(true);
    }
}
