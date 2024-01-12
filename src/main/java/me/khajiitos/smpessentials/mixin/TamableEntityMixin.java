package me.khajiitos.smpessentials.mixin;

import me.khajiitos.smpessentials.manager.PVPManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.ServerPlayerEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.UUID;

@Mixin(TameableEntity.class)
public abstract class TamableEntityMixin {

    @Shadow @Nullable public abstract LivingEntity getOwner();

    @Shadow @Nullable public abstract UUID getOwnerUUID();

    @Inject(at = @At("HEAD"), method = "canAttack", cancellable = true)
    public void canAttack(LivingEntity entity, CallbackInfoReturnable<Boolean> cir) {
        if (entity instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) entity;
            if (this.getOwnerUUID() != null) {
                if (!PVPManager.canAttackEachOther(player.getUUID(), this.getOwnerUUID())) {
                    cir.setReturnValue(false);
                }
            } else {
                if (!PVPManager.hasPvpEnabled(player)) {
                    cir.setReturnValue(false);
                }
            }
        }
    }
}
