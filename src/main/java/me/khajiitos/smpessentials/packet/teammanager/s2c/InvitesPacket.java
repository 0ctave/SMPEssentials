package me.khajiitos.smpessentials.packet.teammanager.s2c;

import me.khajiitos.smpessentials.packet.teammanager.s2c.handler.InvitesHandler;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;


import java.util.function.Supplier;

public class InvitesPacket {
    public final ListNBT invites;

    public InvitesPacket(ListNBT invites) {
        this.invites = invites;
    }

    public void encode(PacketBuffer buf) {
        CompoundNBT tag = new CompoundNBT();
        tag.put("invites", invites);
        buf.writeNbt(tag);
    }

    public static InvitesPacket decode(PacketBuffer buf) {
        CompoundNBT tag = buf.readNbt();
        return new InvitesPacket(tag != null ? tag.getList("invites", 10) : new ListNBT());
    }

    public static void handle(InvitesPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> InvitesHandler.handle(packet, ctx));
        ctx.get().setPacketHandled(true);
    }
}