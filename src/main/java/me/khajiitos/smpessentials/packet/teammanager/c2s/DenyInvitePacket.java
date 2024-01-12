package me.khajiitos.smpessentials.packet.teammanager.c2s;

import me.khajiitos.smpessentials.packet.teammanager.c2s.handler.DenyInviteHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;


import java.util.UUID;
import java.util.function.Supplier;

public class DenyInvitePacket {

    public final UUID teamUUID;

    public DenyInvitePacket(UUID teamUUID) {
        this.teamUUID = teamUUID;
    }

    public void encode(PacketBuffer buf) {
        buf.writeUUID(this.teamUUID);
    }

    public static DenyInvitePacket decode(PacketBuffer buf) {
        return new DenyInvitePacket(buf.readUUID());
    }

    public static void handle(DenyInvitePacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> DenyInviteHandler.handle(packet, ctx));
        ctx.get().setPacketHandled(true);
    }
}
