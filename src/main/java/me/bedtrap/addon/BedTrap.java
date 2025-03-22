package me.bedtrap.addon;

import me.bedtrap.addon.util.other.Wrapper;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.config.Config;
import meteordevelopment.meteorclient.systems.hud.HudGroup;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.item.Items;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

public class BedTrap extends MeteorAddon {
    public static final String ADDON = "BedTrap";
    public static final String VERSION = "5.0";
    public static final HudGroup BEDTRAP_HUD = new HudGroup("BedTrap");
    public static long initTime;

    public static final Category Combat = new Category("Combat+", Items.RED_BED.getDefaultStack());
    public static final Category Misc = new Category("Misc+", Items.BLUE_BED.getDefaultStack());
    public static final Category Info = new Category("Info", Items.WHITE_BED.getDefaultStack());

    public static final Logger LOG = LogManager.getLogger();
    public static final File FOLDER = new File(System.getProperty("user.home"), "BedTrapEx");

    public static void log(String message) {
        LOG.log(Level.INFO, "[" + BedTrap.ADDON + "] " + message);
    }

    @Override
    public void onInitialize() {
        initTime = System.currentTimeMillis();
        if (!FOLDER.exists()) {
            boolean created = FOLDER.mkdirs();
            if (created) {
                log("Directory created: " + FOLDER.getAbsolutePath());
            } else {
                log("Failed to create directory: " + FOLDER.getAbsolutePath());
            }
        }
        log("Initializing " + ADDON + " " + VERSION);
        Wrapper.init();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log("Saving config...");
            Config.get().save();
        }));
    }

    @Override
    public void onRegisterCategories() {
        Modules.registerCategory(Combat);
        Modules.registerCategory(Misc);
        Modules.registerCategory(Info);
    }

    @Override
    public String getPackage() {
        return "me.bedtrap.addon";
    }

    public static void addModules(Module... module) {
        for (Module module1 : module) {
            Modules.get().add(module1);
        }
    }
}
