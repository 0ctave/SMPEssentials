package me.khajiitos.smpessentials.screen.tab;

import me.khajiitos.smpessentials.Packets;
import me.khajiitos.smpessentials.packet.teammanager.c2s.InvitePacket;
import me.khajiitos.smpessentials.packet.teammanager.s2c.PlayerInvitedPacket;
import me.khajiitos.smpessentials.screen.TeamManagerScreen;
import me.khajiitos.smpessentials.screen.widget.PlainText;
import me.khajiitos.smpessentials.screen.widget.Spacer;
import me.khajiitos.smpessentials.screen.widget.TeamManagerScrollPanel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;

public class InviteTab extends AbstractTab {

    private TextFieldWidget editBoxUsername;
    private String message;

    public InviteTab(TeamManagerScreen parent, TeamManagerScrollPanel panel) {
        super(parent, panel);
    }

    @Override
    public void initManagerPanelWidgets() {

        if (editBoxUsername == null) {
            editBoxUsername = new TextFieldWidget(Minecraft.getInstance().font, 0, 0, 100, 20, new StringTextComponent("Username"));
        }
        this.panel.children().add(new PlainText(0, 0, new StringTextComponent("Username")));
        this.panel.children().add(editBoxUsername);
        this.panel.children().add(new Spacer(0, 3));
        this.panel.children().add(new Button(0, 0, 75, 20, new StringTextComponent("Invite"), (b) -> {
            setError(null);
            Packets.sendToServer(new InvitePacket(editBoxUsername.getValue()));
        }));

        if (this.error != null) {
            panel.children().add(new PlainText(0, 0, new StringTextComponent("ERROR: " + this.error), 0xFFFF0000));
            this.message = null;
        } else if (this.message != null) {
            panel.children().add(new PlainText(0, 0, new StringTextComponent(this.message)));
        }
    }

    @Override
    public <T> void onPacket(T packet) {
        super.onPacket(packet);

        if (packet instanceof PlayerInvitedPacket) {
            this.message = "Player invited!";
        }
    }
}
