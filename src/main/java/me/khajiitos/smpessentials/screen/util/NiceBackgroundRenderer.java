package me.khajiitos.smpessentials.screen.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.matrix.MatrixStack;
import me.khajiitos.smpessentials.SMPEssentials;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.ResourceLocation;

public class NiceBackgroundRenderer {
    protected static final ResourceLocation TEAM_MANAGER = new ResourceLocation(SMPEssentials.MOD_ID, "textures/gui/team_manager.png");

    public static void render(AbstractGui component, MatrixStack poseStack, int x, int y, int width, int height) {
        int widthLeft = width;
        int heightLeft = height;

        Minecraft.getInstance().getTextureManager().bind(TEAM_MANAGER);

        component.blit(poseStack, x, y, 1, 1, 8, 8);
        component.blit(poseStack, x + width - 8, y, 247, 1, 8, 8);

        component.blit(poseStack, x, y + height - 8, 1, 27, 8, 8);
        component.blit(poseStack, x + width - 8, y + height - 8, 247, 27, 8, 8);

        widthLeft -= 16;
        heightLeft -= 16;

        int curX = x + 8;
        while (widthLeft > 0) {
            int length = Math.min(238, widthLeft);
            component.blit(poseStack, curX, y, 9, 1, length, 8);
            component.blit(poseStack, curX, y + height - 8, 9, 27, length, 8);
            widthLeft -= length;
            curX += length;
        }

        int curY = y + 8;
        while (heightLeft > 0) {
            int length = Math.min(16, heightLeft);
            component.blit(poseStack, x, curY, 1, 9, 8, length);
            component.blit(poseStack, x + width - 8, curY, 247, 9, 8, length);
            heightLeft -= length;
            curY += length;
        }

        AbstractGui.fill(poseStack, x + 8, y + 8, x + width - 8, y + height - 8, 0xFF333333);
    }
}
