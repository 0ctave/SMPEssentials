package me.khajiitos.smpessentials.screen.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.matrix.MatrixStack;
import me.khajiitos.smpessentials.Packets;
import me.khajiitos.smpessentials.SMPEssentials;
import me.khajiitos.smpessentials.packet.teammanager.c2s.AllyInviteResponsePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.ResourceLocation;

import java.util.UUID;

public class AllyInviteWidget extends Widget {


    protected static final ResourceLocation TEAM_MANAGER = new ResourceLocation(SMPEssentials.MOD_ID, "textures/gui/team_manager.png");
    public final String teamName;
    public final String teamTag;
    public final UUID teamUUID;

    public AllyInviteWidget(int x, int y, int width, int height, String teamName, String teamTag, UUID teamUUID) {
        super(x, y, width, height, StringTextComponent.EMPTY);
        this.teamName = teamName;
        this.teamTag = teamTag;
        this.teamUUID = teamUUID;
    }

    @Override
    public void render(MatrixStack poseStack, int mouseX, int mouseY, float partialTicks) {
        int y = this.y + this.height / 2 - 4;
        AbstractGui.fill(poseStack, this.x, this.y, this.x + this.width, this.y + this.height, 0x66000000);
        Minecraft.getInstance().font.draw(poseStack, teamName + " [" + teamTag + "]", this.x + 4, y, 0xFFFFFFFF);

        int startXAccept = this.x + this.width - 40;
        int startYAccept = this.y + this.height / 2 - 8;

        int startXDeny = this.x + this.width - 20;
        int startYDeny = this.y + this.height / 2 - 8;

        Minecraft.getInstance().getTextureManager().bind(TEAM_MANAGER);

        int vAccept = 38;

        if (mouseX >= startXAccept && mouseX <= startXAccept + 16 && mouseY >= startYAccept && mouseY <= startYAccept + 16) {
            vAccept += 16;
        }

        this.blit(poseStack, startXAccept, startYAccept, 0, vAccept, 16, 16);

        int vDeny = 38;

        if (mouseX >= startXDeny && mouseX <= startXDeny + 16 && mouseY >= startYDeny && mouseY <= startYDeny + 16) {
            vDeny += 16;
        }

        this.blit(poseStack, startXDeny, startYDeny, 16, vDeny, 16, 16);
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        int startXAccept = this.x + this.width - 40;
        int startYAccept = this.y + this.height / 2 - 8;

        int startXDeny = this.x + this.width - 20;
        int startYDeny = this.y + this.height / 2 - 8;

        if (mouseX >= startXAccept && mouseX <= startXAccept + 16 && mouseY >= startYAccept && mouseY <= startYAccept + 16) {
            Packets.sendToServer(new AllyInviteResponsePacket(this.teamUUID, true));
        } else if (mouseX >= startXDeny && mouseX <= startXDeny + 16 && mouseY >= startYDeny && mouseY <= startYDeny + 16) {
            Packets.sendToServer(new AllyInviteResponsePacket(this.teamUUID, false));
        }
    }
}
