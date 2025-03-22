package me.bedtrap.addon.modules.info;

import me.bedtrap.addon.BedTrap;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;

import static me.bedtrap.addon.util.basic.BlockInfo.*;
import static me.bedtrap.addon.util.basic.EntityInfo.getBlockPos;

public class KillFx extends Module {
    private boolean lightningOnceFlag = false;

    public KillFx() {
        super(BedTrap.Info, "kill-fx", "Unique player death effects.");
    }

    public void onKill(PlayerEntity player) {
        if (isActive()) {
            spawnLightning(player);
        }
    }

    private void spawnLightning(PlayerEntity player) {
        BlockPos blockPos = getBlockPos(player);

        double x = X(blockPos);
        double y = Y(blockPos);
        double z = Z(blockPos);

        LightningEntity lightningEntity = new LightningEntity(EntityType.LIGHTNING_BOLT, mc.world);

        lightningEntity.updatePosition(x, y, z);
        lightningEntity.refreshPositionAfterTeleport(x, y, z);

        mc.world.addEntity(lightningEntity);

        if (!lightningOnceFlag) {
            mc.world.playSound(mc.player, x, y, z, SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.WEATHER, 10000.0F, 0.8F * 0.2F);
            mc.world.playSound(mc.player, x, y, z, SoundEvents.ENTITY_LIGHTNING_BOLT_IMPACT, SoundCategory.WEATHER, 2.0F, 0.5F * 0.2F);
            lightningOnceFlag = true;
        }
    }
}
