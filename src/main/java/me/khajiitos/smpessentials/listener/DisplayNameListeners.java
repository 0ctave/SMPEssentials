package me.khajiitos.smpessentials.listener;

import me.khajiitos.smpessentials.CustomPlayerSynchedData;
import me.khajiitos.smpessentials.data.Team;
import me.khajiitos.smpessentials.manager.PVPManager;
import me.khajiitos.smpessentials.manager.StaffManager;
import me.khajiitos.smpessentials.manager.TeamManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class DisplayNameListeners {

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onJoin(PlayerEvent.PlayerLoggedInEvent e) {
        if (e.getPlayer() instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) e.getPlayer();
            CustomPlayerSynchedData data = (CustomPlayerSynchedData) serverPlayer;
            data.setStaff(StaffManager.isStaff(serverPlayer.getUUID()));
            data.setPvpOn(PVPManager.hasPvpEnabled(serverPlayer.getUUID()));
            Team team = TeamManager.getTeam(serverPlayer.getUUID());
            data.setTeamTag(team != null ? team.tag : null);
        }
    }

    @SubscribeEvent
    public void onClone(PlayerEvent.Clone e) {
        if (e.isWasDeath() && e.getPlayer() instanceof ServerPlayerEntity && e.getOriginal() instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) e.getPlayer();
            ServerPlayerEntity original = (ServerPlayerEntity) e.getOriginal();
            CustomPlayerSynchedData dataOriginal = (CustomPlayerSynchedData) original;
            CustomPlayerSynchedData dataNew = (CustomPlayerSynchedData) serverPlayer;

            dataNew.setPvpOn(dataOriginal.isPvpOn());
            dataNew.setStaff(dataOriginal.isStaff());
            dataNew.setTeamTag(dataOriginal.getTeamTag());
        }
    }

    private ITextComponent getDisplayName(PlayerEntity player) {
        CustomPlayerSynchedData data = (CustomPlayerSynchedData) player;
        boolean staff = data.isStaff();
        boolean pvp = data.isPvpOn();
        String tag = data.getTeamTag();

        String newDisplayNamePrefix = "";

        if (tag != null) {
            newDisplayNamePrefix += "§7[" + tag + "] ";
        }

        if (staff) {
            newDisplayNamePrefix += "§4[Staff] ";
        }

        newDisplayNamePrefix += (pvp ? "§a" : "§8");

        return new StringTextComponent(newDisplayNamePrefix + player.getName().getContents() + "§r");
    }

    @SubscribeEvent
    public void getName(PlayerEvent.NameFormat e) {
        e.setDisplayname(getDisplayName(e.getPlayer()));
    }

    @SubscribeEvent
    public void getNameTab(PlayerEvent.TabListNameFormat e) {
        e.setDisplayName(getDisplayName(e.getPlayer()));
    }
}
