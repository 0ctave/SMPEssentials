package me.khajiitos.smpessentials.packet;

import me.khajiitos.smpessentials.packet.handler.RulesResultHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;


import java.util.function.Supplier;

public class RulesResultPacket {
    private final boolean accept;

    public boolean accepted() {
        return this.accept;
    }

    public RulesResultPacket(boolean accept) {
        this.accept = accept;
    }

    public void encode(PacketBuffer buf) {
        buf.writeBoolean(this.accept);
    }

    public static RulesResultPacket decode(PacketBuffer buf) {
        return new RulesResultPacket(buf.readBoolean());
    }

    public static void handle(RulesResultPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> RulesResultHandler.handle(packet, ctx));
        ctx.get().setPacketHandled(true);
    }
}
