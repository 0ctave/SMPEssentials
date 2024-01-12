package me.khajiitos.smpessentials.packet.teammanager.c2s;

import me.khajiitos.smpessentials.packet.teammanager.c2s.handler.AllyInviteResponseHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;


import java.util.UUID;
import java.util.function.Supplier;

public class AllyInviteResponsePacket {
    public final UUID teamUuid;
    public final boolean accept;

    public AllyInviteResponsePacket(UUID teamUuid, boolean accept) {
        this.teamUuid = teamUuid;
        this.accept = accept;
    }

    public void encode(PacketBuffer buf) {
        buf.writeUUID(this.teamUuid);
        buf.writeBoolean(this.accept);
    }

    public static AllyInviteResponsePacket decode(PacketBuffer buf) {
        return new AllyInviteResponsePacket(buf.readUUID(), buf.readBoolean());
    }

    public static void handle(AllyInviteResponsePacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> AllyInviteResponseHandler.handle(packet, ctx));
        ctx.get().setPacketHandled(true);
    }
}
