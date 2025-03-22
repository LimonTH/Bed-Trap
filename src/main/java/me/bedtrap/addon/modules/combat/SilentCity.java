package me.bedtrap.addon.modules.combat;

import me.bedtrap.addon.BedTrap;
import me.bedtrap.addon.modules.info.Notifications;
import me.bedtrap.addon.util.advanced.CityUtils;
import me.bedtrap.addon.util.advanced.Interaction;
import me.bedtrap.addon.util.advanced.PacketUtils;
import me.bedtrap.addon.util.advanced.RenderUtils;
import me.bedtrap.addon.util.basic.RenderInfo;
import me.bedtrap.addon.util.other.Task;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.entity.SortPriority;
import meteordevelopment.meteorclient.utils.entity.TargetUtils;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

import static me.bedtrap.addon.util.advanced.RenderUtils.render;
import static me.bedtrap.addon.util.basic.BlockInfo.getState;
import static me.bedtrap.addon.util.basic.BlockInfo.isAir;

public class SilentCity extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgRender = settings.createGroup("Render");
    private final SettingGroup sgNone = settings.createGroup("");

    private final Setting<Notifications.Mode> notifications = sgNone.add(new EnumSetting.Builder<Notifications.Mode>().name("notifications").defaultValue(Notifications.Mode.Toast).build());

    private final Setting<Integer> targetRange = sgGeneral.add(new IntSetting.Builder().name("target-range").description("The maximum range to target near player entity.").defaultValue(8).sliderRange(3, 12).build());
    private final Setting<Boolean> rotate = sgGeneral.add(new BoolSetting.Builder().name("rotate").description("Automatically faces towards the blocks being broken.").defaultValue(false).build());
    private final Setting<Boolean> fasterRemove = sgGeneral.add(new BoolSetting.Builder().name("faster-removing").description("Mined blocks will be removed faster.May cause desync if server is strict.").defaultValue(false).build());
    private final Setting<Boolean> packetSwitch = sgGeneral.add(new BoolSetting.Builder().name("packet-switch").description("The packet variation for switching to pickaxe.").defaultValue(false).build());

    private final Setting<Boolean> swing = sgRender.add(new BoolSetting.Builder().name("swing").description("Renders swing client-side.").defaultValue(false).build());
    private final Setting<Boolean> progress = sgRender.add(new BoolSetting.Builder().name("progress").description("Renders client-side block breaking progress.").defaultValue(true).build());
    private final Setting<Boolean> render = sgRender.add(new BoolSetting.Builder().name("render").description("Renders the block that being broken.").defaultValue(true).build());
    private final Setting<ShapeMode> shapeMode = sgRender.add(new EnumSetting.Builder<ShapeMode>().name("shape-mode").description("How the shapes are rendered.").defaultValue(ShapeMode.Both).build());
    private final Setting<SettingColor> sideColor = sgRender.add(new ColorSetting.Builder().name("side-color").description("The side color for positions to be placed.").defaultValue(new SettingColor(255, 0, 170, 10)).build());
    private final Setting<SettingColor> lineColor = sgRender.add(new ColorSetting.Builder().name("line-color").description("The line color for positions to be placed.").defaultValue(new SettingColor(255, 0, 170, 90)).build());
    private final Task silentTask = new Task();
    private final PacketUtils silentMine = new PacketUtils();
    private int timer, prevSlot;
    private BlockPos blockPos;
    private BlockState blockState;
    private FindItemResult tool;
    private PlayerEntity target;

    public SilentCity() {
        super(BedTrap.Combat, "silent-city", "Auto City Breaker based on silent mining.");
    }

    @Override
    public void onActivate() {
        timer = 0;
        blockPos = null;
        blockState = null;
        target = null;

        silentTask.reset();
    }

    @Override
    public void onDeactivate() {
        if (blockPos != null) {
            silentMine.abortMining(blockPos);

            Interaction.updateSlot(prevSlot, packetSwitch.get());

            blockPos = null;
            blockState = null;
            target = null;
            silentMine.reset();
            silentTask.reset();
        }
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        target = TargetUtils.getPlayerTarget(targetRange.get(), SortPriority.LowestDistance);
        if (TargetUtils.isBadTarget(target, targetRange.get())) {
            Notifications.send("The target is too far! disabling...", notifications);
            toggle();
            return;
        }

        tool = InvUtils.findInHotbar(Items.NETHERITE_PICKAXE, Items.DIAMOND_PICKAXE);
        if (!tool.found()) {
            Notifications.send("There's no pickaxe in your hotbar! disabling...", notifications);
            toggle();
            return;
        }

        if (rotate.get() && blockPos != null)
            Rotations.rotate(Rotations.getYaw(blockPos), Rotations.getPitch(blockPos));

        // получение инфы только один раз, что бы при пакет майне не дропало копание
        if (blockPos == null) {
            blockPos = CityUtils.getBreakPos(target);
            timer = 0;
            silentTask.reset();
        }
        if (blockPos == null) {
            Notifications.send("Pos is null, disabling...", notifications);
            toggle();
            return;
        }

        blockState = getState(blockPos);
        tool = InvUtils.findFastestTool(blockState);

        silentMine.mine(blockPos, silentTask);
        if (mc.player.getInventory().selectedSlot != tool.slot()) prevSlot = mc.player.getInventory().selectedSlot;
        if (swing.get() && silentTask.isCalled()) mc.player.swingHand(Hand.MAIN_HAND);
        if (progress.get())
            mc.world.setBlockBreakingInfo(mc.player.getId(), blockPos, (int) (silentMine.getProgress() * 10.0F) - 1);

        if (silentMine.isReadyOn(0.95)) Interaction.updateSlot(tool, packetSwitch.get());
        if (fasterRemove.get() && silentMine.isReady()) mc.world.setBlockState(blockPos, Blocks.AIR.getDefaultState());

        boolean shouldStop = PlayerUtils.distanceTo(blockPos) >= 5 || isBugged();
        if (isAir(blockPos) || shouldStop) {
            if (shouldStop) silentMine.abortMining(blockPos);

            Interaction.updateSlot(prevSlot, packetSwitch.get());

            blockPos = null;
            blockState = null;
            target = null;
            silentMine.reset();
            silentTask.reset();
            toggle();
        }
    }

    @EventHandler
    public void onRender(Render3DEvent event) {
        RenderInfo ri = new RenderInfo(event, RenderUtils.RenderMode.Shape, shapeMode.get());
        if (blockPos == null || !render.get()) return;

        if (!silentMine.isReady()) render(ri, blockPos, sideColor.get(), lineColor.get(), 1);
        else render(ri, blockPos, new Color(255, 0, 0, sideColor.get().a), new Color(255, 0, 0, lineColor.get().a), 1);
    }

    private boolean isBugged() {
        if (!silentMine.isReady()) return false;
        timer++;

        if (timer >= 10) {
            timer = 0;
            return true;
        }

        return false;
    }

    @Override
    public String getInfoString() {
        return (Math.round(silentMine.getProgress() * 100) + "%");
    }
}