package me.khajiitos.smpessentials.packet.teammanager.c2s;

import me.khajiitos.smpessentials.packet.teammanager.c2s.handler.InviteHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;


import java.util.function.Supplier;

public class InvitePacket {

    public InvitePacket(String username) {
        this.username = username;
    }

    public final String username;

    public void encode(PacketBuffer buf) {
        buf.writeUtf(username);
    }

    public static InvitePacket decode(PacketBuffer buf) {
        return new InvitePacket(buf.readUtf());
    }

    public static void handle(InvitePacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> InviteHandler.handle(packet, ctx));
        ctx.get().setPacketHandled(true);
    }
}
