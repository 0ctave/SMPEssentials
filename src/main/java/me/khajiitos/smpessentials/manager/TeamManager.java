package me.khajiitos.smpessentials.manager;

import me.khajiitos.smpessentials.SMPEssentials;
import me.khajiitos.smpessentials.data.Team;
import net.minecraft.entity.player.ServerPlayerEntity;

import javax.annotation.Nullable;
import java.util.*;

public class TeamManager {
    public static final HashMap<UUID, Set<UUID>> playerInvites = new HashMap<>(); // player -> list of teams the player is invited to
    public static final HashMap<UUID, Set<UUID>> teamRequests = new HashMap<>(); // team uuid -> list of players that request to join the team
    public static final HashMap<UUID, Set<UUID>> teamWarInvites = new HashMap<>(); // team uuid -> list of teams that want to be at war with this team
    public static final HashMap<UUID, Set<UUID>> teamAllyInvites = new HashMap<>(); // team uuid -> list of teams that want to be an ally with this team

    public static Team getTeam(ServerPlayerEntity player) {
        UUID teamId = SMPEssentials.getData().getOrCreate(player.getUUID()).team;

        if (teamId != null) {
            return SMPEssentials.getData().getTeams().get(teamId);
        }

        return null;
    }

    public static Team getTeam(UUID player) {
        UUID teamId = SMPEssentials.getData().getOrCreate(player).team;

        if (teamId != null) {
            return SMPEssentials.getData().getTeams().get(teamId);
        }

        return null;
    }

    public static Team createTeam() {
        Team team = new Team();
        SMPEssentials.getData().getTeams().put(UUID.randomUUID(), team);
        SMPEssentials.getData().setDirty();
        return team;
    }

    public static void deleteTeam(Team team) {
        UUID thisTeamUuid = getTeamUuid(team);

        team.wars.forEach(pair -> {
            Team warTeam = getTeamByUuid(pair.getFirst());
            warTeam.wars.removeIf(warPair -> warPair.getFirst() == thisTeamUuid);
        });

        team.allies.forEach(uuid -> {
            Team allyTeam = getTeamByUuid(uuid);
            allyTeam.allies.remove(thisTeamUuid);
        });

        SMPEssentials.getData().getTeams().remove(thisTeamUuid);
        SMPEssentials.getData().setDirty();
    }

    public static Team getTeamByUuid(UUID teamUuid) {
        return SMPEssentials.getData().getTeams().get(teamUuid);
    }

    public static UUID getTeamUuid(Team team) {
        for (Map.Entry<UUID, Team> entry : SMPEssentials.getData().getTeams().entrySet()) {
            if (team == entry.getValue()) {
                return entry.getKey();
            }
        }
        return null;
    }

    public static void setPlayerTeam(ServerPlayerEntity player, @Nullable Team team, Team.Role role) {
        Team oldTeam = getTeam(player);

        if (oldTeam != null) {
            oldTeam.members.remove(player.getUUID());
        }

        if (team != null) {
            team.members.put(player.getUUID(), role);
            SMPEssentials.getData().getOrCreate(player.getUUID()).team = getTeamUuid(team);
            PlayerSynchedDataManager.setTeamTagIfOnline(player.getUUID(), team.tag);
        } else {
            SMPEssentials.getData().getOrCreate(player.getUUID()).team = null;
            PlayerSynchedDataManager.setTeamTagIfOnline(player.getUUID(), null);
        }
        SMPEssentials.getData().setDirty();
    }

    public static void setPlayerTeam(UUID player, @Nullable Team team, Team.Role role) {
        Team oldTeam = getTeam(player);

        if (oldTeam != null) {
            oldTeam.members.remove(player);
        }

        if (team != null) {
            team.members.put(player, role);
            SMPEssentials.getData().getOrCreate(player).team = getTeamUuid(team);
            PlayerSynchedDataManager.setTeamTagIfOnline(player, team.tag);
        } else {
            SMPEssentials.getData().getOrCreate(player).team = null;
            PlayerSynchedDataManager.setTeamTagIfOnline(player, null);
        }
        SMPEssentials.getData().setDirty();
    }

    public static boolean invitePlayer(ServerPlayerEntity player, Team team) {
        Set<UUID> set = playerInvites.computeIfAbsent(player.getUUID(), (uuid) -> new HashSet<>());
        UUID uuid = getTeamUuid(team);
        if (!set.contains(uuid)) {
            set.add(uuid);
            return true;
        }
        return false;
    }

    public static boolean requestToJoinTeam(ServerPlayerEntity player, Team team) {
        Set<UUID> set = teamRequests.computeIfAbsent(getTeamUuid(team), (uuid) -> new HashSet<>());
        UUID playerUUID = player.getUUID();
        if (!set.contains(playerUUID)) {
            set.add(playerUUID);
            return true;
        }
        return false;
    }
}
