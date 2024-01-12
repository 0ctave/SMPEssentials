package me.khajiitos.smpessentials.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.IRenderable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextComponent;

import java.util.ArrayList;
import java.util.List;

public abstract class SMPScreen extends Screen {


    public final List<IRenderable> renderables = new ArrayList<>();

    protected SMPScreen(ITextComponent p_i51108_1_) {
        super(p_i51108_1_);
    }

    @Override
    public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
        super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
        for (IRenderable renderable : this.renderables) {
            renderable.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
        }

    }

    protected <T extends IGuiEventListener & IRenderable> T addRenderableWidget(T p_169406_) {
        this.renderables.add(p_169406_);
        return this.addWidget(p_169406_);
    }

}
