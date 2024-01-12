package me.khajiitos.smpessentials.packet.teammanager.s2c;

import me.khajiitos.smpessentials.packet.teammanager.s2c.handler.RefreshCurrentTabHandler;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;


import java.util.function.Supplier;

public class RefreshCurrentTabPacket {
    public final CompoundNBT newData;

    public RefreshCurrentTabPacket(CompoundNBT newData) {
        this.newData = newData;
    }

    public void encode(PacketBuffer buf) {
        buf.writeNbt(this.newData);
    }

    public static RefreshCurrentTabPacket decode(PacketBuffer buf) {
        return new RefreshCurrentTabPacket(buf.readNbt());
    }

    public static void handle(RefreshCurrentTabPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> RefreshCurrentTabHandler.handle(packet, ctx));
        ctx.get().setPacketHandled(true);
    }
}
