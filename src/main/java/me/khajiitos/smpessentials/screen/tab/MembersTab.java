package me.khajiitos.smpessentials.screen.tab;

import me.khajiitos.smpessentials.data.Team;
import me.khajiitos.smpessentials.screen.TeamManagerScreen;
import me.khajiitos.smpessentials.screen.widget.JoinRequestEntryWidget;
import me.khajiitos.smpessentials.screen.widget.MemberEntryWidget;
import me.khajiitos.smpessentials.screen.widget.PlainText;
import me.khajiitos.smpessentials.screen.widget.TeamManagerScrollPanel;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.util.Constants;

public class MembersTab extends AbstractTab {
    public MembersTab(TeamManagerScreen parent, TeamManagerScrollPanel panel) {
        super(parent, panel);
    }

    @Override
    public void initManagerPanelWidgets() {
        if (this.parent.data == null) {
            return;
        }

        boolean isTeamManager = this.getRole(this.parent.data).ordinal() >= Team.Role.TEAM_MANAGER.ordinal();

        this.panel.children().add(new PlainText(0, 0, new StringTextComponent("Members").withStyle(TextFormatting.GRAY)));
        CompoundNBT members = this.parent.data.getCompound("members");
        members.getAllKeys().forEach(key -> {
            this.panel.children().add(new MemberEntryWidget(0, 0, this.panel.getWidth() - this.panel.widgetOffset * 2, 20, key, Team.Role.values()[members.getCompound(key).getInt("role")], this, isTeamManager));
        });

        Team.Role role = this.getRole(this.parent.data);

        Button inviteButton = new Button(0, 0, 100, 20, new StringTextComponent("Invite"), (b) -> {
            this.parent.openTab(new InviteTab(parent, panel));
        }, role.ordinal() < Team.Role.TEAM_MANAGER.ordinal() ? (button, poseStack, mouseX, mouseY) -> {
            this.parent.setTooltip(new StringTextComponent("Only team managers and above can invite to the team!"));
        } : Button.NO_TOOLTIP);

        if (this.getRole(this.parent.data).ordinal() < Team.Role.TEAM_MANAGER.ordinal()) {
            inviteButton.active = false;
        } else {
            ListNBT requesters = this.parent.data.getList("requesters", Constants.NBT.TAG_STRING);
            if (!requesters.isEmpty()) {
                this.panel.children().add(new PlainText(0, 0, new StringTextComponent("Join requests").withStyle(TextFormatting.GRAY)));

                requesters.forEach(tag -> {
                    if (tag instanceof StringNBT) {
                        StringNBT stringTag = (StringNBT) tag;
                        this.panel.children().add(new JoinRequestEntryWidget(0, 0, this.panel.getWidth() - this.panel.widgetOffset * 2, 20, stringTag.getAsString()));
                    }
                });
            }
        }

        this.panel.children().add(inviteButton);
    }
}
