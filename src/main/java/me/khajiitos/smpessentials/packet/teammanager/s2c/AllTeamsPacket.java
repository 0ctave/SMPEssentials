package me.khajiitos.smpessentials.packet.teammanager.s2c;

import me.khajiitos.smpessentials.packet.teammanager.s2c.handler.AllTeamsHandler;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;


import java.util.function.Supplier;

public class AllTeamsPacket {

    public final ListNBT data;

    public AllTeamsPacket(ListNBT data) {
        this.data = data;
    }

    public void encode(PacketBuffer buf) {
        CompoundNBT tag = new CompoundNBT();
        tag.put("data", data);
        buf.writeNbt(tag);
    }

    public static AllTeamsPacket decode(PacketBuffer buf) {
        CompoundNBT tag = buf.readNbt();
        return new AllTeamsPacket(tag != null ? tag.getList("data", 10) : new ListNBT());
    }

    public static void handle(AllTeamsPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> AllTeamsHandler.handle(packet, ctx));
        ctx.get().setPacketHandled(true);
    }
}
