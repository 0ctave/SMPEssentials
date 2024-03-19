package me.khajiitos.smpessentials.manager;

import me.khajiitos.smpessentials.SMPEssentials;
import me.khajiitos.smpessentials.data.Team;
import me.khajiitos.smpessentials.data.War;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.management.PlayerList;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

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

        team.wars.keySet().forEach(uuid -> {
            War war = getWars().get(uuid);
            war.endWar(war.getOtherTeam(uuid), thisTeamUuid);
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

    public static void clearInvites(Team team) {
        UUID teamUuid = getTeamUuid(team);
        playerInvites.values().forEach(set -> set.remove(teamUuid));
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

    public static boolean isAtWar(ServerPlayerEntity player1, ServerPlayerEntity player2) {
        Team team1 = getTeam(player1);
        Team team2 = getTeam(player2);

        return areTeamsAtWar(team1, team2);
    }

    public static HashMap<UUID, Team> getTeams() {
        return SMPEssentials.getData().getTeams();
    }

    public static War declareWar(Team blue, Team red) {
        War war = new War(TeamManager.getTeamUuid(blue), TeamManager.getTeamUuid(red));
        SMPEssentials.getData().getWars().put(war.warID, war);
        SMPEssentials.getData().setDirty();

        TeamManager.clearInvites(blue);
        TeamManager.clearInvites(red);

        blue.wars.putIfAbsent(war.warID, false);
        red.wars.putIfAbsent(war.warID, false);

        war.blueMaxHP = getMaxHP(red);
        war.redMaxHP = getMaxHP(blue);

        return war;
    }

    public static HashMap<UUID, War> getWars() {
        return SMPEssentials.getData().getWars();
    }

    public static boolean areTeamsAtWar(UUID team1, UUID team2) {
        return areTeamsAtWar(getTeamByUuid(team1), getTeamByUuid(team2));
    }

    public static boolean areTeamsAtWar(Team team1, Team team2) {
        if (team1 == null || team2 == null) {
            return false;
        }

        return team1.wars.keySet().stream().anyMatch(team2.wars.keySet()::contains);
    }

    public static War getWar(ServerPlayerEntity player1, ServerPlayerEntity player2) {
        Team team1 = getTeam(player1);
        Team team2 = getTeam(player2);

        UUID warID = team1.wars.keySet().stream().filter(team2.wars.keySet()::contains).findFirst().get();

        return getWars().get(warID);
    }


    private static int getMaxHP(Team team) {

        //int teamOnline = getTeamOnline(team);
        //int alliesOnline = team.allies.stream().map(TeamManager::getTeamByUuid).mapToInt(TeamManager::getTeamOnline).sum();

        int teamOnline = team.members.size();
        int alliesOnline = team.allies.stream().map(TeamManager::getTeamByUuid).mapToInt((ally) -> ally.members.size()).sum();

        return 2 * (teamOnline + alliesOnline);
    }

    private static int getTeamOnline(Team team) {
        PlayerList players = ServerLifecycleHooks.getCurrentServer().getPlayerList();
        return team.members.keySet().stream().map(players::getPlayer).mapToInt(player -> player != null ? 1 : 0).sum();
    }
}
