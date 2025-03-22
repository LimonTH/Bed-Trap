package me.bedtrap.addon.modules.misc;

import me.bedtrap.addon.BedTrap;
import me.bedtrap.addon.util.advanced.RenderUtils;
import me.bedtrap.addon.util.basic.RenderInfo;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.entity.BedBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.Comparator;

import static me.bedtrap.addon.util.advanced.Interaction.*;
import static me.bedtrap.addon.util.advanced.RenderUtils.*;
import static me.bedtrap.addon.util.basic.BlockInfo.*;
import static meteordevelopment.meteorclient.utils.player.PlayerUtils.distanceTo;

public class AutoBedTrap extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgRender = settings.createGroup("Render");

    private final Setting<Double> placeRange = sgGeneral.add(new DoubleSetting.Builder().name("place-range").description("Range in which to place blocks.").defaultValue(4.6).sliderRange(0, 7).build());
    private final Setting<Integer> placeDelay = sgGeneral.add(new IntSetting.Builder().name("place-delay").description("Delay between placing blocks.").defaultValue(1).sliderRange(0, 10).build());
    private final Setting<Boolean> packetPlace = sgGeneral.add(new BoolSetting.Builder().name("packet").description("Using packet interaction instead of client.").defaultValue(false).build());
    private final Setting<Boolean> rotate = sgGeneral.add(new BoolSetting.Builder().name("rotate").description("Rotating to the block.").defaultValue(false).build());
    private final Setting<Boolean> swapBack = sgGeneral.add(new BoolSetting.Builder().name("swap-back").description("Automatically swaps to previous slot.").defaultValue(true).build());

    private final Setting<SwingHand> swing = sgRender.add(new EnumSetting.Builder<SwingHand>().name("swing").description("The way to render swing.").defaultValue(SwingHand.Auto).build());
    private final Setting<Boolean> packetSwing = sgRender.add(new BoolSetting.Builder().name("packet").description("Swings with packets.").defaultValue(false).build());
    private final Setting<RenderUtils.RenderMode> renderMode = sgRender.add(new EnumSetting.Builder<RenderUtils.RenderMode>().name("render").description("How the render will work.").defaultValue(RenderUtils.RenderMode.Box).build());
    private final Setting<Double> height = sgRender.add(new DoubleSetting.Builder().name("height").description("Maximum damage anchors can deal to yourself.").defaultValue(0.99).sliderRange(0, 1).visible(() -> visibleHeight(renderMode.get())).build());
    private final Setting<ShapeMode> shapeMode = sgRender.add(new EnumSetting.Builder<ShapeMode>().name("shape-mode").description("How the shapes are rendered.").defaultValue(ShapeMode.Both).build());
    private final Setting<SettingColor> sideColor = sgRender.add(new ColorSetting.Builder().name("side-color").description("The side color for positions to be placed.").defaultValue(new SettingColor(255, 0, 170, 10)).visible(() -> visibleSide(shapeMode.get())).build());
    private final Setting<SettingColor> lineColor = sgRender.add(new ColorSetting.Builder().name("line-color").description("The line color for positions to be placed.").defaultValue(new SettingColor(255, 0, 170, 90)).visible(() -> visibleLine(shapeMode.get())).build());
    private int placeTimer;

    public AutoBedTrap() {
        super(BedTrap.Misc, "auto-BedTrap", "Automatically places blocks around the bed.");
    }

    @Override
    public void onActivate() {
        placeTimer = 0;
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (placeTimer > 0) {
            placeTimer--;
            return;
        }

        doPlace(InvUtils.findInHotbar(Items.OBSIDIAN), getBedPos());
    }

    private boolean canPlace(BlockPos blockPos) {
        if (isNull(blockPos)) return false;
        if (!isAir(blockPos)) return false;

        return !hasEntity(new Box(blockPos));
    }

    private BlockPos getBedPos() {
        ArrayList<BlockPos> bedPoses = new ArrayList<>();

        for (BlockEntity blockEntity : Utils.blockEntities()) {
            if (blockEntity instanceof BedBlockEntity) {
                for (Direction direction : Direction.values()) {
                    if (distanceTo(blockEntity.getPos().offset(direction)) > placeRange.get()) continue;
                    if (canPlace(blockEntity.getPos().offset(direction)))
                        bedPoses.add(blockEntity.getPos().offset(direction));
                }
            }
        }

        if (bedPoses.isEmpty()) return null;
        bedPoses.sort(Comparator.comparingDouble(PlayerUtils::distanceTo));

        return bedPoses.getFirst();
    }

    private void doPlace(FindItemResult result, BlockPos blockPos) {
        if (isNull(blockPos) || !result.found()) return;
        Hand hand = result.isOffhand() ? Hand.OFF_HAND : Hand.MAIN_HAND;

        if (rotate.get()) Rotations.rotate(Rotations.getYaw(blockPos), Rotations.getPitch(blockPos));
        updateSlot(result, true);
        placeBlock(hand, new BlockHitResult(closestVec3d(blockPos), Direction.DOWN, blockPos, true), packetPlace.get());
        doSwing(swing.get(), packetSwing.get(), result.getHand());
        if (swapBack.get()) swapBack();

        placeTimer = placeDelay.get();
    }

    @EventHandler
    public void onRender(Render3DEvent event) {
        RenderInfo ri = new RenderInfo(event, renderMode.get(), shapeMode.get());

        render(ri, getBedPos(), sideColor.get(), lineColor.get(), height.get());
    }
}
