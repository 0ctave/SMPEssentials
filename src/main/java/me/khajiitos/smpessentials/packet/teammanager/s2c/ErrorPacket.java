package me.khajiitos.smpessentials.packet.teammanager.s2c;

import me.khajiitos.smpessentials.packet.teammanager.s2c.handler.ErrorHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;


import java.util.function.Supplier;

public class ErrorPacket {

    public final String error;

    public ErrorPacket(String error) {
        this.error = error;
    }

    public void encode(PacketBuffer buf) {
        buf.writeUtf(error);
    }

    public static ErrorPacket decode(PacketBuffer buf) {
        return new ErrorPacket(buf.readUtf());
    }

    public static void handle(ErrorPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> ErrorHandler.handle(packet, ctx));
        ctx.get().setPacketHandled(true);
    }
}
