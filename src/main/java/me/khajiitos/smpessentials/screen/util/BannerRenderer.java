package me.khajiitos.smpessentials.screen.util;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.BannerTileEntityRenderer;
import net.minecraft.item.DyeColor;
import net.minecraft.tileentity.BannerPattern;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.vector.Vector3f;


import java.util.List;

public class BannerRenderer {
    private static final ModelRenderer flag = BannerTileEntityRenderer.makeFlag();

    private static final Vector3f DIFFUSE_LIGHT_0 = (Vector3f) Util.make(new Vector3f(0.2F, 1.0F, -0.7F), Vector3f::normalize);
    private static final Vector3f DIFFUSE_LIGHT_1 = (Vector3f)Util.make(new Vector3f(-0.2F, 1.0F, 0.7F), Vector3f::normalize);


    public static void render(MatrixStack poseStack, int x, int y, List<Pair<BannerPattern, DyeColor>> bannerPatterns) {
        // Warning: not guaranteed that this will work correctly in every instance

        GlStateManager.setupGuiFlatDiffuseLighting(DIFFUSE_LIGHT_0, DIFFUSE_LIGHT_1);

        IRenderTypeBuffer.Impl multibuffersource$buffersource = Minecraft.getInstance().renderBuffers().bufferSource();
        poseStack.pushPose();
        poseStack.translate(x + 4, y + 69, 0.0D);
        poseStack.scale(24.0F, -24.0F, 1.0F);
        poseStack.translate(0.5D, 0.5D, 0.5D);
        poseStack.scale(1, -1, -1);
        //poseStack.translate(1, 1, 0);
        flag.xRot = 0.0F;
        flag.y = -40.0F;
        //flag.y = 0;
        net.minecraft.client.renderer.tileentity.BannerTileEntityRenderer.renderPatterns(poseStack, multibuffersource$buffersource, 0x00F000F0, OverlayTexture.NO_OVERLAY, flag, ModelBakery.BANNER_BASE, true, bannerPatterns);
        poseStack.popPose();
        multibuffersource$buffersource.endBatch();

        GlStateManager.setupGui3DDiffuseLighting(DIFFUSE_LIGHT_0, DIFFUSE_LIGHT_1);

    }
}
