package me.khajiitos.smpessentials.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.khajiitos.smpessentials.SMPEssentials;
import me.khajiitos.smpessentials.Utils;
import me.khajiitos.smpessentials.config.Config;
import me.khajiitos.smpessentials.data.SMPData;
import me.khajiitos.smpessentials.manager.PunishmentManager;
import me.khajiitos.smpessentials.manager.StaffManager;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;

import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.TimeArgument;

import net.minecraft.util.text.StringTextComponent;
import net.minecraft.entity.player.ServerPlayerEntity;

import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.UsernameCache;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;

public class StaffCommands {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("staff")
                .requires(Permissions.fromPermissionType(Config.permissionsStaff))
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(StaffCommands::staff)));

        dispatcher.register(Commands.literal("unstaff")
                .requires(Permissions.fromPermissionType(Config.permissionsUnstaff))
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(StaffCommands::unstaff)));

        dispatcher.register(Commands.literal("invsee")
                .requires(Permissions.fromPermissionType(Config.permissionsInvsee))
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(StaffCommands::invsee)));

        dispatcher.register(Commands.literal("ec")
                .requires(Permissions.fromPermissionType(Config.permissionsEc))
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(StaffCommands::ec)));

        dispatcher.register(Commands.literal("kickall")
                .requires(Permissions.fromPermissionType(Config.permissionsKickall))
                .executes(StaffCommands::kickall)
                .then(Commands.argument("reason", StringArgumentType.string())
                        .requires(Permissions.fromPermissionType(Config.permissionsKickall))
                        .executes(StaffCommands::kickall)));

        dispatcher.register(Commands.literal("mute")
                .requires(Permissions.fromPermissionType(Config.permissionsMute))
                .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.argument("time", TimeArgument.time())
                                .then(Commands.argument("reason", StringArgumentType.greedyString())
                                        .executes(StaffCommands::mute)))));

        dispatcher.register(Commands.literal("unmute")
                .requires(Permissions.fromPermissionType(Config.permissionsUnmute))
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(StaffCommands::unmute)));

        dispatcher.register(Commands.literal("tempban")
                .requires(Permissions.fromPermissionType(Config.permissionsTempban))
                .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.argument("time", TimeArgument.time())
                                .then(Commands.argument("reason", StringArgumentType.greedyString())
                                        .executes(StaffCommands::tempban)))));

        dispatcher.register(Commands.literal("unban")
                .requires(Permissions.fromPermissionType(Config.permissionsUnban))
                .then(Commands.argument("player", StringArgumentType.string())
                        .executes(StaffCommands::unban)));

        dispatcher.register(Commands.literal("warn")
                .requires(Permissions.fromPermissionType(Config.permissionsWarn))
                .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.argument("reason", StringArgumentType.greedyString())
                        .executes(StaffCommands::warn))));

        dispatcher.register(Commands.literal("check")
                .requires(Permissions.fromPermissionType(Config.permissionsCheck))
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(StaffCommands::check)));

        dispatcher.register(Commands.literal("discord").requires(Permissions.fromPermissionType(Config.permissionsDiscord)).executes(StaffCommands::discord));
        dispatcher.register(Commands.literal("rules").requires(Permissions.fromPermissionType(Config.permissionsRules)).executes(StaffCommands::rules));
    }

    private static int check(CommandContext<CommandSource> ctx) {
        try {
            ServerPlayerEntity player = EntityArgument.getPlayer(ctx, "player");

            SMPData data = SMPEssentials.getData();

            List<ITextComponent> messages = new ArrayList<>();

            data.punishmentLog.forEach(tag -> {
                if (Objects.equals(tag.getString("for"), player.getUUID().toString())) {

                    DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);
                    LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(tag.getLong("at")), ZoneOffset.systemDefault());

                    IFormattableTextComponent component = new StringTextComponent("[" + dateTime.format(formatter) + "] ").withStyle(TextFormatting.GRAY);

                    component.append(new StringTextComponent("Type: ").withStyle(TextFormatting.GOLD));
                    component.append(new StringTextComponent(StringUtils.capitalize(tag.getString("type"))).withStyle(TextFormatting.YELLOW));

                    component.append(new StringTextComponent(" Reason: ").withStyle(TextFormatting.GOLD));
                    component.append(new StringTextComponent(tag.getString("reason")).withStyle(TextFormatting.YELLOW));

                    String by = tag.getString("by");
                    try {
                        if (!by.equals("console")) {
                            UUID uuid = UUID.fromString(by);
                            by = data.getUsername(uuid);
                        }
                    } catch (IllegalArgumentException ignored) {}

                    component.append(new StringTextComponent(" By: ").withStyle(TextFormatting.GOLD));
                    component.append(new StringTextComponent(by).withStyle(TextFormatting.YELLOW));

                    if (tag.contains("length")) {
                        long length = tag.getLong("length");

                        component.append(new StringTextComponent(" Length: ").withStyle(TextFormatting.GOLD));
                        component.append(new StringTextComponent(Utils.timeToStr(length)).withStyle(TextFormatting.YELLOW));
                    }

                    messages.add(component);

                    // [13 July 2023 6:06 PM GMT] Type: Warn, Reason: whatever, By: Someone
                }
            });

            if (!messages.isEmpty()) {
                ctx.getSource().sendSuccess(new StringTextComponent("Punishments received by " + player.getScoreboardName() + ":").withStyle(TextFormatting.GOLD), false);
                messages.forEach(component -> ctx.getSource().sendSuccess(component, false));
            } else {
                ctx.getSource().sendSuccess(new StringTextComponent("This player has no previous punishments!").withStyle(TextFormatting.RED), false);
            }
        } catch (CommandSyntaxException ignored) {}
        return 0;
    }

    private static int warn(CommandContext<CommandSource> ctx) {
        ServerPlayerEntity executor;

        try {
            executor = ctx.getSource().getPlayerOrException();
        } catch (CommandSyntaxException e) {
            executor = null;
        }

        try {
            ServerPlayerEntity player = EntityArgument.getPlayer(ctx, "player");
            String reason = ctx.getArgument("reason", String.class);

            PunishmentManager.warnPlayer(player, executor, reason);

            ctx.getSource().sendSuccess(new StringTextComponent(String.format("Warned %s for reason: %s", player.getScoreboardName(), reason)).withStyle(TextFormatting.GREEN), true);
        } catch (CommandSyntaxException ignored) {}
        return 0;
    }

    private static int ec(CommandContext<CommandSource> ctx) {
        try {
            PlayerEntity executor = ctx.getSource().getPlayerOrException();
            ServerPlayerEntity player = EntityArgument.getPlayer(ctx, "player");
            executor.openMenu(new SimpleNamedContainerProvider((a, b, c) -> ChestContainer.threeRows(27, b, player.getEnderChestInventory()), new StringTextComponent("Ender Chest: " + player.getScoreboardName())));
        } catch (CommandSyntaxException ignored) {}
        return 0;
    }

    private static int invsee(CommandContext<CommandSource> ctx) {
        try {
            PlayerEntity executor = ctx.getSource().getPlayerOrException();
            ServerPlayerEntity player = EntityArgument.getPlayer(ctx, "player");

            executor.openMenu(new SimpleNamedContainerProvider((menuId, inventory, player1) -> new ChestContainer(ContainerType.GENERIC_9x4, menuId, player1.inventory, player.inventory, 4) {
                @Override
                public boolean stillValid(@NotNull PlayerEntity p) {
                    return true;
                }
            }, new StringTextComponent("Inventory: " + player.getScoreboardName())));
        } catch (CommandSyntaxException ignored) {}
        return 0;
    }

    private static int kickall(CommandContext<CommandSource> ctx) {
        ServerPlayerEntity executor;
        try {
            executor = ctx.getSource().getPlayerOrException();
        } catch (CommandSyntaxException e)  {
            executor = null;
        }
        int kickedPlayers = 0;
        try {
            String reason = ctx.getArgument("reason", String.class);
            for (ServerPlayerEntity player : ctx.getSource().getServer().getPlayerList().getPlayers()) {
                if (player == executor) {
                    continue;
                }
                player.connection.disconnect(new StringTextComponent(reason).withStyle(TextFormatting.RED));
                kickedPlayers++;
            }
        } catch (IllegalArgumentException e) {
            for (ServerPlayerEntity player : ctx.getSource().getServer().getPlayerList().getPlayers()) {
                if (player == executor) {
                    continue;
                }
                player.connection.disconnect(new StringTextComponent("You have been kicked!").withStyle(TextFormatting.RED));
                kickedPlayers++;
            }
        }

        if (kickedPlayers > 0) {
            ctx.getSource().sendSuccess(new StringTextComponent(String.format("Kicked %d players!", kickedPlayers)).withStyle(TextFormatting.GREEN), true);
        } else {
            ctx.getSource().sendSuccess(new StringTextComponent("There was no one to kick!").withStyle(TextFormatting.RED), false);
        }
        return 0;
    }

    private static int rules(CommandContext<CommandSource> ctx) {
        ctx.getSource().sendSuccess(new StringTextComponent(Config.rules), false);
        return 0;
    }

    private static int discord(CommandContext<CommandSource> ctx) {
        ctx.getSource().sendSuccess(ForgeHooks.newChatWithLinks(Config.discordMessage), false);
        return 0;
    }

    private static int unstaff(CommandContext<CommandSource> ctx) {
        try {
            ServerPlayerEntity player = EntityArgument.getPlayer(ctx, "player");
            if (!StaffManager.isStaff(player.getUUID())) {
                ctx.getSource().sendFailure(new StringTextComponent("This player is not staff!").withStyle(TextFormatting.RED));
            } else {
                StaffManager.removeStaff(player.getUUID());
                ctx.getSource().sendSuccess(new StringTextComponent(String.format("Removed %s from staff!", player.getScoreboardName())).withStyle(TextFormatting.GREEN), true);
            }
        } catch (CommandSyntaxException ignored) {}
        return 0;
    }

    private static int staff(CommandContext<CommandSource> ctx) {
        try {
            ServerPlayerEntity player = EntityArgument.getPlayer(ctx, "player");
            if (StaffManager.isStaff(player.getUUID())) {
                ctx.getSource().sendFailure(new StringTextComponent("This player is already staff!").withStyle(TextFormatting.RED));
            } else {
                StaffManager.addStaff(player.getUUID());
                ctx.getSource().sendSuccess(new StringTextComponent(String.format("Made %s staff!", player.getScoreboardName())).withStyle(TextFormatting.GREEN), true);
            }
        } catch (CommandSyntaxException ignored) {}
        return 0;
    }

    private static int mute(CommandContext<CommandSource> ctx) {
        ServerPlayerEntity executor;

        try {
            executor = ctx.getSource().getPlayerOrException();
        } catch (CommandSyntaxException e) {
            executor = null;
        }

        try {
            ServerPlayerEntity player = EntityArgument.getPlayer(ctx, "player");
            long time = ctx.getArgument("time", Integer.class) * 50L;
            String reason = ctx.getArgument("reason", String.class);

            PunishmentManager.mutePlayer(player, executor, time, reason);

            DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);
            LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(System.currentTimeMillis() + time), ZoneOffset.systemDefault());

            ctx.getSource().sendSuccess(new StringTextComponent(String.format("Muted %s until %s for reason: %s", player.getScoreboardName(), dateTime.format(formatter), reason)).withStyle(TextFormatting.GREEN), true);

        } catch (CommandSyntaxException ignored) {}
        return 0;
    }

    private static int unmute(CommandContext<CommandSource> ctx) {
        try {
            ServerPlayerEntity player = EntityArgument.getPlayer(ctx, "player");
            if (PunishmentManager.unmutePlayer(player)) {
                ctx.getSource().sendSuccess(new StringTextComponent(String.format("Unmuted %s!", player.getScoreboardName())).withStyle(TextFormatting.GREEN), true);
            } else {
                ctx.getSource().sendFailure(new StringTextComponent(String.format("%s is not muted!", player.getScoreboardName())).withStyle(TextFormatting.RED));
            }
        } catch (CommandSyntaxException ignored) {}
        return 0;
    }

    private static int tempban(CommandContext<CommandSource> ctx) {
        ServerPlayerEntity executor;

        try {
            executor = ctx.getSource().getPlayerOrException();
        } catch (CommandSyntaxException e) {
            executor = null;
        }

        try {
            ServerPlayerEntity player = EntityArgument.getPlayer(ctx, "player");
            long time = ctx.getArgument("time", Integer.class) * 50L;
            String reason = ctx.getArgument("reason", String.class);

            PunishmentManager.banPlayer(player, executor, time, reason);

            DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);
            LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(System.currentTimeMillis() + time), ZoneOffset.systemDefault());

            ctx.getSource().sendSuccess(new StringTextComponent(String.format("Temporarily banned %s until %s for reason: %s", player.getScoreboardName(), dateTime.format(formatter), reason)).withStyle(TextFormatting.GREEN), true);
        } catch (CommandSyntaxException ignored) {}
        return 0;
    }

    private static int unban(CommandContext<CommandSource> ctx) {
        String playerName = StringArgumentType.getString(ctx, "player");

        for (Map.Entry<UUID, String> entry : UsernameCache.getMap().entrySet()) {
            if (entry.getValue().equals(playerName)) {
                if (PunishmentManager.unbanPlayer(entry.getKey())) {
                    ctx.getSource().sendSuccess(new StringTextComponent(String.format("Unbanned %s!", playerName)).withStyle(TextFormatting.GREEN), true);
                } else {
                    ctx.getSource().sendFailure(new StringTextComponent(String.format("%s is not banned!", playerName)).withStyle(TextFormatting.RED));
                }
                return 0;
            }
        }

        ctx.getSource().sendFailure(new StringTextComponent("This player doesn't exist!").withStyle(TextFormatting.RED));
        return 0;
    }
}
