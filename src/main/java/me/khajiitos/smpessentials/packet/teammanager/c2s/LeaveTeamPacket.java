package me.khajiitos.smpessentials.packet.teammanager.c2s;

import me.khajiitos.smpessentials.packet.teammanager.c2s.handler.LeaveTeamHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;


import java.util.function.Supplier;

public class LeaveTeamPacket {

    public void encode(PacketBuffer buf) {}

    public static LeaveTeamPacket decode(PacketBuffer buf) {
        return new LeaveTeamPacket();
    }

    public static void handle(LeaveTeamPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> LeaveTeamHandler.handle(packet, ctx));
        ctx.get().setPacketHandled(true);
    }
}
