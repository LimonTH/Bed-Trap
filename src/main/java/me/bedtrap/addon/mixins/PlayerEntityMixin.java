package me.bedtrap.addon.mixins;

import me.bedtrap.addon.modules.misc.TimerFall;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "getMovementSpeed", at = @At(value = "RETURN"), cancellable = true)
    public void getMovementSpeed(CallbackInfoReturnable<Float> cir) {
        if (TimerFall.shouldFreeze && TimerFall.INSTANCE.isActive()) cir.setReturnValue(0f);
    }
}