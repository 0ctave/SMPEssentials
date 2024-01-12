package me.khajiitos.smpessentials.packet.teammanager.s2c;

import me.khajiitos.smpessentials.packet.teammanager.s2c.handler.OpenTeamInfoHandler;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;


import java.util.function.Supplier;

public class OpenTeamInfoPacket {

    public OpenTeamInfoPacket(CompoundNBT data) {
        this.data = data;
    }

    public final CompoundNBT data;

    public void encode(PacketBuffer buf) {
        buf.writeNbt(this.data);
    }

    public static OpenTeamInfoPacket decode(PacketBuffer buf) {
        return new OpenTeamInfoPacket(buf.readNbt());
    }

    public static void handle(OpenTeamInfoPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> OpenTeamInfoHandler.handle(packet, ctx));
        ctx.get().setPacketHandled(true);
    }
}
