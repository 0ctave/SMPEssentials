package me.khajiitos.smpessentials.screen.tab;

import me.khajiitos.smpessentials.Packets;
import me.khajiitos.smpessentials.data.Team;
import me.khajiitos.smpessentials.packet.teammanager.c2s.KickMemberPacket;
import me.khajiitos.smpessentials.packet.teammanager.c2s.UpdateMemberRolePacket;
import me.khajiitos.smpessentials.screen.TeamManagerScreen;
import me.khajiitos.smpessentials.screen.widget.PlainText;
import me.khajiitos.smpessentials.screen.widget.TeamManagerScrollPanel;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.StringTextComponent;

public class MemberInfoTab extends AbstractTab {

    private final String name;
    private final MembersTab parentTab;

    private Button memberButton;
    private Button teamManagerButton;

    public MemberInfoTab(TeamManagerScreen parent, TeamManagerScrollPanel panel, MembersTab parentTab, String name) {
        super(parent, panel);
        this.name = name;
        this.parentTab = parentTab;
    }

    @Override
    public void initManagerPanelWidgets() {
        this.panel.children().add(new Button(0, 0, 100, 20, new StringTextComponent("Back"), (button) -> {
            this.parent.openTab(this.parentTab);
        }));
        this.panel.children().add(new PlainText(0, 0, new StringTextComponent("Member: " + name)));

        Team.Role role = Team.Role.values()[this.parent.data.getCompound("members").getCompound(name).getInt("role")];
        Team.Role ourRole = this.getRole(this.parent.data);

        if (ourRole == Team.Role.TEAM_LEADER && role != Team.Role.TEAM_LEADER) {
            this.panel.children().add(new PlainText(0, 0, new StringTextComponent("Role: ")));

            this.memberButton = new Button(0, 0, 100, 20, new StringTextComponent("Member"), (button) -> {
                this.memberButton.active = false;
                this.teamManagerButton.active = true;

                //this.parent.data.getCompound(name).putInt("role", Team.Role.MEMBER.ordinal());
                Packets.sendToServer(new UpdateMemberRolePacket(this.name, Team.Role.MEMBER));
            });

            this.teamManagerButton = new Button(0, 0, 100, 20, new StringTextComponent("Team Manager"), (button) -> {
                this.memberButton.active = true;
                this.teamManagerButton.active = false;

                //this.parent.data.getCompound(name).putInt("role", Team.Role.TEAM_MANAGER.ordinal());
                Packets.sendToServer(new UpdateMemberRolePacket(this.name, Team.Role.TEAM_MANAGER));
            });

            this.memberButton.active = role == Team.Role.TEAM_MANAGER;
            this.teamManagerButton.active = role == Team.Role.MEMBER;

            this.panel.children().add(this.memberButton);
            this.panel.children().add(this.teamManagerButton);

            if (ourRole.ordinal() > role.ordinal()) {
                this.panel.children().add(new Button(0, 0, 100, 20, new StringTextComponent("Kick member").withStyle(TextFormatting.RED), (button) -> {
                    Packets.sendToServer(new KickMemberPacket(this.name));
                }));
            }
        }
    }
}
