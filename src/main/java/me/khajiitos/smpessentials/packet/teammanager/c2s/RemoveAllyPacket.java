package me.khajiitos.smpessentials.packet.teammanager.c2s;

import me.khajiitos.smpessentials.packet.teammanager.c2s.handler.RemoveAllyHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;


import java.util.UUID;
import java.util.function.Supplier;

public class RemoveAllyPacket {

    public final UUID team;

    public RemoveAllyPacket(UUID team) {
        this.team = team;
    }

    public void encode(PacketBuffer buf) {
        buf.writeUUID(team);
    }

    public static RemoveAllyPacket decode(PacketBuffer buf) {
        return new RemoveAllyPacket(buf.readUUID());
    }

    public static void handle(RemoveAllyPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> RemoveAllyHandler.handle(packet, ctx));
        ctx.get().setPacketHandled(true);
    }
}
