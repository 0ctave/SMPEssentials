package me.khajiitos.smpessentials.screen.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import me.khajiitos.smpessentials.Packets;
import me.khajiitos.smpessentials.packet.teammanager.c2s.RequestOpenTeamInfoPacket;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.StringTextComponent;

import java.util.UUID;

public class TeamWidget extends Widget {
    private final UUID teamUuid;
    private final String teamName;
    private final String teamTag;
    private final CompoundNBT banner;
    private final TextFormatting chatFormatting;

    public TeamWidget(int x, int y, int width, int height, UUID teamUuid, String teamName, String teamTag, CompoundNBT banner, TextFormatting chatFormatting) {
        super(x, y, width, height, StringTextComponent.EMPTY);
        this.teamUuid = teamUuid;
        this.teamName = teamName;
        this.teamTag = teamTag;
        this.banner = banner;
        this.chatFormatting = chatFormatting;
    }


    @Override
    public void render(MatrixStack poseStack, int mouseX, int mouseY, float partialTicks) {
        this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
        int y = this.y + this.height / 2 - 4;
        AbstractGui.fill(poseStack, this.x, this.y, this.x + this.width, this.y + this.height, this.isHovered ? 0x88444444 : 0x55000000);

        Minecraft.getInstance().font.draw(poseStack, new StringTextComponent(this.teamName + " [" + this.teamTag +  "]").withStyle(chatFormatting), this.x + 4, y, 0xFFFFFFFF);
    }

    @Override
    public void onClick(double x, double y) {
        Packets.sendToServer(new RequestOpenTeamInfoPacket(this.teamUuid));
    }
}
