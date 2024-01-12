package me.khajiitos.smpessentials.screen.tab;

import me.khajiitos.smpessentials.Packets;
import me.khajiitos.smpessentials.data.Team;
import me.khajiitos.smpessentials.packet.teammanager.c2s.UpdateBannerPacket;
import me.khajiitos.smpessentials.packet.teammanager.s2c.RefreshCurrentTabPacket;
import me.khajiitos.smpessentials.screen.TeamManagerScreen;
import me.khajiitos.smpessentials.screen.widget.BannerWidget;
import me.khajiitos.smpessentials.screen.widget.PlainText;
import me.khajiitos.smpessentials.screen.widget.TeamManagerScrollPanel;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.item.DyeColor;
import net.minecraft.tileentity.BannerTileEntity;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.text.StringTextComponent;

public class BannerTab extends AbstractTab {
    public BannerTab(TeamManagerScreen parent, TeamManagerScrollPanel panel) {
        super(parent, panel);
    }

    @Override
    public void initManagerPanelWidgets() {
        if (this.parent.data == null) {
            return;
        }
        CompoundNBT bannerTag = this.parent.data.getCompound("banner");
        ListNBT bannerPatterns = bannerTag.getList("Patterns", 10);
        DyeColor dyeColor = DyeColor.byId(bannerTag.getInt("Color"));

        if (bannerTag.isEmpty()) {
            this.panel.children().add(new PlainText(0, 0, new StringTextComponent("This team doesn't have a banner!")));
        } else {
            this.panel.children().add(new BannerWidget(0, 0, 32, 64, dyeColor, BannerTileEntity.createPatterns(dyeColor, bannerPatterns)));
        }

        Team.Role role = this.getRole(this.parent.data);

        if (role.ordinal() >= Team.Role.TEAM_MANAGER.ordinal()) {
            this.panel.children().add(new Button(0, 0, 100, 20, new StringTextComponent("Update banner"), (b) -> {
                Packets.sendToServer(new UpdateBannerPacket());
            }, (a, b, c, d) -> {
                this.parent.setTooltip(new StringTextComponent("Click this button while holding a banner to set it!"));
            }));
        }

        if (this.error != null) {
            this.panel.children().add(new PlainText(0, 0, new StringTextComponent(this.error).withStyle(TextFormatting.RED)));
        }
    }
}
