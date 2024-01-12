package me.khajiitos.smpessentials.mixin;

import me.khajiitos.smpessentials.listener.AntiGriefListeners;
import me.khajiitos.smpessentials.manager.PVPManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

@Mixin(World.class)
public class WorldMixin {

    @Inject(at = @At("RETURN"), method = "getEntities(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/AxisAlignedBB;Ljava/util/function/Predicate;)Ljava/util/List;")
    public void getEntities(Entity entity, AxisAlignedBB p_46537_, Predicate<? super Entity> p_46538_, CallbackInfoReturnable<List<Entity>> cir) {
        if (entity instanceof ServerPlayerEntity) {
            ServerPlayerEntity playerTriggering = (ServerPlayerEntity) entity;
            if (playerTriggering == AntiGriefListeners.gorgonHeadUser) {
                cir.getReturnValue().removeIf(e -> e instanceof ServerPlayerEntity);

                AntiGriefListeners.gorgonHeadUser = null;
            } else if (playerTriggering == AntiGriefListeners.cockatriceScepterUser) {
                cir.getReturnValue().removeIf(e -> e instanceof ServerPlayerEntity && (!PVPManager.hasPvpEnabled((ServerPlayerEntity) e) || !PVPManager.hasPvpEnabled(playerTriggering)));
                AntiGriefListeners.cockatriceScepterUser = null;
            }
        }
    }
}
