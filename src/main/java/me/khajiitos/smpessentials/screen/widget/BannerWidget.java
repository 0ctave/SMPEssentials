package me.khajiitos.smpessentials.screen.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.datafixers.util.Pair;
import me.khajiitos.smpessentials.screen.util.BannerRenderer;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.tileentity.BannerTileEntityRenderer;
import net.minecraft.item.DyeColor;
import net.minecraft.tileentity.BannerPattern;
import net.minecraft.util.text.StringTextComponent;

import java.util.List;

public class BannerWidget extends Widget {
    public List<Pair<BannerPattern, DyeColor>> bannerPatterns;
    public DyeColor bannerColor;
    private final ModelRenderer flag;

    public BannerWidget(int x, int y, int width, int height, DyeColor bannerColor, List<Pair<BannerPattern, DyeColor>> bannerPatterns) {
        super(x, y, width, height, StringTextComponent.EMPTY);
        this.bannerPatterns = bannerPatterns;
        this.bannerColor = bannerColor;
        this.flag = BannerTileEntityRenderer.makeFlag();
    }

    @Override
    public void render(MatrixStack poseStack, int p_93658_, int p_93659_, float p_93660_) {
        BannerRenderer.render(poseStack, this.x, this.y, bannerPatterns);
    }
}
