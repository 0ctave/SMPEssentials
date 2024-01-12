package me.khajiitos.smpessentials.screen.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.matrix.MatrixStack;
import me.khajiitos.smpessentials.Packets;
import me.khajiitos.smpessentials.SMPEssentials;
import me.khajiitos.smpessentials.packet.teammanager.c2s.RemoveAllyPacket;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.ResourceLocation;

import java.util.UUID;

public class AllyEntryWidget extends Widget {

    protected static final ResourceLocation TEAM_MANAGER = new ResourceLocation(SMPEssentials.MOD_ID, "textures/gui/team_manager.png");
    public final String teamName;
    public final String teamTag;
    public final UUID teamUUID;
    public final boolean canChange;

    public AllyEntryWidget(int x, int y, int width, int height, String teamName, String teamTag, UUID teamUUID, boolean canChange) {
        super(x, y, width, height, StringTextComponent.EMPTY);
        this.teamName = teamName;
        this.teamTag = teamTag;
        this.teamUUID = teamUUID;
        this.canChange = canChange;
    }

    @Override
    public void render(MatrixStack poseStack, int mouseX, int mouseY, float partialTicks) {
        int y = this.y + this.height / 2 - 4;
        AbstractGui.fill(poseStack, this.x, this.y, this.x + this.width, this.y + this.height, 0x66000000);
        Minecraft.getInstance().font.draw(poseStack, new StringTextComponent(this.teamName + " [" + this.teamTag + "]").withStyle(TextFormatting.GREEN), this.x + 4, y, 0xFFFFFFFF);

        if (this.canChange) {
            int startX = this.x + this.width - 20;
            int startY = this.y + this.height / 2 - 8;

            Minecraft.getInstance().getTextureManager().bind(TEAM_MANAGER);

            int v = 36;

            if (mouseX >= startX && mouseX <= startX + 16 && mouseY >= startY && mouseY <= startY + 16) {
                v += 16;
            }

            this.blit(poseStack, startX, startY, 16, v, 16, 16);
        }
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        super.onClick(mouseX, mouseY);

        if (this.canChange) {
            int startX = this.x + this.width - 20;
            int startY = this.y + this.height / 2 - 8;

            if (mouseX >= startX && mouseX <= startX + 16 && mouseY >= startY && mouseY <= startY + 16) {
                Packets.sendToServer(new RemoveAllyPacket(this.teamUUID));
            }
        }
    }
}
