package me.bedtrap.addon.mixins;

import me.bedtrap.addon.modules.misc.HandTweaks;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public abstract class HeldItemRenderMixin {
    @Shadow
    private float prevEquipProgressMainHand;
    @Shadow
    private float equipProgressMainHand;
    @Shadow
    private float equipProgressOffHand;
    @Shadow
    private float prevEquipProgressOffHand;
    @Shadow
    private ItemStack offHand;
    @Shadow
    private ItemStack mainHand;

    @Inject(method = {"updateHeldItems"}, at = {@At("HEAD")}, cancellable = true)
    public void updateHeldItems1(CallbackInfo ci) {
        HandTweaks viewmodel = Modules.get().get(HandTweaks.class);
        if (viewmodel.isActive()) {
            ci.cancel();
            this.prevEquipProgressMainHand = this.equipProgressMainHand;
            this.prevEquipProgressOffHand = this.equipProgressOffHand;
            ClientPlayerEntity clientPlayerEntity = MinecraftClient.getInstance().player;
            ItemStack itemStack = clientPlayerEntity.getMainHandStack();
            ItemStack itemStack2 = clientPlayerEntity.getOffHandStack();
            if (ItemStack.areEqual(this.mainHand, itemStack)) {
                this.mainHand = itemStack;
            }

            if (ItemStack.areEqual(this.offHand, itemStack2)) {
                this.offHand = itemStack2;
            }

            if (clientPlayerEntity.isRiding()) {
                this.equipProgressMainHand = MathHelper.clamp(this.equipProgressMainHand - 0.4F, 0.0F, 1.0F);
                this.equipProgressOffHand = MathHelper.clamp(this.equipProgressOffHand - 0.4F, 0.0F, 1.0F);
            } else {
                this.equipProgressMainHand += MathHelper.clamp((this.mainHand == itemStack ? 1.0F : 0.0F) - this.equipProgressMainHand, -0.4F, 0.4F);
                this.equipProgressOffHand += MathHelper.clamp((float) (this.offHand == itemStack2 ? 1 : 0) - this.equipProgressOffHand, -0.4F, 0.4F);
            }

            if (this.equipProgressMainHand < 0.1F) {
                this.mainHand = itemStack;
            }

            if (this.equipProgressOffHand < 0.1F) {
                this.offHand = itemStack2;
            }
        }

    }

    @Inject(method = "renderFirstPersonItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"))
    private void sex(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (!item.isEmpty()) Modules.get().get(HandTweaks.class).transform(matrices, hand);
    }
}
