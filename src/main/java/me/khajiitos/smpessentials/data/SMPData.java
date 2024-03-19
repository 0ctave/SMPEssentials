package me.khajiitos.smpessentials.data;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class SMPData extends WorldSavedData {
    private final HashMap<UUID, PlayerDataInstance> players = new HashMap<>();
    private final HashMap<UUID, Team> teams = new HashMap<>();
    private final HashMap<UUID, War> wars = new HashMap<>();
    public final List<CompoundNBT> punishmentLog = new ArrayList<>();

    public SMPData() {
        super("smpdata");
    }

    @Override
    public @Nonnull CompoundNBT save(CompoundNBT nbt) {
        CompoundNBT playersTag = new CompoundNBT();
        players.forEach((uuid, data) -> {
            if (uuid != null) {
                playersTag.put(uuid.toString(), data.save());
            }
        });
        nbt.put("players", playersTag);

        CompoundNBT teamsTag = new CompoundNBT();
        teams.forEach((uuid, team) -> {
            if (uuid != null) {
                teamsTag.put(uuid.toString(), team.save());
            }
        });
        nbt.put("teams", teamsTag);

        CompoundNBT warsTag = new CompoundNBT();
        wars.forEach((uuid, war) -> {
            if (uuid != null) {
                warsTag.put(uuid.toString(), war.save());
            }
        });
        nbt.put("wars", warsTag);

        ListNBT punishmentLogTag = new ListNBT();
        punishmentLogTag.addAll(punishmentLog);
        nbt.put("punishment_log", punishmentLogTag);
        return nbt;
    }

    public PlayerDataInstance getOrCreate(UUID uuid) {
        return players.computeIfAbsent(uuid, (u) -> new PlayerDataInstance());
    }

    public PlayerDataInstance get(UUID uuid) {
        return players.get(uuid);
    }

    public HashMap<UUID, Team> getTeams() {
        return teams;
    }

    public HashMap<UUID, War> getWars() {
        return wars;
    }

    @Override
    public void load(CompoundNBT nbt) {

        CompoundNBT playersNbt = nbt.getCompound("players");
        playersNbt.getAllKeys().forEach(uuidStr -> {
            try {
                UUID uuid = UUID.fromString(uuidStr);
                this.players.put(uuid, PlayerDataInstance.load(playersNbt.getCompound(uuidStr)));
            } catch (IllegalArgumentException ignored) {}
        });

        CompoundNBT teamsNbt = nbt.getCompound("teams");
        teamsNbt.getAllKeys().forEach(uuidStr -> {
            try {
                UUID uuid = UUID.fromString(uuidStr);
                this.teams.put(uuid, Team.load(teamsNbt.getCompound(uuidStr)));
            } catch (IllegalArgumentException ignored) {}
        });

        CompoundNBT warsNbt = nbt.getCompound("wars");
        warsNbt.getAllKeys().forEach(uuidStr -> {
            try {
                UUID uuid = UUID.fromString(uuidStr);
                this.wars.put(uuid, War.load(uuid, warsNbt.getCompound(uuidStr)));
            } catch (IllegalArgumentException ignored) {}
        });

        ListNBT punishmentLogNbt = nbt.getList("punishment_log", Constants.NBT.TAG_COMPOUND);

        punishmentLogNbt.forEach(tag -> {
            CompoundNBT compoundTag = (CompoundNBT) tag;
            this.punishmentLog.add(compoundTag);
        });
    }

    public String getUsername(UUID uuid) {
        PlayerDataInstance dataInstance = get(uuid);
        return dataInstance != null && dataInstance.lastUsername != null ? dataInstance.lastUsername : uuid.toString();
    }
}
