package me.khajiitos.smpessentials.screen.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.text.StringTextComponent;

public class ScoreboardEntryWidget extends Widget {
    private final String playerName;
    private final int kills, deaths;
    public ScoreboardEntryWidget(int x, int y, int width, int height, String playerName, int kills, int deaths) {
        super(x, y, width, height, StringTextComponent.EMPTY);
        this.playerName = playerName;
        this.kills = kills;
        this.deaths = deaths;
    }

    @Override
    public void render(MatrixStack poseStack, int mouseX, int mouseY, float partialTicks) {
        int y = this.y + this.height / 2 - 4;
        AbstractGui.fill(poseStack, this.x, this.y, this.x + this.width, this.y + this.height, 0x66000000);
        Minecraft.getInstance().font.draw(poseStack, this.playerName, this.x + 4, y, 0xFFFFFFFF);
        String text = "Kills: " + kills + " | Deaths: " + deaths;
        Minecraft.getInstance().font.draw(poseStack, text, this.x + this.width - Minecraft.getInstance().font.width(text) - 4, y, 0xFFFFFFFF);
    }
}