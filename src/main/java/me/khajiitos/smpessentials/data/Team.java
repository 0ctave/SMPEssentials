package me.khajiitos.smpessentials.data;

import joptsimple.internal.AbbreviationMap;
import me.khajiitos.smpessentials.SMPEssentials;
import me.khajiitos.smpessentials.manager.TeamManager;
import net.blay09.mods.hardcorerevival.HardcoreRevival;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.UsernameCache;
import net.minecraftforge.common.util.Constants;

import java.util.*;

public class Team {


    public enum Role {
        MEMBER("Member"),
        TEAM_MANAGER("Team Manager"),
        TEAM_LEADER("Team Leader");

        public final String niceName;

        Role(String niceName) {
            this.niceName = niceName;
        }
    }

    public String name;
    public String tag;
    public CompoundNBT banner; // Color, Patterns
    public HashMap<UUID, Role> members = new HashMap<>();
    public List<UUID> allies = new ArrayList<>();
    //public Map<UUID, Boolean> warInvites = new HashMap<>();
    public Map<UUID, Boolean> wars = new HashMap<>();
    public boolean friendlyFire = false;

    public CompoundNBT save() {
        CompoundNBT tag = new CompoundNBT();

        tag.putString("name", this.name);
        tag.putString("tag", this.tag);
        tag.putBoolean("friendlyFire", this.friendlyFire);

        if (this.banner != null) {
            tag.put("banner", this.banner);
        }

        CompoundNBT membersTag = new CompoundNBT();
        members.forEach((uuid, role) -> membersTag.putInt(uuid.toString(), role.ordinal()));
        tag.put("members", membersTag);

        ListNBT alliesTag = new ListNBT();
        allies.forEach(uuid -> alliesTag.add(StringNBT.valueOf(uuid.toString())));
        tag.put("allies", alliesTag);

        CompoundNBT warsTag = new CompoundNBT();
        wars.forEach((uuid, askedPeace) -> warsTag.putBoolean(uuid.toString(), askedPeace));
        tag.put("wars", warsTag);

        return tag;
    }

    public CompoundNBT toNbtForGui() {
        // Differences:
        // Usernames instead of UUIDs
        // instead of member -> roleId, it's member -> {role: roleId, kills: kills, deaths: deaths}
        CompoundNBT tag = new CompoundNBT();

        UUID teamUuid = TeamManager.getTeamUuid(this);
        if (teamUuid != null) {
            tag.putUUID("uuid", teamUuid);
        }

        tag.putString("name", this.name);
        tag.putString("tag", this.tag);
        tag.putBoolean("friendlyFire", this.friendlyFire);

        if (this.banner != null) {
            tag.put("banner", this.banner);
        }

        CompoundNBT membersTag = new CompoundNBT();
        members.forEach((uuid, role) -> {
            CompoundNBT memberTag = new CompoundNBT();
            memberTag.putInt("role", role.ordinal());
            memberTag.putInt("kills", SMPEssentials.getData().getOrCreate(uuid).kills);
            memberTag.putInt("deaths", SMPEssentials.getData().getOrCreate(uuid).deaths);
            membersTag.put(UsernameCache.getMap().getOrDefault(uuid, uuid.toString()), memberTag);
        });
        tag.put("members", membersTag);

        ListNBT alliesTag = new ListNBT();
        allies.forEach(uuid -> alliesTag.add(StringNBT.valueOf(uuid.toString())));
        tag.put("allies", alliesTag);

        CompoundNBT warsTag = new CompoundNBT();
        wars.forEach((uuid, askedPeace) -> warsTag.putBoolean(uuid.toString(), askedPeace));
        tag.put("wars", warsTag);

        ListNBT requestersTag = new ListNBT();
        Set<UUID> requesters = TeamManager.teamRequests.get(teamUuid);

        if (requesters != null) {
            requesters.forEach(uuid -> {
                String username = UsernameCache.getLastKnownUsername(uuid);

                if (username == null) {
                    username = uuid.toString();
                }

                requestersTag.add(StringNBT.valueOf(username));
            });
        }

        tag.put("requesters", requestersTag);

        return tag;
    }

    public CompoundNBT toNbtForOthers() {
        // Differences:
        // only name, tag, banner, allies, and leader, members as list

        CompoundNBT tag = new CompoundNBT();

        UUID teamUuid = TeamManager.getTeamUuid(this);
        if (teamUuid != null) {
            tag.putUUID("uuid", teamUuid);
        }
        tag.putString("name", this.name);
        tag.putString("tag", this.tag);

        for (Map.Entry<UUID, Role> member : this.members.entrySet()) {
            if (member.getValue() == Role.TEAM_LEADER) {
                tag.putString("leader", UsernameCache.getMap().getOrDefault(member.getKey(), member.getKey().toString()));
                break;
            }
        }

        if (!tag.contains("leader")) {
            tag.putString("leader", "<NONE>");
        }

        if (this.banner != null) {
            tag.put("banner", this.banner);
        }

        ListNBT alliesTag = new ListNBT();
        allies.forEach(uuid -> {
            Team team = TeamManager.getTeamByUuid(uuid);
            if (team != null) {
                CompoundNBT allyTag = new CompoundNBT();
                allyTag.putString("name", team.name);
                allyTag.putString("tag", team.tag);

                if (team.banner != null) {
                    allyTag.put("banner", team.banner);
                }
            }
        });

        tag.put("allies", alliesTag);

        ListNBT membersTag = new ListNBT();
        members.forEach((uuid, role) -> {
            String lastKnown = UsernameCache.getLastKnownUsername(uuid);
            if (lastKnown == null) {
                lastKnown = uuid.toString();
            }

            membersTag.add(StringNBT.valueOf(lastKnown));
        });
        tag.put("members", membersTag);

        return tag;
    }

    public void broadcast(ITextComponent component) {
        for (ServerPlayerEntity player : SMPEssentials.server.getPlayerList().getPlayers()) {
            Team team = TeamManager.getTeam(player);

            if (team == this) {
                player.sendMessage(component, ChatType.SYSTEM, player.getUUID());
            }
        }
    }

    public static Team load(CompoundNBT nbt) {
        Team team = new Team();
        team.name = nbt.getString("name");
        team.tag = nbt.getString("tag");
        team.banner = nbt.getCompound("banner");
        team.friendlyFire = nbt.getBoolean("friendlyFire");

        CompoundNBT membersTag = nbt.getCompound("members");
        membersTag.getAllKeys().forEach(uuidString -> {
            try {
                UUID uuid = UUID.fromString(uuidString);
                team.members.put(uuid, Role.values()[membersTag.getInt(uuidString)]);
            } catch (IllegalArgumentException ignored) {}
        });

        ListNBT alliesTag = nbt.getList("allies", Constants.NBT.TAG_STRING);
        alliesTag.forEach(tag -> {
            try {
                UUID uuid = UUID.fromString(tag.getAsString());
                team.allies.add(uuid);
            } catch (IllegalArgumentException ignored) {}
        });

        CompoundNBT warsTag = nbt.getCompound("wars");
        warsTag.getAllKeys().forEach(warID -> {
            try {
                team.wars.put(UUID.fromString(warID), warsTag.getBoolean(warID));
            } catch (IllegalArgumentException ignored) {}
        });
        return team;
    }
}
