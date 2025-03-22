package me.bedtrap.addon.modules.hud;

import me.bedtrap.addon.BedTrap;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.elements.TextHud;

public class BedTrapTextHud {
    public static final HudElementInfo<TextHud> INFO = new HudElementInfo<>(BedTrap.BEDTRAP_HUD, "text-hud", "Displays arbitrary text with Starscript.", BedTrapTextHud::create);

    public static final HudElementInfo<TextHud>.Preset CPS;
    public static final HudElementInfo<TextHud>.Preset ONLINE_TIME;
    public static final HudElementInfo<TextHud>.Preset WATERMARK;
    public static final HudElementInfo<TextHud>.Preset WELCOME;
    public static final HudElementInfo<TextHud>.Preset YAW;

    static {
        CPS = addPreset("crystals/PS", "Crystals/S: #1{bedtrap.cps}", 0);
        ONLINE_TIME = addPreset("online-time", "Time of you online: #1{bedtrap.online_time}", 0);
        WATERMARK = addPreset("watermark", "BedTrap #1{bedtrap.version}");
        WELCOME = addPreset("welcome", "#1{bedtrap.mae} #1{player}, keep owning #1{server}", 0);
        YAW = addPreset("rotation-yaw", "Rotation: #1{bedtrap.yaw}", 0);
    }

    private static TextHud create() {
        return new TextHud(INFO);
    }

    private static HudElementInfo<TextHud>.Preset addPreset(String title, String text, int updateDelay) {
        return INFO.addPreset(title, textHud -> {
            if (text != null) textHud.text.set(text);
            if (updateDelay != -1) textHud.updateDelay.set(updateDelay);
        });
    }

    private static HudElementInfo<TextHud>.Preset addPreset(String title, String text) {
        return addPreset(title, text, -1);
    }
}
