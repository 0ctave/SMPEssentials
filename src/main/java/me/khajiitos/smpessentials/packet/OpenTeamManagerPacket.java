package me.khajiitos.smpessentials.packet;

import me.khajiitos.smpessentials.packet.handler.OpenTeamManagerHandler;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;


import java.util.function.Supplier;

public class OpenTeamManagerPacket {
    public final boolean hasTeam;
    public final CompoundNBT data;

    public OpenTeamManagerPacket(boolean hasTeam, CompoundNBT data) {
        this.hasTeam = hasTeam;
        this.data = data;
    }

    public void encode(PacketBuffer buf) {
        buf.writeBoolean(this.hasTeam);
        buf.writeNbt(this.data);
    }

    public static OpenTeamManagerPacket decode(PacketBuffer buf) {
        return new OpenTeamManagerPacket(buf.readBoolean(), buf.readNbt());
    }

    public static void handle(OpenTeamManagerPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> OpenTeamManagerHandler.handle(packet, ctx));
        ctx.get().setPacketHandled(true);
    }
}
