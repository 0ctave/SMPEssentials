package me.khajiitos.smpessentials.mixin;

import me.khajiitos.smpessentials.CustomPlayerSynchedData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerMixin extends LivingEntity implements CustomPlayerSynchedData {
    @Unique
    private static final DataParameter<String> TEAM_TAG = EntityDataManager.defineId(PlayerEntity.class, DataSerializers.STRING);
    @Unique
    private static final DataParameter<Boolean> PVP_ON = EntityDataManager.defineId(PlayerEntity.class, DataSerializers.BOOLEAN);
    @Unique
    private static final DataParameter<Boolean> STAFF = EntityDataManager.defineId(PlayerEntity.class, DataSerializers.BOOLEAN);

    protected PlayerMixin(EntityType<? extends LivingEntity> p_20966_, World p_20967_) {
        super(p_20966_, p_20967_);
    }

    @Inject(at = @At("TAIL"), method = "defineSynchedData")
    public void defineSynchedData(CallbackInfo ci) {
        this.entityData.define(TEAM_TAG, "");
        this.entityData.define(PVP_ON, false);
        this.entityData.define(STAFF, false);
    }

    @Override
    public void setStaff(boolean staff) {
        this.entityData.set(STAFF, staff);
    }

    @Override
    public void setPvpOn(boolean pvpOn) {
        this.entityData.set(PVP_ON, pvpOn);
    }

    @Override
    public void setTeamTag(String teamTag) {
        this.entityData.set(TEAM_TAG, teamTag == null ? "" : teamTag);
    }

    @Override
    public String getTeamTag() {
        String tag = this.entityData.get(TEAM_TAG);
        return tag.isEmpty() ? null : tag;
    }

    @Override
    public boolean isPvpOn() {
        return this.entityData.get(PVP_ON);
    }

    @Override
    public boolean isStaff() {
        return this.entityData.get(STAFF);
    }

    @Override
    public void onSyncedDataUpdated(@NotNull DataParameter<?> data) {
        super.onSyncedDataUpdated(data);

        if (data.equals(TEAM_TAG) || data.equals(PVP_ON) || data.equals(STAFF)) {
            PlayerEntity thisPlayer = (PlayerEntity)(Object)this;
            thisPlayer.refreshDisplayName();
            if (thisPlayer instanceof ServerPlayerEntity) {
                ServerPlayerEntity serverPlayer = (ServerPlayerEntity) thisPlayer;
                serverPlayer.refreshTabListName();
            }
        }
    }
}
