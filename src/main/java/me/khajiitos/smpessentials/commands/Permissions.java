package me.khajiitos.smpessentials.commands;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.khajiitos.smpessentials.config.PermissionType;
import me.khajiitos.smpessentials.manager.StaffManager;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;

import java.util.function.Predicate;

public class Permissions {

    public static boolean isStaff(CommandSource stack) {
        try {
            return isStaff(stack.getPlayerOrException());
        } catch (CommandSyntaxException ignored) {}
        return false;
    }

    public static boolean isOp(CommandSource stack) {
        return stack.hasPermission(4);
    }

    public static boolean isStaffOrOp(CommandSource stack) {
        return isOp(stack) || isStaff(stack);
    }

    public static boolean isStaff(ServerPlayerEntity serverPlayer) {
        return StaffManager.isStaff(serverPlayer.getUUID());
    }

    public static boolean isOp(ServerPlayerEntity serverPlayer) {
        return serverPlayer.hasPermissions(4);
    }

    public static boolean isStaffOrOp(ServerPlayerEntity serverPlayer) {
        return isOp(serverPlayer) || isStaff(serverPlayer);
    }

    public static Predicate<CommandSource> fromPermissionType(PermissionType type) {
        if (type == PermissionType.OP) {
            return Permissions::isOp;
        } else if (type == PermissionType.STAFF) {
            return Permissions::isStaffOrOp;
        }
        return (stack) -> true;
    }
}
