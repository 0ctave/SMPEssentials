package me.khajiitos.smpessentials.screen.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.matrix.MatrixStack;
import me.khajiitos.smpessentials.Packets;
import me.khajiitos.smpessentials.SMPEssentials;
import me.khajiitos.smpessentials.packet.teammanager.c2s.RequestEndWarPacket;
import me.khajiitos.smpessentials.screen.TeamManagerScreen;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class WarEntryWidget extends Widget {

    protected static final ResourceLocation TEAM_MANAGER = new ResourceLocation(SMPEssentials.MOD_ID, "textures/gui/team_manager.png");
    public final String teamName;
    public final String teamTag;
    public final UUID teamUUID;
    public boolean askedToRemove;
    public boolean theyAskedToRemove;
    private final TeamManagerScreen parent;

    public WarEntryWidget(TeamManagerScreen parent, int x, int y, int width, int height, String teamName, String teamTag, UUID teamUUID, boolean askedToRemove, boolean theyAskedToRemove) {
        super(x, y, width, height, StringTextComponent.EMPTY);
        this.parent = parent;
        this.teamName = teamName;
        this.teamTag = teamTag;
        this.teamUUID = teamUUID;
        this.askedToRemove = askedToRemove;
        this.theyAskedToRemove = theyAskedToRemove;
    }

    @Override
    public void render(@NotNull MatrixStack poseStack, int mouseX, int mouseY, float partialTicks) {
        int y = this.y + this.height / 2 - 4;
        AbstractGui.fill(poseStack, this.x, this.y, this.x + this.width, this.y + this.height, 0x66000000);
        Minecraft.getInstance().font.draw(poseStack, new StringTextComponent(teamName + " [" + teamTag + "]").withStyle(TextFormatting.RED), this.x + 4, y, 0xFFFFFFFF);
        
        int startXRemove = this.x + this.width - 20;
        int startYRemove = this.y + this.height / 2 - 8;

        Minecraft minecraft = Minecraft.getInstance();
        FontRenderer fontrenderer = minecraft.font;
        minecraft.getTextureManager().bind(TEAM_MANAGER);

        int vRemove = 38;

        if (mouseX >= startXRemove && mouseX <= startXRemove + 16 && mouseY >= startYRemove && mouseY <= startYRemove + 16) {
            if (askedToRemove) {
                this.parent.setTooltip(new StringTextComponent("Your team has requested to end the war with this team!"));
            } else if (theyAskedToRemove) {
                this.parent.setTooltip(new StringTextComponent("The team has requested to end the war with your team!"));
            } else {
                vRemove += 16;
            }
        }

        if (askedToRemove) {
            vRemove += 32;
        } else if (theyAskedToRemove) {
            vRemove += 48;
        }

        this.blit(poseStack, startXRemove, startYRemove, 16, vRemove, 16, 16);
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        int startXRemove = this.x + this.width - 20;
        int startYRemove = this.y + this.height / 2 - 8;

        if (!askedToRemove && mouseX >= startXRemove && mouseX <= startXRemove + 16 && mouseY >= startYRemove && mouseY <= startYRemove + 16) {
            Packets.sendToServer(new RequestEndWarPacket(this.teamUUID));
        }
    }
}
