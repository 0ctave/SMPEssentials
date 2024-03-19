package me.khajiitos.smpessentials.data;

import me.khajiitos.smpessentials.manager.TeamManager;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.loading.FMLServiceProvider;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.List;
import java.util.UUID;

public class War {

    public UUID warID;

    public UUID blueTeam;
    public int blueKills = 0;
    public int blueMaxHP = 0;

    public UUID redTeam;
    public int redKills = 0;
    public int redMaxHP = 0;


    public long lifetime;

    public boolean warEnded = false;

    public War(UUID blue, UUID red) {
        this.warID = UUID.randomUUID();

        this.blueTeam = blue;
        this.redTeam = red;
    }

    public War(UUID uuid, UUID blueTeam, UUID redTeam, int blueKills, int blueMaxHP, int redKills, int redMaxHP, long lifetime) {
        this(blueTeam, redTeam);

        this.warID = uuid;

        this.blueKills = blueKills;
        this.blueMaxHP = blueMaxHP;

        this.redKills = redKills;
        this.redMaxHP = redMaxHP;

        this.lifetime = lifetime;

    }


    public void tick() {
        lifetime++;

        if (redKills >= blueMaxHP) {
            endWar(redTeam, blueTeam);
        } else if (blueKills >= redMaxHP) {
            endWar(blueTeam, redTeam);
        }
    }

    public void endWar(UUID winnerID, UUID loserID) {
        Team winner = TeamManager.getTeamByUuid(winnerID);
        Team loser = TeamManager.getTeamByUuid(loserID);

        winner.broadcast(new StringTextComponent("Your team won the war against ").withStyle(TextFormatting.GOLD).append(new StringTextComponent(loser.name + " [" + loser.tag + "]").withStyle(TextFormatting.RED)));
        loser.broadcast(new StringTextComponent("Your team lost the war against ").withStyle(TextFormatting.RED).append(new StringTextComponent(winner.name + " [" + winner.tag + "]").withStyle(TextFormatting.GOLD)));

        winner.wars.remove(this.warID);
        loser.wars.remove(this.warID);

        warEnded = true;
    }

    public static War load(UUID uuid, CompoundNBT nbt) {
        UUID blueTeam = UUID.fromString(nbt.getString("blueTeam"));
        int blueKills = nbt.getInt("blueKills");
        int blueHP = nbt.getInt("blueHP");

        UUID redTeam = UUID.fromString(nbt.getString("redTeam"));
        int redKills = nbt.getInt("redKills");
        int redHP = nbt.getInt("redHP");

        long lifetime = nbt.getLong("lifetime");

        return new War(uuid, blueTeam, redTeam, blueKills, blueHP, redKills, redHP, lifetime);
    }

    public CompoundNBT save() {

        CompoundNBT nbt = new CompoundNBT();

        nbt.putString("blueTeam", blueTeam.toString());
        nbt.putInt("blueKills", blueKills);
        nbt.putInt("blueHP", blueMaxHP);


        nbt.putString("redTeam",  redTeam.toString());
        nbt.putInt("redKills",  redKills);
        nbt.putInt("redHP",  redMaxHP);

        nbt.putLong("lifetime", lifetime);

        return nbt;
    }

    public UUID getOtherTeam(UUID team) {
        if (team.equals(blueTeam)) {
            return redTeam;
        } else if (team.equals(redTeam)) {
            return blueTeam;
        }
        return null;
    }

    public int getTeamHP(UUID team) {
        if (team.equals(blueTeam)) {
            return blueMaxHP;
        } else if (team.equals(redTeam)) {
            return redMaxHP;
        }
        return 0;
    }

    public int getTeamKills(UUID team) {
        if (team.equals(blueTeam)) {
            return blueKills;
        } else if (team.equals(redTeam)) {
            return redKills;
        }
        return 0;
    }

    public void addKill(UUID team) {
        if (team.equals(blueTeam)) {
            blueKills++;
        } else if (team.equals(redTeam)) {
            redKills++;
        }
    }
}
