package me.khajiitos.smpessentials.packet.teammanager.c2s;

import me.khajiitos.smpessentials.packet.teammanager.c2s.handler.KickMemberHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;


import java.util.function.Supplier;

public class KickMemberPacket {
    public final String member;

    public KickMemberPacket(String member) {
        this.member = member;
    }

    public void encode(PacketBuffer buf) {
        buf.writeUtf(this.member);
    }

    public static KickMemberPacket decode(PacketBuffer buf) {
        return new KickMemberPacket(buf.readUtf());
    }

    public static void handle(KickMemberPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> KickMemberHandler.handle(packet, ctx));
        ctx.get().setPacketHandled(true);
    }
}
