package me.khajiitos.smpessentials.screen.tab;

import me.khajiitos.smpessentials.packet.teammanager.c2s.FetchInvitesPacket;
import me.khajiitos.smpessentials.packet.teammanager.s2c.InvitesPacket;
import me.khajiitos.smpessentials.screen.TeamManagerScreen;
import me.khajiitos.smpessentials.screen.widget.InviteEntryWidget;
import me.khajiitos.smpessentials.screen.widget.PlainText;
import me.khajiitos.smpessentials.screen.widget.TeamManagerScrollPanel;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.text.StringTextComponent;

import java.util.UUID;

public class InvitesTab extends AbstractTab {
    private ListNBT invites;
    public InvitesTab(TeamManagerScreen parent, TeamManagerScrollPanel panel) {
        super(parent, panel);
        this.sendPacket(new FetchInvitesPacket());
    }

    @Override
    public void initManagerPanelWidgets() {
        if (invites == null) {
            this.panel.children().add(new PlainText(0, 0, new StringTextComponent("...")));
        } else if (invites.isEmpty()) {
            this.panel.children().add(new PlainText(0, 0, new StringTextComponent("You don't have any pending invites!")));
        } else {
            invites.forEach(tag -> {
                if (tag instanceof CompoundNBT) {
                    CompoundNBT compoundTag = (CompoundNBT) tag;
                    String nameStr = compoundTag.getString("name");
                    String tagStr = compoundTag.getString("tag");
                    UUID uuid = compoundTag.getUUID("uuid");
                    this.panel.children().add(new InviteEntryWidget(0, 0, panel.getWidth() - panel.widgetOffset * 2, 25, nameStr, tagStr, uuid));
                }
            });
        }
    }

    @Override
    public <T> void onPacket(T packet) {
        super.onPacket(packet);
        if (packet instanceof InvitesPacket) {
            InvitesPacket invitesPacket = (InvitesPacket) packet;
            invites = invitesPacket.invites;
            this.clearPanelWidgets();
            this.initManagerPanelWidgets();
        }
    }
}
