package me.khajiitos.smpessentials.config;

import com.google.gson.*;
import me.khajiitos.smpessentials.SMPEssentials;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;

public class Config {
    private static final File file = new File("config/smpessentials/config.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @ConfigEntry(path = "discord_message")
    public static String discordMessage = "§aDiscord: §bhttps://discord.gg/whatever";

    @ConfigEntry
    public static String rules = "§c- Test rule\n§c- Test rule\n§c- Test rule";

    @ConfigEntry(path = "permissions.staff")
    public static PermissionType permissionsStaff = PermissionType.OP;

    @ConfigEntry(path = "permissions.unstaff")
    public static PermissionType permissionsUnstaff = PermissionType.OP;

    @ConfigEntry(path = "permissions.mute")
    public static PermissionType permissionsMute = PermissionType.STAFF;

    @ConfigEntry(path = "permissions.unmute")
    public static PermissionType permissionsUnmute = PermissionType.STAFF;

    @ConfigEntry(path = "permissions.warn")
    public static PermissionType permissionsWarn = PermissionType.STAFF;

    @ConfigEntry(path = "permissions.kickall")
    public static PermissionType permissionsKickall = PermissionType.OP;

    @ConfigEntry(path = "permissions.ec")
    public static PermissionType permissionsEc = PermissionType.OP;

    @ConfigEntry(path = "permissions.invsee")
    public static PermissionType permissionsInvsee = PermissionType.OP;

    @ConfigEntry(path = "permissions.tempban")
    public static PermissionType permissionsTempban = PermissionType.STAFF;

    @ConfigEntry(path = "permissions.unban")
    public static PermissionType permissionsUnban = PermissionType.STAFF;

    @ConfigEntry(path = "permissions.check")
    public static PermissionType permissionsCheck = PermissionType.STAFF;

    @ConfigEntry(path = "permissions.discord")
    public static PermissionType permissionsDiscord = PermissionType.ALL;

    @ConfigEntry(path = "permissions.rules")
    public static PermissionType permissionsRules = PermissionType.ALL;

    @ConfigEntry(path = "anti_grief.spawn_protection_time")
    public static int spawnProtectionTime = 300;

    @ConfigEntry(path = "anti_grief.noob_protection_time")
    public static int noobProtectionTime = 12000;

    @ConfigEntry(path = "anti_grief.prevent_gorgon_head")
    public static boolean preventGorgonHead = true;

    @ConfigEntry(path = "anti_grief.prevent_chains")
    public static boolean preventChains = true;

    @ConfigEntry(path = "anti_grief.prevent_flying_dragons_in_nether")
    public static boolean preventFlyingDragonsInNether = true;

    @ConfigEntry(path = "anti_grief.prevent_infinite_golden_hearts")
    public static boolean preventInfiniteGoldenHearts = true;

    @ConfigEntry(path = "grief_log.tamed_kills")
    public static boolean logTamedKills = true;

    @ConfigEntry(path = "grief_log.player_kills")
    public static boolean logPlayerKills = true;

    @ConfigEntry(path = "grief_log.inventory_changes")
    public static boolean logInventoryChanges = true;

    @ConfigEntry(path = "grief_log.login_logout")
    public static boolean logLoginLogout = true;

    @ConfigEntry(path = "prevent.blocks_in_combat")
    public static JsonArray preventedBlocksInCombat = new JsonArray();

    @ConfigEntry(path = "prevent.items_in_combat")
    public static JsonArray preventedItemsInCombat = new JsonArray();

    public static void load() {
        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                JsonObject object = GSON.fromJson(reader, JsonObject.class);

                boolean hasMissingFields = false;
                for (Field field : Config.class.getDeclaredFields()) {
                    ConfigEntry annotation = field.getAnnotation(ConfigEntry.class);
                    if (annotation != null) {
                        String path = annotation.path();

                        if (path.isEmpty()) {
                            path = field.getName();
                        }

                        String[] pathDir = path.split("\\.");
                        JsonObject readFrom = object;
                        for (int i = 0; i < pathDir.length; i++) {
                            String name = pathDir[i];
                            if (i == pathDir.length - 1) {
                                JsonElement element = readFrom.get(name);
                                if (element != null) {
                                    try {
                                        field.set(null, fromJson(element, field.getType()));
                                    } catch (IllegalAccessException e) {
                                        SMPEssentials.LOGGER.error("Failed to load config element", e);
                                    }
                                } else {
                                    hasMissingFields = true;
                                }
                            } else {
                                if (object.has(name)) {
                                    readFrom = object.getAsJsonObject(name);
                                } else {
                                    hasMissingFields = true;
                                    break;
                                }
                            }
                        }
                    }
                }

                if (hasMissingFields) {
                    SMPEssentials.LOGGER.info("Found missing fields in the config, resaving it");
                    save();
                }
            } catch (IOException e) {
                SMPEssentials.LOGGER.error("Failed to load config.json", e);
            }
        } else {
            save();
        }
    }

    public static void save() {
        if (!file.getParentFile().exists() && !file.getParentFile().mkdirs()) {
            SMPEssentials.LOGGER.error("Failed to create directories for config.json");
        }

        try (FileWriter writer = new FileWriter(file)) {
            JsonObject object = new JsonObject();

            for (Field field : Config.class.getDeclaredFields()) {
                ConfigEntry annotation = field.getAnnotation(ConfigEntry.class);
                if (annotation != null) {
                    String path = annotation.path();

                    if (path.isEmpty()) {
                        path = field.getName();
                    }

                    String[] pathDir = path.split("\\.");
                    JsonObject toPlaceIn = object;
                    for (int i = 0; i < pathDir.length; i++) {
                        String name = pathDir[i];
                        if (i == pathDir.length - 1) {
                            try {
                                JsonElement element = toJson(field.get(null));
                                if (element != null) {
                                    toPlaceIn.add(name, element);
                                }
                            } catch (IllegalAccessException e) {
                                SMPEssentials.LOGGER.error("Failed to save config element", e);
                            }
                        } else {
                            if (object.has(name)) {
                                toPlaceIn = object.getAsJsonObject(name);
                            } else {
                                JsonObject newObj = new JsonObject();
                                toPlaceIn.add(name, newObj);
                                toPlaceIn = newObj;
                            }
                        }
                    }
                }
            }
            writer.write(GSON.toJson(object));
        } catch (IOException e) {
            SMPEssentials.LOGGER.error("Failed to save config.json", e);
        }
    }

    public static JsonElement toJson(Object object) {
        if (object.getClass() == Integer.class) {
            return new JsonPrimitive((Integer) object);
        } else if (object.getClass() == String.class) {
            return new JsonPrimitive((String) object);
        } else if (object.getClass() == Boolean.class) {
            return new JsonPrimitive((Boolean) object);
        } else if (object instanceof Enum<?>) {
            return new JsonPrimitive(((Enum<?>) object).name().toLowerCase());
        } else if (object.getClass() == JsonArray.class) {
            return (JsonArray) object;
        } else {
            SMPEssentials.LOGGER.error("Failed to save config element - unsupported type " + object.getClass().getSimpleName());
            return null;
        }
    }

    public static Object fromJson(JsonElement element, Class<?> classType) {
        if (classType == int.class) {
            return element.getAsInt();
        } else if (classType == String.class) {
            return element.getAsString();
        } else if (classType == boolean.class) {
            return element.getAsBoolean();
        } else if (classType.isEnum()) {
            String enumName = element.getAsString().toUpperCase();
            try {
                return Enum.valueOf(classType.asSubclass(Enum.class), enumName);
            } catch (IllegalArgumentException e) {
                SMPEssentials.LOGGER.error("Invalid enum constant: " + enumName);
                return null;
            }
        } else if (classType == JsonArray.class) {
            return element.getAsJsonArray();
        } else {
            SMPEssentials.LOGGER.error("Failed to load config element - unsupported type " + classType.getSimpleName());
            return null;
        }
    }
}