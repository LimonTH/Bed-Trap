package me.bedtrap.addon.modules.misc;

import me.bedtrap.addon.BedTrap;
import me.bedtrap.addon.events.InteractEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;

public class MultiTask extends Module {
    public MultiTask() {
        super(BedTrap.Misc, "multi-task+", "Allows you to eat while mining a block.");
    }

    @EventHandler
    public void onInteractEvent(InteractEvent event) {
        event.usingItem = false;
    }
}
