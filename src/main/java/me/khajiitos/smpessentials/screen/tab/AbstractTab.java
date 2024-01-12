package me.khajiitos.smpessentials.screen.tab;

import me.khajiitos.smpessentials.Packets;
import me.khajiitos.smpessentials.data.Team;
import me.khajiitos.smpessentials.packet.teammanager.s2c.ErrorPacket;
import me.khajiitos.smpessentials.packet.teammanager.s2c.RefreshCurrentTabPacket;
import me.khajiitos.smpessentials.screen.TeamManagerScreen;
import me.khajiitos.smpessentials.screen.widget.TeamManagerScrollPanel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.nbt.CompoundNBT;

public abstract class AbstractTab {
    public TeamManagerScrollPanel panel;
    public TeamManagerScreen parent;

    protected String error = null;

    public void setError(String error) {
        this.error = error;
        this.clearPanelWidgets();
        this.initManagerPanelWidgets();
    }

    public AbstractTab(TeamManagerScreen parent, TeamManagerScrollPanel panel) {
        this.panel = panel;
        this.parent = parent;
    }

    protected Team.Role getRole(CompoundNBT data) {
        ClientPlayerEntity player = Minecraft.getInstance().player;

        if (player == null) {
            return Team.Role.MEMBER;
        }

        int role = data.getCompound("members").getCompound(player.getScoreboardName()).getInt("role");

        if (role < 0 || role >= Team.Role.values().length) {
            return Team.Role.MEMBER;
        }

        return Team.Role.values()[role];
    }

    public abstract void initManagerPanelWidgets();

    public void clearPanelWidgets() {
        this.panel.children().clear();
        this.parent.applyManagerScrollLimits();
    }

    public <T> void sendPacket(T packet) {
        Packets.INSTANCE.sendToServer(packet);
    }

    public <T> void onPacket(T packet) {
        if (packet instanceof ErrorPacket) {
            ErrorPacket errorPacket = (ErrorPacket) packet;
            this.setError(errorPacket.error);
        } else if (packet instanceof RefreshCurrentTabPacket) {
            RefreshCurrentTabPacket refreshPacket = (RefreshCurrentTabPacket) packet;
            this.parent.data = refreshPacket.newData;
            this.parent.refresh();
        }
    }
}
