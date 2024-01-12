package me.khajiitos.smpessentials.packet.teammanager.c2s;

import me.khajiitos.smpessentials.packet.teammanager.c2s.handler.CreateTeamHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;


import java.util.function.Supplier;

public class CreateTeamPacket {
    private final String name, tag;

    public CreateTeamPacket(String name, String tag) {
        this.name = name;
        this.tag = tag;
    }

    public void encode(PacketBuffer buf) {
        buf.writeUtf(this.name, 32);
        buf.writeUtf(this.tag, 4);
    }

    public String getName() {
        return name;
    }

    public String getTag() {
        return tag;
    }

    public static CreateTeamPacket decode(PacketBuffer buf) {
        return new CreateTeamPacket(buf.readUtf(32), buf.readUtf(4));
    }

    public static void handle(CreateTeamPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> CreateTeamHandler.handle(packet, ctx));
        ctx.get().setPacketHandled(true);
    }
}
