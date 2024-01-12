package me.khajiitos.smpessentials.screen.tab;

import me.khajiitos.smpessentials.Packets;
import me.khajiitos.smpessentials.data.Team;
import me.khajiitos.smpessentials.packet.teammanager.c2s.InviteToAlliesPacket;
import me.khajiitos.smpessentials.packet.teammanager.c2s.InviteToWarPacket;
import me.khajiitos.smpessentials.packet.teammanager.c2s.RequestJoinTeamPacket;
import me.khajiitos.smpessentials.screen.TeamManagerScreen;
import me.khajiitos.smpessentials.screen.widget.BannerWidget;
import me.khajiitos.smpessentials.screen.widget.PlainText;
import me.khajiitos.smpessentials.screen.widget.Spacer;
import me.khajiitos.smpessentials.screen.widget.TeamManagerScrollPanel;
import net.minecraft.item.DyeColor;
import net.minecraft.tileentity.BannerTileEntity;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.util.Constants;


public class TeamInfoTab extends AbstractTab {
    private final CompoundNBT data;
    private final TeamsTab parentTab;

    public TeamInfoTab(TeamManagerScreen parent, TeamManagerScrollPanel panel, TeamsTab parentTab, CompoundNBT data) {
        super(parent, panel);
        this.data = data;
        this.parentTab = parentTab;
    }

    @Override
    public void initManagerPanelWidgets() {
        this.panel.children().add(new Button(0, 0, 100, 20, new StringTextComponent("Back"), button -> {
            this.parent.openTab(this.parentTab);
        }));

        if (this.data == null) {
            return;
        }

        CompoundNBT banner = this.data.getCompound("banner");

        if (!banner.isEmpty()) {
            ListNBT bannerPatterns = banner.getList("Patterns", Constants.NBT.TAG_COMPOUND);
            DyeColor dyeColor = DyeColor.byId(banner.getInt("Color"));

            this.panel.children().add(new Spacer(0, 3));
            this.panel.children().add(new BannerWidget(0, 0, 32, 64, dyeColor, BannerTileEntity.createPatterns(dyeColor, bannerPatterns)));
        }

        this.panel.children().add(new PlainText(0, 0, new StringTextComponent("Team: " + this.data.getString("name") + " [" + this.data.getString("tag") + "]")));
        this.panel.children().add(new PlainText(0, 0, new StringTextComponent("Leader: " + this.data.getString("leader"))));

        if (this.parent.data != null && !this.parent.data.getUUID("uuid").equals(this.data.getUUID("uuid"))) {
            Team.Role role = this.getRole(this.parent.data);

            int allyStatus = this.data.getInt("allyStatus");
            int warStatus = this.data.getInt("warStatus");

            if (allyStatus != 2 && warStatus != 2) {
                if (role.ordinal() >= Team.Role.TEAM_MANAGER.ordinal()) {
                    Button inviteToAllies = new Button(0, 0, 130, 20, new StringTextComponent("Invite to allies").withStyle(TextFormatting.GREEN), (button) -> {
                        Packets.sendToServer(new InviteToAlliesPacket(this.data.getUUID("uuid")));
                        button.active = false;
                        button.setMessage(new StringTextComponent("Invite to allies (pending)").withStyle(TextFormatting.GREEN));
                    });

                    Button inviteToWar = new Button(0, 0, 130, 20, new StringTextComponent("Invite to war").withStyle(TextFormatting.RED), (button) -> {
                        Packets.sendToServer(new InviteToWarPacket(this.data.getUUID("uuid")));
                        button.active = false;
                        button.setMessage(new StringTextComponent("Invite to war (pending)").withStyle(TextFormatting.RED));
                    });

                    if (allyStatus == 1) {
                        inviteToAllies.active = false;
                        inviteToAllies.setMessage(new StringTextComponent("Invite to allies (pending)").withStyle(TextFormatting.GREEN));
                    }

                    if (warStatus == 1) {
                        inviteToWar.active = false;
                        inviteToWar.setMessage(new StringTextComponent("Invite to war (pending)").withStyle(TextFormatting.RED));
                    }

                    this.panel.children().add(inviteToAllies);
                    this.panel.children().add(inviteToWar);
                }
            }
        }

        if (this.parent.data == null) {
            Button requestJoin = new Button(0, 0, 135, 20, new StringTextComponent("Request to join"), (button) -> {
                Packets.sendToServer(new RequestJoinTeamPacket(this.data.getUUID("uuid")));
                button.active = false;
                button.setMessage(new StringTextComponent("Request to join (pending)"));
            });

            if (this.data.getBoolean("requestedToJoin")) {
                requestJoin.active = false;
                requestJoin.setMessage(new StringTextComponent("Request to join (pending)"));
            }

            this.panel.children().add(requestJoin);
        }

        ListNBT allies = this.data.getList("allies", 10);

        if (!allies.isEmpty()) {
            this.panel.children().add(new Spacer(0, 5));
            this.panel.children().add(new PlainText(0, 0, new StringTextComponent("Allies: ").withStyle(TextFormatting.GREEN)));

            allies.forEach(allyTag -> {
                if (allyTag instanceof CompoundNBT) {
                    CompoundNBT ally = (CompoundNBT) allyTag;
                    this.panel.children().add(new PlainText(0, 0, new StringTextComponent(ally.getString("name") + " [" + ally.getString("tag") + "]").withStyle(TextFormatting.GREEN)));
                }
            });
        }

        ListNBT members = this.data.getList("members", Constants.NBT.TAG_STRING);
        this.panel.children().add(new PlainText(0, 0, new StringTextComponent("").withStyle(TextFormatting.GRAY)));
        this.panel.children().add(new PlainText(0, 0, new StringTextComponent("Members:").withStyle(TextFormatting.GRAY)));
        members.forEach(tag -> {
            if (tag instanceof StringNBT) {
                StringNBT stringTag = (StringNBT) tag;
                this.panel.children().add(new PlainText(0, 0, new StringTextComponent(stringTag.getAsString()).withStyle(TextFormatting.GRAY)));
            }
        });
    }
}
