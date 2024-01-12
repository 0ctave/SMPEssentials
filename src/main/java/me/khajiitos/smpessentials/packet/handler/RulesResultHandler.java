package me.khajiitos.smpessentials.packet.handler;


import me.khajiitos.smpessentials.Packets;
import me.khajiitos.smpessentials.SMPEssentials;
import me.khajiitos.smpessentials.config.Config;
import me.khajiitos.smpessentials.packet.CloseRulesPacket;
import me.khajiitos.smpessentials.packet.RulesResultPacket;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;


import java.util.function.Supplier;

public class RulesResultHandler {

    public static void handle(RulesResultPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ServerPlayerEntity sender = ctx.get().getSender();

        if (sender != null) {
            if (packet.accepted()) {
                SMPEssentials.getData().getOrCreate(sender.getUUID()).acceptedRulesHash = Config.rules.hashCode();
                SMPEssentials.getData().setDirty();
                Packets.INSTANCE.send(PacketDistributor.PLAYER.with(() -> sender), new CloseRulesPacket());
            } else {
                sender.connection.disconnect(new StringTextComponent("You need to accept the rules to play here!").withStyle(TextFormatting.RED));
            }
        }
    }
}
