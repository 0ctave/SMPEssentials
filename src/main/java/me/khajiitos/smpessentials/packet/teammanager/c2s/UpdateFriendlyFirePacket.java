package me.khajiitos.smpessentials.packet.teammanager.c2s;

import me.khajiitos.smpessentials.packet.teammanager.c2s.handler.UpdateFriendlyFireHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;


import java.util.function.Supplier;

public class UpdateFriendlyFirePacket {
    public final boolean enabled;

    public UpdateFriendlyFirePacket(boolean enabled) {
        this.enabled = enabled;
    }

    public void encode(PacketBuffer buf) {
        buf.writeBoolean(enabled);
    }

    public static UpdateFriendlyFirePacket decode(PacketBuffer buf) {
        return new UpdateFriendlyFirePacket(buf.readBoolean());
    }

    public static void handle(UpdateFriendlyFirePacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> UpdateFriendlyFireHandler.handle(packet, ctx));
        ctx.get().setPacketHandled(true);
    }
}
