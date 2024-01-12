package me.khajiitos.smpessentials.packet.teammanager.c2s;

import me.khajiitos.smpessentials.packet.teammanager.c2s.handler.AcceptInviteHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;


import java.util.UUID;
import java.util.function.Supplier;

public class AcceptInvitePacket {

    public final UUID teamUUID;

    public AcceptInvitePacket(UUID teamUUID) {
        this.teamUUID = teamUUID;
    }

    public void encode(PacketBuffer buf) {
        buf.writeUUID(this.teamUUID);
    }

    public static AcceptInvitePacket decode(PacketBuffer buf) {
        return new AcceptInvitePacket(buf.readUUID());
    }

    public static void handle(AcceptInvitePacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> AcceptInviteHandler.handle(packet, ctx));
        ctx.get().setPacketHandled(true);
    }
}
