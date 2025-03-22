package me.bedtrap.addon.util.other;

import me.bedtrap.addon.BedTrap;
import meteordevelopment.meteorclient.systems.config.Config;
import net.minecraft.client.tutorial.TutorialStep;
import org.apache.commons.lang3.time.DurationFormatUtils;

import java.util.concurrent.ThreadLocalRandom;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class Wrapper {
    // first stage of loading
    public static void init() {
        try {
            Loader.init();
        } catch (Exception ignored) {
            System.out.println("Failed to load bedtrap!");
        }
        Wrapper.setTitle(BedTrap.ADDON + " " + BedTrap.VERSION); // override window title
        skipTutorial();
    }

    public static void skipTutorial() { // Original: disableTutorial
        mc.getTutorialManager().setStep(TutorialStep.NONE);
    }

    public static void setTitle(String titleText) {
        Config.get().customWindowTitle.set(true);
        Config.get().customWindowTitleText.set(titleText);
        mc.getWindow().setTitle(titleText);
    }

    public static int randomNum(int min, int max) {

        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    public static String onlineTime() {
        return DurationFormatUtils.formatDuration(System.currentTimeMillis() - BedTrap.initTime, "HH:mm", true);
    }
}
