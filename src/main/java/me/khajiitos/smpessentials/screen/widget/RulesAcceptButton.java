package me.khajiitos.smpessentials.screen.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.matrix.MatrixStack;
import me.khajiitos.smpessentials.SMPEssentials;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class RulesAcceptButton extends Button {
    private static final ResourceLocation IMAGE = new ResourceLocation(SMPEssentials.MOD_ID, "textures/gui/rules_buttons.png");
    private final boolean accept;

    public RulesAcceptButton(int x, int y, boolean accept, IPressable onPress) {
        super(x, y, 16, 16, StringTextComponent.EMPTY, onPress);
        this.accept = accept;
    }

    @Override
    public void renderButton(@NotNull MatrixStack poseStack, int mouseX, int mouseY, float partialTicks) {
        RenderSystem.color4f(255.0F, 255.0F, 255.0F, 255.0F);
        Minecraft.getInstance().getTextureManager().bind(IMAGE);

        int u = this.accept ? 0 : 16;
        int v = this.isHovered() || this.isFocused() ? 16 : 0;

        blit(poseStack, this.x, this.y, this.getBlitOffset(), u, v, 16, 16, 32, 32);
    }
}
