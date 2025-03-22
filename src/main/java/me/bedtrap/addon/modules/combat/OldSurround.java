package me.bedtrap.addon.modules.combat;

import me.bedtrap.addon.BedTrap;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.misc.Pool;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.meteorclient.utils.world.CardinalDirection;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.TeleportConfirmC2SPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static me.bedtrap.addon.util.basic.EntityInfo.isSurrounded;


public class OldSurround extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgPyramid = settings.createGroup("Pyramid");
    private final SettingGroup sgCrystalBreaker = settings.createGroup("Crystal Breaker");
    private final SettingGroup sgMisc = settings.createGroup("Misc");
    private final SettingGroup sgRender = settings.createGroup("Render");

    // General
    private final Setting<List<Block>> blocks = sgGeneral.add(new BlockListSetting.Builder().name("blocks").description("What blocks to use for surround.").defaultValue(Collections.singletonList(Blocks.OBSIDIAN)).filter(this::blockFilter).build());
    private final Setting<Boolean> ignoreEntities = sgGeneral.add(new BoolSetting.Builder().name("ignore-entities").description("Tries to place on entities, recomended for high ping players.").defaultValue(true).build());
    private final Setting<Boolean> antiSupport = sgGeneral.add(new BoolSetting.Builder().name("anti-support").description("Automatically places EChest under surround.").defaultValue(false).build());

    // Pyramid
    private final Setting<Boolean> pyramid = sgPyramid.add(new BoolSetting.Builder().name("pyramid").description("Russian surround.").defaultValue(false).build());
    private final Setting<Integer> pyramidDelay = sgPyramid.add(new IntSetting.Builder().name("delay").description("The speed at which you rotate.").defaultValue(0).sliderRange(0, 20).visible(pyramid::get).build());
    int count = pyramidDelay.get();
    // Crystal Breaker
    private final Setting<CrystalMode> mode = sgCrystalBreaker.add(new EnumSetting.Builder<CrystalMode>().name("crystal-breaker").description("Breaks crystals in range.").defaultValue(CrystalMode.Legs).build());
    private final Setting<CrystalMode> obbyPlaceMode = sgCrystalBreaker.add(new EnumSetting.Builder<CrystalMode>().name("place-mode").description("Places obsidian in crystal position.").defaultValue(CrystalMode.Legs).visible(() -> mode.get() != CrystalMode.None).build());
    private final Setting<HitMode> hitMode = sgCrystalBreaker.add(new EnumSetting.Builder<HitMode>().name("hit-mode").description("The way to interact with crystal.").defaultValue(HitMode.Default).visible(() -> mode.get() != CrystalMode.None).build());
    private final Setting<Boolean> onlyHole = sgCrystalBreaker.add(new BoolSetting.Builder().name("hole-only").description("Woks only if player is in hole.").defaultValue(false).visible(() -> mode.get() != CrystalMode.None).build());
    private final Setting<Double> breakRange = sgCrystalBreaker.add(new DoubleSetting.Builder().name("range").description("The speed at which you rotate.").defaultValue(2.7).sliderRange(0, 7).visible(() -> mode.get() != CrystalMode.None).build());
    private final Setting<Integer> crystalAge = sgCrystalBreaker.add(new IntSetting.Builder().name("crystal-age").description("The speed at which you rotate.").defaultValue(1).sliderRange(0, 10).visible(() -> mode.get() != CrystalMode.None).build());
    // Misc
    private final Setting<TpMode> centerMode = sgMisc.add(new EnumSetting.Builder<TpMode>().name("center-mode").description("Teleports you to the center of the block.").defaultValue(TpMode.Default).build());
    private final Setting<Integer> centerDelay = sgMisc.add(new IntSetting.Builder().name("delay").description("Delay for teleporting to center.").defaultValue(5).sliderRange(1, 20).visible(() -> centerMode.get() == TpMode.Smooth).build());
    private final Setting<Boolean> rotate = sgMisc.add(new BoolSetting.Builder().name("rotate").description("Automatically faces towards the obsidian being placed.").defaultValue(false).build());
    private final Setting<Boolean> antiGhost = sgMisc.add(new BoolSetting.Builder().name("anti-ghost").description("Removing client-side surround blocks.").defaultValue(true).build());
    private final Setting<Boolean> onlyOnGround = sgMisc.add(new BoolSetting.Builder().name("only-on-ground").description("Works only when you standing on blocks.").defaultValue(true).build());
    private final Setting<Boolean> disableOnJump = sgMisc.add(new BoolSetting.Builder().name("disable-on-jump").description("Automatically disables when you jump.").defaultValue(true).build());
    private final Setting<Boolean> disableOnTp = sgMisc.add(new BoolSetting.Builder().name("disable-on-tp").description("Automatically disables when you teleporting (like using chorus or pearl).").defaultValue(true).build());
    private final Setting<Boolean> disableOnYChange = sgMisc.add(new BoolSetting.Builder().name("disable-on-y-change").description("Automatically disables when your y level (step, jumping, atc).").defaultValue(true).build());
    private final Setting<Boolean> render = sgRender.add(new BoolSetting.Builder().name("render").description("Renders an overlay where blocks will be placed.").defaultValue(true).build());
    private final Setting<ShapeMode> shapeMode = sgRender.add(new EnumSetting.Builder<ShapeMode>().name("shape-mode").description("How the shapes are rendered.").defaultValue(ShapeMode.Both).build());
    private final Setting<SettingColor> sideColor = sgRender.add(new ColorSetting.Builder().name("side-color").description("The side color of the target block rendering.").defaultValue(new SettingColor(197, 137, 232, 10)).build());
    private final Setting<SettingColor> lineColor = sgRender.add(new ColorSetting.Builder().name("line-color").description("The line color of the target block rendering.").defaultValue(new SettingColor(197, 137, 232)).build());
    private final Pool<RenderBlock> renderBlockPool = new Pool<>(RenderBlock::new);
    private final List<RenderBlock> renderBlocks = new ArrayList<>();
    private final BlockPos.Mutable renderPos = new BlockPos.Mutable();
    private final BlockPos.Mutable blockPos = new BlockPos.Mutable();
    BlockPos pos;
    private boolean crystalRemoved = false;
    private boolean doReturn;
    private int centerDelayLeft;

    public OldSurround() {
        super(BedTrap.Combat, "old-surround", "Surrounds you in blocks to prevent you from taking lots of damage.");
    }

    @Override
    public void onActivate() {
        centerDelayLeft = 0;
        switch (centerMode.get()) {
            case Default -> PlayerUtils.centerPlayer();
            case Smooth -> {
                centerDelayLeft = centerDelay.get();
                if (inCenter()) {
                    centerDelayLeft = 0;
                }
            }
        }

        for (RenderBlock renderBlock : renderBlocks) renderBlockPool.free(renderBlock);
        renderBlocks.clear();
    }

    @Override
    public void onDeactivate() {
        for (RenderBlock renderBlock : renderBlocks) renderBlockPool.free(renderBlock);
        renderBlocks.clear();
    }

    @EventHandler
    public void onPacket(PacketEvent.Receive event) {
        if (!antiGhost.get()) return;

        if (!(event.packet instanceof EntityStatusS2CPacket status)) return;
        if (status.getStatus() != 35) return;
        Entity entity = status.getEntity(mc.world);
        if (!(entity instanceof PlayerEntity) || !entity.equals(mc.player)) return;

        for (Direction direction : Direction.values()) {
            mc.player.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, mc.player.getBlockPos().offset(direction), Direction.UP));
        }
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        // Teleport
        teleport();

        // Render
        renderBlocks.forEach(RenderBlock::tick);
        renderBlocks.removeIf(renderBlock -> renderBlock.ticks <= 0);

        for (CardinalDirection direction : CardinalDirection.values()) {
            if (blockFilter(mc.world.getBlockState(mc.player.getBlockPos().offset(direction.toDirection())).getBlock())) {
                renderPos.set(mc.player.getBlockPos().offset(direction.toDirection()));
                renderBlocks.add(renderBlockPool.get().set(renderPos));
            }
        }

        if ((disableOnJump.get() && (mc.options.jumpKey.isPressed() || mc.player.input.jumping)) || (disableOnYChange.get() && mc.player.prevY < mc.player.getY())) {
            toggle();
            return;
        }

        if (onlyOnGround.get() && !mc.player.isOnGround()) return;

        if (antiGhost.get()) {
            for (Entity entity : mc.world.getEntities()) {
                if (entity instanceof EndCrystalEntity crystal && mc.player.distanceTo(crystal) < 3) {
                    BlockPos pos = crystal.getBlockPos();

                    for (Direction direction : Direction.values()) {
                        if (mc.player.getBlockPos().offset(direction).equals(pos) &&
                                blockFilter(mc.world.getBlockState(mc.player.getBlockPos().offset(direction)).getBlock())) {
                            mc.world.setBlockState(pos, Blocks.AIR.getDefaultState());
                        }
                    }
                }
            }
        }

        instantPlace(0, -1, 0);
        instantPlace(1, 0, 0);
        instantPlace(-1, 0, 0);
        instantPlace(0, 0, 1);
        instantPlace(0, 0, -1);

        // Place
        doReturn = false;

        if (pyramid.get()) {
            if (count == 0) {
                count = pyramidDelay.get();
            } else {
                count--;
                return;
            }

            defaultPlace(0, -2, 0);
            if (doReturn) return;
            defaultPlace(1, -1, 0);
            if (doReturn) return;
            defaultPlace(-1, -1, 0);
            if (doReturn) return;
            defaultPlace(0, -1, 1);
            if (doReturn) return;
            defaultPlace(0, -1, -1);
            if (doReturn) return;
            defaultPlace(1, 0, 1);
            if (doReturn) return;
            defaultPlace(-1, 0, -1);
            if (doReturn) return;
            defaultPlace(-1, 0, 1);
            if (doReturn) return;
            defaultPlace(1, 0, -1);
            if (doReturn) return;
            defaultPlace(2, 0, 0);
            if (doReturn) return;
            defaultPlace(-2, 0, 0);
            if (doReturn) return;
            defaultPlace(0, 0, 2);
            if (doReturn) return;
            defaultPlace(0, 0, -2);
            if (doReturn) return;
            defaultPlace(1, 1, 0);
            if (doReturn) return;
            defaultPlace(-1, 1, 0);
            if (doReturn) return;
            defaultPlace(0, 1, 1);
            if (doReturn) return;
            defaultPlace(0, 1, -1);
            if (doReturn) return;
            defaultPlace(1, 2, 0);
            if (doReturn) return;
            defaultPlace(-1, 2, 0);
            if (doReturn) return;
            defaultPlace(0, 2, 1);
            if (doReturn) return;
            defaultPlace(0, 2, -1);
            if (doReturn) return;
            defaultPlace(0, 2, 0);
            if (doReturn) return;
            defaultPlace(0, 3, 0);
        }

        if (antiSupport.get()) {
            placeDown(1, -1, 0);
            if (doReturn) return;
            placeDown(-1, -1, 0);
            if (doReturn) return;
            placeDown(0, -1, 1);
            if (doReturn) return;
            placeDown(0, -1, -1);
        }

        if (mode.get() == CrystalMode.None || (onlyHole.get() && !isSurrounded(mc.player))) return;
        for (Entity crystal : mc.world.getEntities()) {
            if (crystal instanceof EndCrystalEntity && mc.player.distanceTo(crystal) < breakRange.get() && crystal.age >= crystalAge.get()) {
                if (mode.get() == CrystalMode.Legs && crystal.getBlockPos().getY() > mc.player.getBlockPos().getY())
                    return;
                pos = crystal.getBlockPos();
                switch (mode.get()) {
                    case Always -> attack(crystal);
                    case Legs -> {
                        if (pos.getY() <= mc.player.getBlockPos().getY())
                            attack(crystal);
                    }
                }
                crystalRemoved = true;
            } else if (crystalRemoved) {
                switch (obbyPlaceMode.get()) {
                    case Always -> place();
                    case Legs -> {
                        if (pos.getY() <= mc.player.getBlockPos().getY())
                            place();
                    }
                }
                crystalRemoved = false;
            }
        }
    }

    private void attack(Entity target) {
        switch (hitMode.get()) {
            case Default -> mc.interactionManager.attackEntity(mc.player, target);
            case Packet ->
                    mc.player.networkHandler.sendPacket(PlayerInteractEntityC2SPacket.attack(target, mc.player.isSneaking()));
            case Both -> {
                mc.interactionManager.attackEntity(mc.player, target);
                mc.player.networkHandler.sendPacket(PlayerInteractEntityC2SPacket.attack(target, mc.player.isSneaking()));
            }
        }
    }

    @EventHandler
    private void onSendPacket(PacketEvent.Send event) {
        if (event.packet instanceof TeleportConfirmC2SPacket && disableOnTp.get()) toggle();
    }

    private void teleport() {
        if (centerMode.get() == TpMode.Smooth) {
            if (centerDelayLeft > 0) {
                pause();
                assert mc.player != null;
                double decrX = MathHelper.floor(mc.player.getX()) + 0.5 - mc.player.getX();
                double decrZ = MathHelper.floor(mc.player.getZ()) + 0.5 - mc.player.getZ();
                double sqrtPos = Math.sqrt(Math.pow(decrX, 2.0) + Math.pow(decrZ, 2.0));
                double div = Math.sqrt(0.5) / centerDelay.get();
                if (sqrtPos <= div) {
                    centerDelayLeft = 0;
                    double x = MathHelper.floor(mc.player.getX()) + 0.5;
                    double z = MathHelper.floor(mc.player.getZ()) + 0.5;
                    mc.player.setPosition(x, mc.player.getY(), z);
                    return;
                }
                double x = mc.player.getX();
                double z = mc.player.getZ();
                double incX = MathHelper.floor(mc.player.getX()) + 0.5;
                double incZ = MathHelper.floor(mc.player.getZ()) + 0.5;
                double incResult = 0.0;
                double decrResult = 0.0;
                double x_ = mc.player.getX();
                double z_ = mc.player.getZ();
                if (Math.sqrt(Math.pow(decrX, 2.0)) > Math.sqrt(Math.pow(decrZ, 2.0))) {
                    if (decrX > 0.0) {
                        incResult = 0.5 / centerDelay.get();
                    } else if (decrX < 0.0) {
                        incResult = -0.5 / centerDelay.get();
                    }
                    x_ = mc.player.getX() + incResult;
                    z_ = z(x, z, incX, incZ, x_);
                } else if (Math.sqrt(Math.pow(decrX, 2.0)) < Math.sqrt(Math.pow(decrZ, 2.0))) {
                    if (decrZ > 0.0) {
                        decrResult = 0.5 / centerDelay.get();
                    } else if (decrZ < 0.0) {
                        decrResult = -0.5 / centerDelay.get();
                    }
                    z_ = mc.player.getZ() + decrResult;
                    x_ = x(x, z, incX, incZ, z_);
                } else if (Math.sqrt(Math.pow(decrX, 2.0)) == Math.sqrt(Math.pow(decrZ, 2.0))) {
                    if (decrX > 0.0) {
                        incResult = 0.5 / (double) centerDelay.get();
                    } else if (decrX < 0.0) {
                        incResult = -0.5 / (double) centerDelay.get();
                    }
                    x_ = mc.player.getX() + incResult;
                    if (decrZ > 0.0) {
                        decrResult = 0.5 / (double) centerDelay.get();
                    } else if (decrZ < 0.0) {
                        decrResult = -0.5 / (double) centerDelay.get();
                    }
                    z_ = mc.player.getZ() + decrResult;
                }
                pause();
                mc.player.setPosition(x_, mc.player.getY(), z_);
            }
        }
    }

    private boolean blockFilter(Block block) {
        return block.getBlastResistance() >= 600 && block.getHardness() > 0;
    }

    private boolean defaultPlace(int x, int y, int z) {
        setBlockPos(x, y, z);
        BlockState blockState = mc.world.getBlockState(blockPos);

        if (!blockState.isReplaceable()) return true;

        if (ignoreEntities.get()) {
            if (BlockUtils.place(blockPos, InvUtils.findInHotbar(itemStack -> blocks.get().contains(Block.getBlockFromItem(itemStack.getItem()))), rotate.get(), 100, false)) {
                doReturn = true;
            }
        } else {
            if (BlockUtils.place(blockPos, InvUtils.findInHotbar(itemStack -> blocks.get().contains(Block.getBlockFromItem(itemStack.getItem()))), rotate.get(), 100, true)) {
                doReturn = true;
            }
        }
        return false;
    }

    private void instantPlace(int x, int y, int z) {
        BlockUtils.place(mc.player.getBlockPos().add(x, y, z), InvUtils.findInHotbar(itemStack -> blocks.get().contains(Block.getBlockFromItem(itemStack.getItem()))), rotate.get(), 100, !ignoreEntities.get());
    }

    private boolean placeDown(int x, int y, int z) {
        setBlockPos(x, y, z);
        BlockState blockState = mc.world.getBlockState(blockPos);

        if (!blockState.isReplaceable()) return true;

        if (BlockUtils.place(blockPos, InvUtils.findInHotbar(Items.ENDER_CHEST), rotate.get(), 100, true)) {
            doReturn = true;
        }
        return false;
    }

    private void setBlockPos(int x, int y, int z) {
        blockPos.set(mc.player.getX() + x, mc.player.getY() + y, mc.player.getZ() + z);
    }

    public void place() {
        BlockUtils.place(pos, InvUtils.find(Items.OBSIDIAN), false, 50, !ignoreEntities.get());
    }

    private double z(double a, double b, double c, double d, double e) {
        return (e - a) * (d - b) / (c - a) + b;
    }

    private double x(double a, double b, double c, double d, double e) {
        return (e - b) * (c - a) / (d - b) + a;
    }

    private void pause() {
        mc.options.jumpKey.setPressed(false);
        mc.options.sprintKey.setPressed(false);
        mc.options.forwardKey.setPressed(false);
        mc.options.backKey.setPressed(false);
        mc.options.leftKey.setPressed(false);
        mc.options.rightKey.setPressed(false);
    }

    private boolean inCenter() {
        if (mc.player == null) {
            return false;
        }
        if (mc.world == null) {
            return false;
        }
        if (mc.interactionManager == null) {
            return false;
        }
        int count = 0;
        if (mc.player.getBlockPos().equals(new BlockPos((int) (mc.player.getX() - (mc.player.getWidth() + 0.1) / 2.0), (int) mc.player.getY(), (int) (mc.player.getZ() - (mc.player.getWidth() + 0.1) / 2.0)))) {
            count++;
        }
        if (mc.player.getBlockPos().equals(new BlockPos((int) (mc.player.getX() + (mc.player.getWidth() + 0.1) / 2.0), (int) mc.player.getY(), (int) (mc.player.getZ() + (mc.player.getWidth() + 0.1) / 2.0)))) {
            count++;
        }
        if (mc.player.getBlockPos().equals(new BlockPos((int) (mc.player.getX() - (mc.player.getWidth() + 0.1) / 2.0), (int) mc.player.getY(), (int) (mc.player.getZ() + (mc.player.getWidth() + 0.1) / 2.0)))) {
            count++;
        }
        if (mc.player.getBlockPos().equals(new BlockPos((int) (mc.player.getX() + (mc.player.getWidth() + 0.1) / 2.0), (int) mc.player.getY(), (int) (mc.player.getZ() - (mc.player.getWidth() + 0.1) / 2.0)))) {
            count++;
        }
        return count == 4;
    }

    @EventHandler
    private void onRender(Render3DEvent event) {
        if (!render.get()) return;

        renderBlocks.sort(Comparator.comparingInt(o -> -o.ticks));
        renderBlocks.forEach(renderBlock -> renderBlock.render(event, sideColor.get(), lineColor.get(), shapeMode.get()));
    }

    public enum Mode {
        Default, Instant
    }

    public enum BottomMode {
        Default, Instant, None
    }

    public enum PlaceMode {
        Default, Custom, None
    }

    public enum CrystalMode {
        Always, Legs, None
    }

    public enum HitMode {
        Default, Packet, Both
    }

    public enum TpMode {
        Default, Smooth, None
    }

    public static class RenderBlock {
        public BlockPos.Mutable pos = new BlockPos.Mutable();
        public int ticks;

        public RenderBlock set(BlockPos blockPos) {
            pos.set(blockPos);
            ticks = 8;

            return this;
        }

        public void tick() {
            ticks--;
        }

        public void render(Render3DEvent event, Color sides, Color lines, ShapeMode shapeMode) {
            int preSideA = sides.a;
            int preLineA = lines.a;

            sides.a *= (double) ticks / 8;
            lines.a *= (double) ticks / 8;

            event.renderer.box(pos, sides, lines, shapeMode, 0);

            sides.a = preSideA;
            lines.a = preLineA;
        }
    }
}
