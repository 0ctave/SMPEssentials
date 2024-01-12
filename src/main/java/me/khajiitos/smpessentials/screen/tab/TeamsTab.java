package me.khajiitos.smpessentials.screen.tab;

import me.khajiitos.smpessentials.Packets;
import me.khajiitos.smpessentials.packet.teammanager.c2s.FetchAllTeamsPacket;
import me.khajiitos.smpessentials.packet.teammanager.s2c.AllTeamsPacket;
import me.khajiitos.smpessentials.packet.teammanager.s2c.OpenTeamInfoPacket;
import me.khajiitos.smpessentials.screen.TeamManagerScreen;
import me.khajiitos.smpessentials.screen.widget.PlainText;
import me.khajiitos.smpessentials.screen.widget.TeamManagerScrollPanel;
import me.khajiitos.smpessentials.screen.widget.TeamWidget;
import net.minecraft.nbt.INBT;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.util.Constants;

import java.util.UUID;

public class TeamsTab extends AbstractTab {
    private ListNBT data;

    public TeamsTab(TeamManagerScreen parent, TeamManagerScrollPanel panel) {
        super(parent, panel);
        Packets.sendToServer(new FetchAllTeamsPacket());
    }

    public boolean containsString(ListNBT listTag, String string) {
        for (INBT tag : listTag) {
            if (tag instanceof StringNBT && tag.getAsString().equals(string)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void initManagerPanelWidgets() {
        if (data != null && !data.isEmpty()) {
            data.forEach(tag -> {
                if (tag instanceof CompoundNBT) {
                    CompoundNBT compoundTag = (CompoundNBT) tag;
                    TextFormatting chatFormatting;
                    if (this.parent.data == null) {
                        chatFormatting = TextFormatting.WHITE;
                    } else {
                        UUID thisTeamUUID = this.parent.data.getUUID("uuid");

                        if (compoundTag.getUUID("uuid").equals(thisTeamUUID)) {
                            chatFormatting = TextFormatting.DARK_AQUA;
                        } else if (containsString(this.parent.data.getList("allies", Constants.NBT.TAG_STRING), compoundTag.getUUID("uuid").toString())) {
                            chatFormatting = TextFormatting.GREEN;
                        } else if (containsString(this.parent.data.getList("wars", Constants.NBT.TAG_STRING), compoundTag.getUUID("uuid").toString())) {
                            chatFormatting = TextFormatting.RED;
                        } else {
                            chatFormatting = TextFormatting.WHITE;
                        }
                    }

                    this.panel.children().add(new TeamWidget(0,
                            0,
                            this.panel.getWidth() - this.panel.widgetOffset * 2,
                            25,
                            compoundTag.getUUID("uuid"),
                            compoundTag.getString("name"),
                            compoundTag.getString("tag"),
                            compoundTag.getCompound("banner"),
                            chatFormatting
                    ));
                }
            });
        } else {
            this.panel.children().add(new PlainText(0, 0, new StringTextComponent("No teams found!")));
        }
    }

    @Override
    public <T> void onPacket(T packet) {
        super.onPacket(packet);
        if (packet instanceof AllTeamsPacket) {
            AllTeamsPacket allTeamsPacket = (AllTeamsPacket) packet;
            this.data = allTeamsPacket.data;
            clearPanelWidgets();
            initManagerPanelWidgets();
        } else if (packet instanceof OpenTeamInfoPacket) {
            OpenTeamInfoPacket openTeamInfoPacket = (OpenTeamInfoPacket) packet;
            this.parent.openTab(new TeamInfoTab(this.parent, this.panel, this, openTeamInfoPacket.data));
        }
    }
}
