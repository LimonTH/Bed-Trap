package me.bedtrap.addon.modules.info;

import me.bedtrap.addon.BedTrap;
import me.bedtrap.addon.util.other.TextUtils;
import meteordevelopment.meteorclient.events.game.OpenScreenEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringListSetting;
import meteordevelopment.meteorclient.systems.config.Config;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.gui.screen.DeathScreen;

import java.util.List;

import static me.bedtrap.addon.util.other.Wrapper.randomNum;

public class AutoExcuse extends Module {

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<List<String>> messages = sgGeneral.add(new StringListSetting.Builder().name("messages").description("Randomly takes the message from the list and sends on each kill.").defaultValue("GG!", "gg").build());
    private boolean lock = false;
    private int i = 15;

    public AutoExcuse() {
        super(BedTrap.Info, "auto-excuse", "Sends GG message after your death.");
    }

    @EventHandler
    private void onOpenScreenEvent(OpenScreenEvent event) {
        if (!(event.screen instanceof DeathScreen)) return;
        lock = true;
        i = 15;
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (mc.currentScreen instanceof DeathScreen) return;
        if (lock) i--;
        if (lock && i <= 0) {
            TextUtils.sendPlayerMsg(Config.get().prefix + "say " + getMessage());
            lock = false;
            i = 15;
        }
    }

    private String getMessage() {
        return messages.get().isEmpty() ? "well played" : messages.get().get(randomNum(0, messages.get().size() - 1));
    }
}
