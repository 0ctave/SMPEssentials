package me.khajiitos.smpessentials.screen.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.matrix.MatrixStack;
import me.khajiitos.smpessentials.screen.util.NiceBackgroundRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.renderer.Tessellator;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TeamManagerScrollPanel extends ScrollPanel {
    private final List<Widget> widgets = new ArrayList<>();
    public final int widgetOffset;
    protected String error = null;

    public TeamManagerScrollPanel(int x, int y, int width, int height, int widgetOffset) {
        super(Minecraft.getInstance(), width, height, y, x);
        this.widgetOffset = widgetOffset;
    }

    public int getWidth() {
        return this.width;
    }

    @Override
    protected int getContentHeight() {
        int height = 0;

        for (Widget button : children()) {
            height += button.getHeight() + 5;
        }

        return height;
    }

    @Override
    protected void drawPanel(MatrixStack poseStack, int entryRight, int relativeY, Tessellator tess, int mouseX, int mouseY) {
        entryRight += widgetOffset;
        relativeY += widgetOffset;

        double scale = Minecraft.getInstance().getWindow().getGuiScale();
        RenderSystem.enableScissor((int)((left - 8) * scale), (int)(Minecraft.getInstance().getWindow().getHeight() - ((bottom - 8) * scale)),
                (int)((width) * scale), (int)((height - 16) * scale));

        for (Widget child : children()) {
            child.x = entryRight - this.width;
            child.y = relativeY;
            child.render(poseStack, mouseX, mouseY, Minecraft.getInstance().getDeltaFrameTime());
            relativeY += child.getHeight() + 5;
        }

        RenderSystem.enableScissor((int)((left) * scale), (int)(Minecraft.getInstance().getWindow().getHeight() - ((bottom) * scale)),
                (int)((width) * scale), (int)((height) * scale));
    }

    @Override
    public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        NiceBackgroundRenderer.render(this, matrix, this.left, this.top, width, height);
        super.render(matrix, mouseX, mouseY, partialTicks);
    }

    @Override
    public List<Widget> children() {
        return widgets;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scroll) {
        if (scroll != 0) {
            this.scrollDistance += -scroll * getScrollAmount();
            this.applyScrollLimits();
            return true;
        }
        return false;
    }

    public void applyScrollLimits() {
        int max = this.getContentHeight() - (this.height - this.widgetOffset * 2);

        if (max < 0 || this.scrollDistance < 0) {
            this.scrollDistance = 0;
        } else {
            this.scrollDistance = Math.min(this.scrollDistance, max);
        }
    }
}
