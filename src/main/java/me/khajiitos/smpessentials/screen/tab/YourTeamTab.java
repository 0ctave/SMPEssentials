package me.khajiitos.smpessentials.screen.tab;

import me.khajiitos.smpessentials.Packets;
import me.khajiitos.smpessentials.data.Team;
import me.khajiitos.smpessentials.packet.teammanager.c2s.LeaveTeamPacket;
import me.khajiitos.smpessentials.screen.TeamManagerScreen;
import me.khajiitos.smpessentials.screen.widget.PlainText;
import me.khajiitos.smpessentials.screen.widget.Spacer;
import me.khajiitos.smpessentials.screen.widget.TeamManagerScrollPanel;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.StringTextComponent;

public class YourTeamTab extends AbstractTab {

    public YourTeamTab(TeamManagerScreen parent, TeamManagerScrollPanel panel) {
        super(parent, panel);
    }

    @Override
    public void initManagerPanelWidgets() {
        if (this.parent.data == null) {
            return;
        }

        String name = this.parent.data.getString("name");
        String tag = this.parent.data.getString("tag");

        this.panel.children().add(new PlainText(0, 0, new StringTextComponent("Team: " + name)));
        this.panel.children().add(new PlainText(0, 0, new StringTextComponent("Tag: " + tag)));
        this.panel.children().add(new Spacer(0, 5));
        CompoundNBT members = this.parent.data.getCompound("members");
        if (Minecraft.getInstance().player != null) {
            int roleOrdinal = members.getCompound(Minecraft.getInstance().player.getScoreboardName()).getInt("role");
            Team.Role role = Team.Role.values()[roleOrdinal];
            this.panel.children().add(new PlainText(0, 0, new StringTextComponent("Your role: " + role.niceName)));
            this.panel.children().add(new PlainText(0, 0, new StringTextComponent("Members: " + members.getAllKeys().size())));
        }

        if (this.getRole(this.parent.data).ordinal() >= Team.Role.TEAM_MANAGER.ordinal()) {
            this.panel.children().add(new Button(0, 0, 100, 20, new StringTextComponent("Team settings"), (button) -> {
                this.parent.openTab(new TeamSettingsTab(this.parent, this.panel, this));
            }));
        }

        this.panel.children().add(new Button(0, 0, 100, 20, new StringTextComponent("Leave team").withStyle(TextFormatting.RED), button -> {
            Packets.sendToServer(new LeaveTeamPacket());
        }));
    }
}