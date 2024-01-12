package me.khajiitos.smpessentials.packet.teammanager.c2s;

import me.khajiitos.smpessentials.data.Team;
import me.khajiitos.smpessentials.packet.teammanager.c2s.handler.UpdateMemberRoleHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;


import java.util.function.Supplier;

public class UpdateMemberRolePacket {
    public final Team.Role role;
    public final String memberName;

    public UpdateMemberRolePacket(String memberName, Team.Role role) {
        this.memberName = memberName;
        this.role = role;
    }

    public void encode(PacketBuffer buf) {
        buf.writeUtf(this.memberName);
        buf.writeEnum(this.role);
    }

    public static UpdateMemberRolePacket decode(PacketBuffer buf) {
        return new UpdateMemberRolePacket(buf.readUtf(), buf.readEnum(Team.Role.class));
    }

    public static void handle(UpdateMemberRolePacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> UpdateMemberRoleHandler.handle(packet, ctx));
        ctx.get().setPacketHandled(true);
    }
}
