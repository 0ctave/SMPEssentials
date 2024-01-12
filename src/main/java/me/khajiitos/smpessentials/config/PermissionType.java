package me.khajiitos.smpessentials.config;

public enum PermissionType {
    OP(2),
    STAFF(1),
    ALL(0);

    public final int level;

    PermissionType(int level) {
        this.level = level;
    }
}