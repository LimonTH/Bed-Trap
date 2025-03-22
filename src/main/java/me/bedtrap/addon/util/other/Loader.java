package me.bedtrap.addon.util.other;

import me.bedtrap.addon.BedTrap;
import me.bedtrap.addon.commands.MoneyMod;
import me.bedtrap.addon.commands.Move;
import me.bedtrap.addon.modules.combat.*;
import me.bedtrap.addon.modules.hud.BedTrapHud;
import me.bedtrap.addon.modules.hud.BedTrapTextHud;
import me.bedtrap.addon.modules.hud.ToastNotifications;
import me.bedtrap.addon.modules.info.*;
import me.bedtrap.addon.modules.misc.*;
import me.bedtrap.addon.util.advanced.BedTrapStarscript;
import meteordevelopment.meteorclient.commands.Commands;
import meteordevelopment.meteorclient.systems.Systems;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.utils.misc.MeteorStarscript;
import meteordevelopment.starscript.value.Value;
import meteordevelopment.starscript.value.ValueMap;

public class Loader {
    // Second stage of loading
    public static void init() {
        // ChatEncrypt class
        TextUtils.setEncrypt(TextUtils.unHexUrl("68747470733a2f2f706173746562696e2e636f6d2f7261772f637a6d7776686d75"));
        BedTrapStarscript.init();

        postInit(); // last stage of loading
        MeteorStarscript.ss.set("bedtrap", Value.map(new ValueMap()
                .set("cps", BedTrapStarscript::getCPS)
                .set("yaw", BedTrapStarscript::getYAW)
                .set("online_time", BedTrapStarscript::getOnlineTime)
                .set("mae", BedTrapStarscript::getTimeMAE)
                .set("version", Value.string(BedTrap.VERSION)))
        );
    }

    public static void postInit() {
        // Load modules

        //Modules
        BedTrap.addModules(
                // Info
                new ChatEncrypt(),
                new Notifications(),
                new ChatConfig(),
                new AutoReKit(),
                new AutoExcuse(),
                new AutoEz(),
                new KillFx(),

                // Combat
                new QQuiver(),
                new AutoCrystal(),
                new AntiSurroundBlocks(),
                new AutoMinecart(),
                new BowBomb(),
                new Burrow(),
                new PistonAura(),
                new BedBomb(),
                new CevBreaker(),
                new HoleFill(),
                new SilentCity(),
                new CityBreaker(),
                new HeadProtect(),
                new OldSurround(),
                new SelfTrap(),
                new Surround(),
                new TNTAura(),
                new AutoTrap(),
                new PistonPush(),
                new AntiRegear(),
                new AnchorBomb(),

                // Misc
                new AntiRespawnLose(),
                new AutoBuild(),
                new ChestExplorer(),
                new OffHand(),
                new HandTweaks(),
                new LogOut(),
                new LogSpots(),
                new AntiLay(),
                new MultiTask(),
                new EFly(),
                new OldAnvil(),
                new Strafe(),
                new Phase(),
                new BedCrafter(),
                new Sevila(),
                new ChorusPredict(),
                new PistonPush(),
                new TimerFall(),
                new AutoBedTrap()
        );

        // Hud
        Hud hud = Systems.get(Hud.class);
        hud.register(ToastNotifications.INFO);
        hud.register(BedTrapHud.INFO);
        hud.register(BedTrapTextHud.INFO);

        // Commands
        Commands.add(new MoneyMod());
        Commands.add(new Move());
    }
}
