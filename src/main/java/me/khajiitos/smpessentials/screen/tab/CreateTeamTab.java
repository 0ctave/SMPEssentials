package me.khajiitos.smpessentials.screen.tab;

import me.khajiitos.smpessentials.Packets;
import me.khajiitos.smpessentials.packet.teammanager.c2s.CreateTeamPacket;
import me.khajiitos.smpessentials.screen.TeamManagerScreen;
import me.khajiitos.smpessentials.screen.widget.PlainText;
import me.khajiitos.smpessentials.screen.widget.Spacer;
import me.khajiitos.smpessentials.screen.widget.TeamManagerScrollPanel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;
import org.apache.commons.lang3.CharUtils;

public class CreateTeamTab extends AbstractTab {
    private TextFieldWidget editBoxTeamName;
    private TextFieldWidget editBoxTagName;

    public CreateTeamTab(TeamManagerScreen parent, TeamManagerScrollPanel panel) {
        super(parent, panel);
    }

    @Override
    public void initManagerPanelWidgets() {
        panel.children().add(new PlainText(0, 0, new StringTextComponent("Create team")));
        panel.children().add(new Spacer(0, 10));
        panel.children().add(new PlainText(0, 0, new StringTextComponent("Name")));
        if (editBoxTeamName == null) {
            editBoxTeamName = new TextFieldWidget(Minecraft.getInstance().font, 0, 0, 100, 20, new StringTextComponent("Name"));
            editBoxTeamName.setMaxLength(32);
        }
        panel.children().add(editBoxTeamName);
        panel.children().add(new PlainText(0, 0, new StringTextComponent("Tag")));
        if (editBoxTagName == null) {
            editBoxTagName = new TextFieldWidget(Minecraft.getInstance().font, 0, 0, 100, 20, new StringTextComponent("Tag")) {
                @Override
                public boolean charTyped(char character, int key) {
                    if (CharUtils.isAsciiAlpha(character)) {
                        return super.charTyped(Character.toUpperCase(character), key);
                    }
                    return false;
                }
            };
            editBoxTagName.setMaxLength(4);
        }
        panel.children().add(editBoxTagName);
        panel.children().add(new Button(0, 0, 75, 20, new StringTextComponent("Create"), (button) -> {
            String name = this.editBoxTeamName.getValue();
            String tag = this.editBoxTagName.getValue();

            if (name.length() < 4 || name.length() > 32) {
                this.setError("Team name must be 4-32 characters long");
            } else if (tag.length() < 2 || tag.length() > 4) {
                this.setError("Team tag must be 2-4 characters long");
            } else {
                this.setError(null);
                Packets.sendToServer(new CreateTeamPacket(this.editBoxTeamName.getValue(), this.editBoxTagName.getValue()));
            }
        }));

        if (this.error != null) {
            panel.children().add(new PlainText(0, 0, new StringTextComponent("ERROR: " + this.error), 0xFFFF0000));
        }
    }
}
