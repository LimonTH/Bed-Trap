package me.bedtrap.addon.util.advanced;

import me.bedtrap.addon.util.other.Wrapper;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.game.GameJoinedEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.starscript.value.Value;
import net.minecraft.item.Items;
import net.minecraft.util.math.MathHelper;

import java.time.LocalTime;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class BedTrapStarscript {
    public static void init() {
        MeteorClient.EVENT_BUS.subscribe(BedTrapStarscript.class);
    }
    // Fields
    public static Integer cps = 0;
    public static String online_time = "";
    public static String yaw = "";
    public static String mae = "";

    private static int ticksPassed;
    public static int first;

    // Starscript Methods
    public static Value getCPS() {
        return Value.number(cps);
    }
    public static Value getOnlineTime() {
        return Value.string(online_time);
    }
    public static Value getYAW() {
        return Value.string(yaw);
    }
    public static Value getTimeMAE() {
        return Value.string(mae);
    }

    @EventHandler
    private static void onGameJoin(GameJoinedEvent event) {
        cps = 0;
        online_time = "00:00";
        yaw = "Invalid";
        mae = getMae();
    }

    @EventHandler
    private static void onTick(TickEvent.Pre event) {
        if (!Utils.canUpdate()) return;
        online_time = Wrapper.onlineTime();
        yaw = getTowards();
        mae = getMae();

        if (ticksPassed < 21) ticksPassed++;
            else {
                ticksPassed = 0;
            }

            if (ticksPassed == 1) first = InvUtils.find(Items.END_CRYSTAL).count();

            if (ticksPassed == 21) {
                int second = InvUtils.find(Items.END_CRYSTAL).count();
                int difference = -(second - first);
                cps = Math.max(0, difference);
            }
        }

    private static String getTowards() {
        return switch (MathHelper.floor((double) (mc.player.getYaw() * 8.0F / 360.0F) + 0.5D) & 7) {
            case 0 -> "+Z";
            case 1 -> "-X +Z";
            case 2 -> "-X";
            case 3 -> "-X -Z";
            case 4 -> "-Z";
            case 5 -> "+X -Z";
            case 6 -> "+X";
            case 7 -> "+X +Z";
            default -> "Invalid";
        };
    }
    private static String getMae() {
        int hours = LocalTime.now().getHour();
        if (mc.player == null || mc.world == null) return "Hello";
        if (hours < 11) {
            return "Good morning";
        } else if (hours < 17) {
            return "Good afternoon";
        } else if (hours < 22) {
            return "Good evening";
        } else {
            return "Good night";
        }
    }
}