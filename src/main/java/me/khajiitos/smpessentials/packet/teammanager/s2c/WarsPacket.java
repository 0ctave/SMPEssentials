package me.khajiitos.smpessentials.packet.teammanager.s2c;

import me.khajiitos.smpessentials.packet.teammanager.s2c.handler.WarsHandler;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;


import java.util.function.Supplier;

public class WarsPacket {

    public final CompoundNBT data;

    public WarsPacket(CompoundNBT data) {
        this.data = data;
    }

    public void encode(PacketBuffer buf) {
        buf.writeNbt(data);
    }

    public static WarsPacket decode(PacketBuffer buf) {
        return new WarsPacket(buf.readNbt());
    }

    public static void handle(WarsPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> WarsHandler.handle(packet, ctx));
        ctx.get().setPacketHandled(true);
    }
}
