package me.khajiitos.smpessentials.screen.tab;

import me.khajiitos.smpessentials.Packets;
import me.khajiitos.smpessentials.data.Team;
import me.khajiitos.smpessentials.packet.teammanager.c2s.RequestWarsPacket;
import me.khajiitos.smpessentials.packet.teammanager.s2c.WarsPacket;
import me.khajiitos.smpessentials.screen.TeamManagerScreen;
import me.khajiitos.smpessentials.screen.widget.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.util.Constants;

import java.util.UUID;

public class WarsTab extends AbstractTab {
    private CompoundNBT data;

    public WarsTab(TeamManagerScreen parent, TeamManagerScrollPanel panel) {
        super(parent, panel);
        Packets.sendToServer(new RequestWarsPacket());
    }

    @Override
    public void initManagerPanelWidgets() {
        if (this.data == null) {
            this.panel.children().add(new PlainText(0, 0, new StringTextComponent("...")));
        } else {
            ListNBT wars = this.data.getList("active", Constants.NBT.TAG_COMPOUND);

            if (wars.isEmpty()) {
                this.panel.children().add(new PlainText(0, 0, new StringTextComponent("This team isn't in any wars!")));
            } else {
                wars.forEach(tag -> {
                    if (tag instanceof CompoundNBT) {
                        CompoundNBT compoundTag = (CompoundNBT) tag;
                        String nameStr = compoundTag.getString("name");
                        String tagStr = compoundTag.getString("tag");
                        UUID uuid = compoundTag.getUUID("uuid");

                        int kills = compoundTag.getInt("kills");
                        int hp = compoundTag.getInt("HP");

                        boolean askedPeace = compoundTag.getBoolean("askedPeace");
                        boolean theyAskedPeace = compoundTag.getBoolean("theyAskedPeace");

                        this.panel.children().add(new WarEntryWidget(this.parent, 0, 0, this.panel.getWidth() - this.panel.widgetOffset * 2, 25, nameStr, tagStr, uuid, kills, hp, askedPeace, theyAskedPeace));
                    }
                });
            }

            ListNBT invites = this.data.getList("invites", Constants.NBT.TAG_COMPOUND);

            if (this.getRole(this.parent.data).ordinal() == Team.Role.TEAM_LEADER.ordinal()) {
                if (!invites.isEmpty()) {
                    this.panel.children().add(new Spacer(0, 5));
                    this.panel.children().add(new PlainText(0, 0, new StringTextComponent("Invites:")));

                    invites.forEach(tag -> {
                        if (tag instanceof CompoundNBT) {
                            CompoundNBT compoundTag = (CompoundNBT) tag;
                            String nameStr = compoundTag.getString("name");
                            String tagStr = compoundTag.getString("tag");
                            UUID uuid = compoundTag.getUUID("uuid");
                            this.panel.children().add(new WarInviteWidget(0, 0, this.panel.getWidth() - this.panel.widgetOffset * 2, 25, nameStr, tagStr, uuid));
                        }
                    });
                }
            }
        }
    }

    @Override
    public <T> void onPacket(T packet) {
        super.onPacket(packet);
        if (packet instanceof WarsPacket) {
            WarsPacket warsPacket = (WarsPacket) packet;
            this.data = warsPacket.data;
            this.clearPanelWidgets();
            this.initManagerPanelWidgets();
        }
    }
}