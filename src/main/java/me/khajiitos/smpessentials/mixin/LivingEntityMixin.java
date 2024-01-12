package me.khajiitos.smpessentials.mixin;

import me.khajiitos.smpessentials.config.Config;

import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {


    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;addEffect(Lnet/minecraft/potion/EffectInstance;)Z"), method = "addEatEffect")
    public boolean addEffect(LivingEntity entity, EffectInstance mobEffect) {
        if (Config.preventInfiniteGoldenHearts && mobEffect.getEffect() == Effects.ABSORPTION && entity.hasEffect(Effects.ABSORPTION)) {
            return false;
        }
        return entity.addEffect(mobEffect);
    }
}
