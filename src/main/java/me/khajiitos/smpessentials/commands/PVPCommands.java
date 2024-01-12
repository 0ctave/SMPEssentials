package me.khajiitos.smpessentials.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.khajiitos.smpessentials.manager.PVPManager;
import net.minecraft.command.Commands;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.command.CommandSource;

import net.minecraft.util.text.StringTextComponent;
import net.minecraft.entity.player.ServerPlayerEntity;


public class PVPCommands {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("pvp")
                .then(Commands.literal("on")
                        .executes(PVPCommands::pvpOn))
                .then(Commands.literal("off")
                        .executes(PVPCommands::pvpOff)));
        
        dispatcher.register(Commands.literal("pvpstatus")
                .executes(PVPCommands::pvpStatus));
    }

    private static int pvpStatus(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayerOrException();
        if (PVPManager.isInCombat(player)) {
            player.sendMessage(new StringTextComponent("You are in combat!").withStyle(TextFormatting.RED), player.getUUID());
        } else {
            player.sendMessage(new StringTextComponent("You are not in combat!").withStyle(TextFormatting.GREEN), player.getUUID());
        }
        return 0;
    }

    private static int pvpOff(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayerOrException();

        if (PVPManager.isInCombat(player)) {
            player.sendMessage(new StringTextComponent("You can't turn off PvP while in combat!").withStyle(TextFormatting.RED), player.getUUID());
            return 1;
        }

        if (!PVPManager.hasPvpEnabled(player)) {
            player.sendMessage(new StringTextComponent("You already have PvP disabled!").withStyle(TextFormatting.RED), player.getUUID());
            return 1;
        }

        if (!PVPManager.canSwitchPvpStatus(player)) {
            long secondsLeft = PVPManager.canSwitchPvpStatusIn(player) / 1000;
            player.sendMessage(new StringTextComponent(String.format("You have to wait %ds before changing your PvP status!", secondsLeft)).withStyle(TextFormatting.RED), player.getUUID());
            return 1;
        }

        PVPManager.setPvpEnabled(player, false);
        player.sendMessage(new StringTextComponent("PvP is now ")
                .withStyle(TextFormatting.GRAY)
                .append(new StringTextComponent("disabled")
                        .withStyle(TextFormatting.RED))
                .append(new StringTextComponent("!")
                        .withStyle(TextFormatting.GRAY)), player.getUUID());

        return 0;
    }

    private static int pvpOn(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayerOrException();

        if (PVPManager.hasPvpEnabled(player)) {
            player.sendMessage(new StringTextComponent("You already have PvP enabled!").withStyle(TextFormatting.RED), player.getUUID());
            return 1;
        }

        if (!PVPManager.canSwitchPvpStatus(player)) {
            long secondsLeft = PVPManager.canSwitchPvpStatusIn(player) / 1000L;
            player.sendMessage(new StringTextComponent(String.format("You have to wait %ds before changing your PvP status!", secondsLeft)).withStyle(TextFormatting.RED), player.getUUID());
            return 1;
        }

        PVPManager.setPvpEnabled(player, true);
        player.sendMessage(new StringTextComponent("PvP is now ")
                .withStyle(TextFormatting.GRAY)
                .append(new StringTextComponent("enabled")
                        .withStyle(TextFormatting.GREEN))
                .append(new StringTextComponent("!")
                        .withStyle(TextFormatting.GRAY)), player.getUUID());
        return 0;
    }
}
