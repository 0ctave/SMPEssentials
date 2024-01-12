package me.khajiitos.smpessentials.screen.tab;

import me.khajiitos.smpessentials.screen.TeamManagerScreen;
import me.khajiitos.smpessentials.screen.widget.ScoreboardEntryWidget;
import me.khajiitos.smpessentials.screen.widget.TeamManagerScrollPanel;
import net.minecraft.nbt.CompoundNBT;

import java.util.Comparator;

public class ScoreboardTab extends AbstractTab {
    public ScoreboardTab(TeamManagerScreen parent, TeamManagerScrollPanel panel) {
        super(parent, panel);
    }

    @Override
    public void initManagerPanelWidgets() {
        if (this.parent.data == null) {
            return;
        }
        CompoundNBT members = this.parent.data.getCompound("members");
        members.getAllKeys().stream().sorted(Comparator.comparingInt(key -> members.getCompound((String) key).getInt("kills")).reversed()).forEach(key -> {
            this.panel.children().add(new ScoreboardEntryWidget(0,
                    0,
                    this.panel.getWidth() - this.panel.widgetOffset * 2,
                    20,
                    key,
                    members.getCompound(key).getInt("kills"),
                    members.getCompound(key).getInt("deaths")));

        });
    }
}
