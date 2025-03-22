package me.bedtrap.addon.modules.misc;

import me.bedtrap.addon.BedTrap;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.EntityPose;
import net.minecraft.item.Items;

public class AntiLay extends Module {
    public AntiLay() {
        super(BedTrap.Misc, "anti-lay", "Prevents from laying due blocks in head position.");
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (mc.player.getInventory().getArmorStack(2).getItem() == Items.ELYTRA && mc.player.isFallFlying()) return;
        mc.player.setPose(EntityPose.STANDING);
    }
}
