package me.khajiitos.smpessentials.screen.tab;

import me.khajiitos.smpessentials.Packets;
import me.khajiitos.smpessentials.data.Team;
import me.khajiitos.smpessentials.packet.teammanager.c2s.RequestAlliesPacket;
import me.khajiitos.smpessentials.packet.teammanager.s2c.AlliesPacket;
import me.khajiitos.smpessentials.screen.TeamManagerScreen;
import me.khajiitos.smpessentials.screen.widget.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.util.Constants;

import java.util.UUID;

public class AlliesTab extends AbstractTab {
    private CompoundNBT data;

    public AlliesTab(TeamManagerScreen parent, TeamManagerScrollPanel panel) {
        super(parent, panel);
        Packets.sendToServer(new RequestAlliesPacket());
    }

    @Override
    public void initManagerPanelWidgets() {
        if (this.data == null) {
            this.panel.children().add(new PlainText(0, 0, new StringTextComponent("...")));
        } else {
            ListNBT allies = this.data.getList("active", Constants.NBT.TAG_COMPOUND);

            if (allies.isEmpty()) {
                this.panel.children().add(new PlainText(0, 0, new StringTextComponent("This team doesn't have any allies!")));
            } else {
                allies.forEach(tag -> {
                    if (tag instanceof CompoundNBT) {
                        CompoundNBT compoundTag = (CompoundNBT) tag;
                        String nameStr = compoundTag.getString("name");
                        String tagStr = compoundTag.getString("tag");
                        UUID uuid = compoundTag.getUUID("uuid");
                        boolean canChange = this.getRole(this.parent.data).ordinal() >= Team.Role.TEAM_MANAGER.ordinal();
                        this.panel.children().add(new AllyEntryWidget(0, 0, this.panel.getWidth() - this.panel.widgetOffset * 2, 25, nameStr, tagStr, uuid, canChange));
                    }
                });
            }

            ListNBT invites = this.data.getList("invites", Constants.NBT.TAG_COMPOUND);

            if (this.getRole(this.parent.data).ordinal() >= Team.Role.TEAM_MANAGER.ordinal()) {

                if (!invites.isEmpty()) {
                    this.panel.children().add(new Spacer(0, 5));
                    this.panel.children().add(new PlainText(0, 0, new StringTextComponent("Invites:")));
                    invites.forEach(tag -> {
                        if (tag instanceof CompoundNBT) {
                            CompoundNBT compoundTag = (CompoundNBT) tag;
                            String nameStr = compoundTag.getString("name");
                            String tagStr = compoundTag.getString("tag");
                            UUID uuid = compoundTag.getUUID("uuid");
                            this.panel.children().add(new AllyInviteWidget(0, 0, this.panel.getWidth() - this.panel.widgetOffset * 2, 25, nameStr, tagStr, uuid));
                        }
                    });
                }

            }
        }
    }

    @Override
    public <T> void onPacket(T packet) {
        super.onPacket(packet);
        if (packet instanceof AlliesPacket) {
            AlliesPacket alliesPacket = (AlliesPacket) packet;
            this.data = alliesPacket.data;
            this.clearPanelWidgets();
            this.initManagerPanelWidgets();
        }
    }
}
