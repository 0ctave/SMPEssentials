package me.khajiitos.smpessentials.packet.teammanager.s2c;

import me.khajiitos.smpessentials.packet.teammanager.s2c.handler.PlayerInvitedHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;


import java.util.function.Supplier;

public class PlayerInvitedPacket {

    public void encode(PacketBuffer buf) {}

    public static PlayerInvitedPacket decode(PacketBuffer buf) {
        return new PlayerInvitedPacket();
    }

    public static void handle(PlayerInvitedPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> PlayerInvitedHandler.handle(packet, ctx));
        ctx.get().setPacketHandled(true);
    }
}
