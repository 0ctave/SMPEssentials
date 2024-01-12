package me.khajiitos.smpessentials.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.khajiitos.smpessentials.data.Team;
import me.khajiitos.smpessentials.manager.TeamManager;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.entity.player.ServerPlayerEntity;

public class TeamCommands {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("teamchat")
                .then(Commands.argument("message", StringArgumentType.greedyString())
                        .executes(TeamCommands::teamChat)));
    }

    private static int teamChat(CommandContext<CommandSource> ctx) {
        try {
            ServerPlayerEntity player = ctx.getSource().getPlayerOrException();
            Team team = TeamManager.getTeam(player);

            if (team == null) {
                ctx.getSource().sendFailure(new StringTextComponent("You don't have a team!").withStyle(TextFormatting.RED));
            } else {
                String message = StringArgumentType.getString(ctx, "message");
                TextComponent messageComponent = new StringTextComponent(String.format("§2[%s] §e%s: §6%s", team.tag, player.getScoreboardName(), message));
                ctx.getSource().getServer().sendMessage(messageComponent, player.getUUID());
                for (ServerPlayerEntity onlinePlayer : ctx.getSource().getServer().getPlayerList().getPlayers()) {
                    Team onlinePlayerTeam = TeamManager.getTeam(onlinePlayer);

                    if (team == onlinePlayerTeam) {
                        onlinePlayer.sendMessage(messageComponent, ChatType.CHAT, player.getUUID());
                    }
                }
            }
        } catch (CommandSyntaxException ignored) {}
        return 0;
    }
}
