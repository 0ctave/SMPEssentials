package me.khajiitos.smpessentials.screen.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.matrix.MatrixStack;
import me.khajiitos.smpessentials.SMPEssentials;
import me.khajiitos.smpessentials.data.Team;
import me.khajiitos.smpessentials.screen.tab.MemberInfoTab;
import me.khajiitos.smpessentials.screen.tab.MembersTab;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.ResourceLocation;

public class MemberEntryWidget extends Widget {
    protected static final ResourceLocation TEAM_MANAGER = new ResourceLocation(SMPEssentials.MOD_ID, "textures/gui/team_manager.png");

    private final String playerName;
    private final Team.Role role;
    private final boolean canEnterMemberSettings;
    private final MembersTab parentTab;

    public MemberEntryWidget(int x, int y, int width, int height, String playerName, Team.Role role, MembersTab parentTab, boolean canEnterMemberSettings) {
        super(x, y, width, height, StringTextComponent.EMPTY);
        this.playerName = playerName;
        this.role = role;
        this.canEnterMemberSettings = canEnterMemberSettings;
        this.parentTab = parentTab;
    }

    @Override
    public void render(MatrixStack poseStack, int mouseX, int mouseY, float partialTicks) {
        int y = this.y + this.height / 2 - 4;
        AbstractGui.fill(poseStack, this.x, this.y, this.x + this.width, this.y + this.height, 0x66000000);
        Minecraft.getInstance().font.draw(poseStack, this.playerName + " - " + role.niceName, this.x + 4, y, 0xFFFFFFFF);

        if (this.canEnterMemberSettings) {
            int settingsStartX = this.x + this.width - 20;
            int settingsStartY = this.y + this.height / 2 - 8;

            Minecraft.getInstance().getTextureManager().bind(TEAM_MANAGER);

            int v = 36;

            if (mouseX >= settingsStartX && mouseX <= settingsStartX + 16 && mouseY >= settingsStartY && mouseY <= settingsStartY + 16) {
                v += 16;
            }

            this.blit(poseStack, settingsStartX, settingsStartY, 33, v, 16, 16);
        }
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        super.onClick(mouseX, mouseY);

        if (this.canEnterMemberSettings) {
            int settingsStartX = this.x + this.width - 20;
            int settingsStartY = this.y + this.height / 2 - 8;

            if (mouseX >= settingsStartX && mouseX <= settingsStartX + 16 && mouseY >= settingsStartY && mouseY <= settingsStartY + 16) {
                this.parentTab.parent.openTab(new MemberInfoTab(this.parentTab.parent, this.parentTab.panel, this.parentTab, this.playerName));
            }
        }
    }
}
