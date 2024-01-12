package me.khajiitos.smpessentials.packet;

import me.khajiitos.smpessentials.packet.handler.RulesHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;


import java.util.function.Supplier;

public class RulesPacket {
    private final String rules;

    public RulesPacket(String rules) {
        this.rules = rules;
    }

    public String getRules() {
        return this.rules;
    }

    public void encode(PacketBuffer buf) {
        buf.writeUtf(this.rules);
    }

    public static RulesPacket decode(PacketBuffer buf) {
        return new RulesPacket(buf.readUtf());
    }

    public static void handle(RulesPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> RulesHandler.handle(packet, ctx));
        ctx.get().setPacketHandled(true);
    }
}
