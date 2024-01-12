package me.khajiitos.smpessentials.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import me.khajiitos.smpessentials.Packets;
import me.khajiitos.smpessentials.packet.RulesResultPacket;
import me.khajiitos.smpessentials.screen.widget.RulesAcceptButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponent;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RulesScreen extends Screen {
    public static final ResourceLocation BOOK_LOCATION = new ResourceLocation("textures/gui/book.png");

    private final List<TextComponent> rules;

    public RulesScreen(String rules) {
        super(new StringTextComponent("Rules"));
        this.rules = Arrays.stream(rules.split("\n")).map(StringTextComponent::new).collect(Collectors.toList());
    }

    @Override
    protected void init() {
        super.init();
        int startX = (this.width - 192) / 2;
        int startY = (this.height - 192) / 2;

        this.addButton(new RulesAcceptButton(startX + 40, startY + 153, true, (p_98287_) -> Packets.INSTANCE.sendToServer(new RulesResultPacket(true))));
        this.addButton(new RulesAcceptButton(startX + 124, startY + 152, false, (p_98297_) -> Packets.INSTANCE.sendToServer(new RulesResultPacket(false))));
    }

    @Override
    public boolean keyPressed(int p_96552_, int p_96553_, int p_96554_) {
        if (p_96552_ != GLFW.GLFW_KEY_ESCAPE) {
            return super.keyPressed(p_96552_, p_96553_, p_96554_);
        }
        return false;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void render(@NotNull MatrixStack poseStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(poseStack);

        RenderSystem.color4f(255.0F, 255.0F, 255.0F, 255.0F);
        Minecraft.getInstance().getTextureManager().bind(BOOK_LOCATION);
        int startX = (this.width - 192) / 2;
        int startY = (this.height - 192) / 2;
        this.blit(poseStack, startX, startY, 0, 0, 192, 192);

        ITextComponent header = new StringTextComponent("§a§lRules");
        this.font.draw(poseStack, header, (float)(startX + 96 - this.font.width(header) / 2), startY + 14, 0);
        int y = startY + 26;
        for (ITextComponent rule : this.rules) {
            this.font.draw(poseStack, rule, (float)(startX + 36), y, 0);
            y += 9;
        }

        super.render(poseStack, mouseX, mouseY, partialTicks);
    }
}
