package me.khajiitos.smpessentials.packet.teammanager.s2c;

import me.khajiitos.smpessentials.packet.OpenTeamManagerPacket;
import me.khajiitos.smpessentials.packet.handler.OpenTeamManagerHandler;
import me.khajiitos.smpessentials.packet.teammanager.s2c.handler.UpdateTeamManagerHandler;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;


import java.util.function.Supplier;

public class UpdateTeamManagerPacket {
    public final boolean hasTeam;
    public final CompoundNBT data;

    public UpdateTeamManagerPacket(boolean hasTeam, CompoundNBT data) {
        this.hasTeam = hasTeam;
        this.data = data;
    }

    public void encode(PacketBuffer buf) {
        buf.writeBoolean(this.hasTeam);
        buf.writeNbt(this.data);
    }

    public static UpdateTeamManagerPacket decode(PacketBuffer buf) {
        return new UpdateTeamManagerPacket(buf.readBoolean(), buf.readNbt());
    }

    public static void handle(UpdateTeamManagerPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> UpdateTeamManagerHandler.handle(packet, ctx));
        ctx.get().setPacketHandled(true);
    }
}
