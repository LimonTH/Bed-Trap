package me.bedtrap.addon.modules.combat;

import me.bedtrap.addon.BedTrap;
import me.bedtrap.addon.util.advanced.Interaction;
import me.bedtrap.addon.util.advanced.PacketUtils;
import me.bedtrap.addon.util.other.Task;
import meteordevelopment.meteorclient.events.entity.player.PlaceBlockEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.EnderChestBlock;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Comparator;

import static me.bedtrap.addon.util.basic.BlockInfo.*;

public class AntiRegear extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Integer> radius = sgGeneral.add(new IntSetting.Builder().name("radius").description("The radius of the sphere around you.").defaultValue(5).sliderRange(1, 10).build());
    private final Setting<Boolean> own = sgGeneral.add(new BoolSetting.Builder().name("own").description("Whether or not to break your own blocks.").defaultValue(false).build());
    private final ArrayList<BlockPos> ownBlocks = new ArrayList<>();
    private final Task mine = new Task();
    private final PacketUtils packetMine = new PacketUtils();
    private FindItemResult tool;
    private BlockPos currentPos;
    private BlockState currentState;
    private int timer;
    public AntiRegear() {
        super(BedTrap.Combat, "anti-regear", "Automatically breaks shulkers and EChests which was placed by enemy.");
    }

    @Override
    public void onActivate() {
        timer = 0;
        currentPos = null;
        currentState = null;
        ownBlocks.clear();

        mine.reset();
    }

    @EventHandler
    public void onPlace(PlaceBlockEvent event) {
        if (event.block instanceof ShulkerBoxBlock || event.block instanceof EnderChestBlock) {
            ownBlocks.add(event.blockPos);
        }
    }

    @EventHandler
    public void onTick(TickEvent.Pre event) {
        if (getBlocks(radius.get()).isEmpty()) return;

        if (currentPos == null) {
            getBlocks(radius.get()).forEach(blockPos -> {
                currentPos = blockPos;
                currentState = getState(currentPos);
            });
        } else {
            tool = InvUtils.findFastestTool(currentState);
            if (!tool.found()) return;

            packetMine.mine(currentPos, mine);
            mc.world.setBlockBreakingInfo(mc.player.getId(), currentPos, (int) (packetMine.getProgress() * 10.0F) - 1);

            if (packetMine.isReadyOn(0.95)) Interaction.updateSlot(tool.slot(), false);

            boolean shouldStop = PlayerUtils.distanceTo(currentPos) > 5 || isBugged();
            if (isAir(currentPos) || shouldStop) {
                if (shouldStop) packetMine.abortMining(currentPos);
                currentPos = null;
                currentState = null;
                packetMine.reset();
                mine.reset();
            }
        }
    }

    private ArrayList<BlockPos> getBlocks(int radius) {
        ArrayList<BlockPos> sphere = new ArrayList<>(getSphere(mc.player.getBlockPos(), radius, radius));
        ArrayList<BlockPos> blocks = new ArrayList<>();

        for (BlockPos blockPos : sphere) {
            if (isAir(blockPos)) continue;
            if (!own.get() && ownBlocks.contains(blockPos)) continue;

            if (!blocks.contains(blockPos) && mc.world.getBlockState(blockPos).getBlock() instanceof ShulkerBoxBlock || mc.world.getBlockState(blockPos).getBlock() == Blocks.ENDER_CHEST) {
                blocks.add(blockPos);
            }
        }

        blocks.sort(Comparator.comparingDouble(PlayerUtils::distanceTo));
        return blocks;
    }

    private boolean isBugged() {
        if (!packetMine.isReady()) return false;
        timer++;

        if (timer >= 10) {
            timer = 0;
            return true;
        }

        return false;
    }

    @EventHandler
    public void onRender(Render3DEvent event) {
        if (getBlocks(5).isEmpty()) return;

        getBlocks(5).forEach(blockPos -> event.renderer.box(blockPos, new Color(123, 123, 123, 160), new Color(123, 123, 123, 160), ShapeMode.Both, 0));
    }
}
