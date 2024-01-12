package me.khajiitos.smpessentials.packet.teammanager.s2c;

import me.khajiitos.smpessentials.packet.teammanager.s2c.handler.AlliesHandler;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;


import java.util.function.Supplier;

public class AlliesPacket {
    public final CompoundNBT data;

    public AlliesPacket(CompoundNBT data) {
        this.data = data;
    }

    public void encode(PacketBuffer buf) {
        buf.writeNbt(data);
    }

    public static AlliesPacket decode(PacketBuffer buf) {
        return new AlliesPacket(buf.readNbt());
    }

    public static void handle(AlliesPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> AlliesHandler.handle(packet, ctx));
        ctx.get().setPacketHandled(true);
    }
}
