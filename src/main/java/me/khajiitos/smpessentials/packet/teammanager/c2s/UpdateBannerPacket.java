package me.khajiitos.smpessentials.packet.teammanager.c2s;

import me.khajiitos.smpessentials.packet.teammanager.c2s.handler.UpdateBannerHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;


import java.util.function.Supplier;

public class UpdateBannerPacket {

    public void encode(PacketBuffer buf) {}

    public static UpdateBannerPacket decode(PacketBuffer buf) {
        return new UpdateBannerPacket();
    }

    public static void handle(UpdateBannerPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> UpdateBannerHandler.handle(packet, ctx));
        ctx.get().setPacketHandled(true);
    }
}
