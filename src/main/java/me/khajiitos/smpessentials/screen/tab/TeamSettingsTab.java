package me.khajiitos.smpessentials.screen.tab;

import me.khajiitos.smpessentials.Packets;
import me.khajiitos.smpessentials.packet.teammanager.c2s.UpdateFriendlyFirePacket;
import me.khajiitos.smpessentials.screen.TeamManagerScreen;
import me.khajiitos.smpessentials.screen.widget.Spacer;
import me.khajiitos.smpessentials.screen.widget.TeamManagerScrollPanel;
import net.minecraft.client.gui.widget.ToggleWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.CheckboxButton;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.StringTextComponent;


public class TeamSettingsTab extends AbstractTab {
    private final YourTeamTab parentTab;

    public TeamSettingsTab(TeamManagerScreen parent, TeamManagerScrollPanel panel, YourTeamTab parentTab) {
        super(parent, panel);
        this.parentTab = parentTab;
    }

    @Override
    public void initManagerPanelWidgets() {
        if (this.parent.data == null) {
            return;
        }

        panel.children().add(new Button(0, 0, 100, 20, new StringTextComponent("Back"), (button) -> {
            this.parent.openTab(this.parentTab);
        }));

        panel.children().add(new Spacer(0, 5));

        panel.children().add(new CheckboxButton(0, 0, 20, 20, new StringTextComponent("Friendly fire"), this.parent.data.getBoolean("friendlyFire")) {
            @Override
            public void onPress() {
                super.onPress();
                Packets.sendToServer(new UpdateFriendlyFirePacket(this.selected()));
                TeamSettingsTab.this.parent.data.putBoolean("friendlyFire", this.selected());
            }
        });
    }
}
