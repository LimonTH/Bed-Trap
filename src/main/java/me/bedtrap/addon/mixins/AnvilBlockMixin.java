package me.bedtrap.addon.mixins;

import me.bedtrap.addon.modules.misc.OldAnvil;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AnvilBlock.class)
public class AnvilBlockMixin {

    @Inject(method = "getOutlineShape", at = @At("HEAD"), cancellable = true)
    public void getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir) {
        OldAnvil oldAnvil = Modules.get().get(OldAnvil.class);

        if (oldAnvil.isActive()) {
            cir.setReturnValue(oldAnvil.voxelShape(state));
        }
    }
}
