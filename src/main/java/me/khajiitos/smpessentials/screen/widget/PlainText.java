package me.khajiitos.smpessentials.screen.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.text.ITextComponent;
import org.jetbrains.annotations.NotNull;

public class PlainText extends Widget {
    private final int color;

    public PlainText(int x, int y, ITextComponent component) {
        this(x, y, component, 0xFFFFFFFF);
    }

    public PlainText(int x, int y, ITextComponent component, int color) {
        super(x, y, 0, 9, component);
        this.color = color;
    }

    @Override
    public void render(@NotNull MatrixStack poseStack, int p_93658_, int p_93659_, float p_93660_) {
        Minecraft.getInstance().font.draw(poseStack, this.getMessage(), this.x, this.y, this.color);
    }
}
