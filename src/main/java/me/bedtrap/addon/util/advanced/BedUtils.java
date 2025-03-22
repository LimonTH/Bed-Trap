package me.bedtrap.addon.util.advanced;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import me.bedtrap.addon.modules.combat.BedBomb;
import me.bedtrap.addon.util.other.Task;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.game.GameJoinedEvent;
import meteordevelopment.meteorclient.events.render.Render2DEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.mixininterface.IExplosion;
import meteordevelopment.meteorclient.mixininterface.IRaycastContext;
import meteordevelopment.meteorclient.mixininterface.IVec3d;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.renderer.text.TextRenderer;
import meteordevelopment.meteorclient.utils.PreInit;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.render.NametagUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.meteorclient.utils.world.CardinalDirection;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.DamageUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.explosion.Explosion;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.List;

import static me.bedtrap.addon.util.advanced.PacketUtils.startPacketMine;
import static me.bedtrap.addon.util.advanced.PredictionUtils.returnPredictBox;
import static me.bedtrap.addon.util.advanced.PredictionUtils.returnPredictVec;
import static me.bedtrap.addon.util.basic.BlockInfo.*;
import static me.bedtrap.addon.util.basic.EntityInfo.*;
import static meteordevelopment.meteorclient.MeteorClient.mc;

public class BedUtils {
    private static final Vec3d vec3d = new Vec3d(0, 0, 0);
    private static Explosion explosion;
    private static RaycastContext raycastContext;

    @PreInit
    public static void init() {
        MeteorClient.EVENT_BUS.subscribe(BedUtils.class);
        MeteorClient.EVENT_BUS.subscribe(CrystalUtils.class);
    }

    @EventHandler
    private static void onGameJoined(GameJoinedEvent event) {
        explosion = new Explosion(mc.world, null, 0, 0, 0, 6, false, Explosion.DestructionType.DESTROY);
        raycastContext = new RaycastContext(null, null, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.ANY, mc.player);
    }

    public static boolean canBed(BlockPos canPlace, BlockPos replace) {
        return getBlock(canPlace) instanceof BedBlock && getBlock(replace) instanceof BedBlock || BlockUtils.canPlace(canPlace) && mc.world.getBlockState(replace).isReplaceable();
    }

    public static void packetMine(BlockPos blockpos, boolean autoSwap, Task task) {
        task.run(() -> {
            FindItemResult best = InvUtils.findFastestTool(getState(blockpos));
            if (!best.found()) return;
            if (autoSwap) InvUtils.swap(best.slot(), false);
            startPacketMine(blockpos, true);
        });
    }

    public static void normalMine(BlockPos blockpos, boolean autoSwap) {
        FindItemResult best = InvUtils.findFastestTool(getState(blockpos));
        if (!best.found()) return;
        if (autoSwap) InvUtils.swap(best.slot(), false);
        BlockUtils.breakBlock(blockpos, false);
    }

    public static ArrayList<BlockPos> getTargetSphere(PlayerEntity target, int xRadius, int yRadius) {
        ArrayList<BlockPos> al = new ArrayList<>();
        BlockPos tPos = getBlockPos(target);
        BlockPos.Mutable p = new BlockPos.Mutable();

        for (int x = -xRadius; x <= xRadius; x++) {
            for (int y = -yRadius; y <= yRadius; y++) {
                for (int z = -xRadius; z <= xRadius; z++) {
                    p.set(tPos).move(x, y, z);
                    if (MathHelper.sqrt((float) ((tPos.getX() - p.toImmutable().getX()) * (tPos.getX() - p.toImmutable().getX()) + (tPos.getZ() - p.toImmutable().getZ()) * (tPos.getZ() - p.toImmutable().getZ()))) <= xRadius && MathHelper.sqrt((float) ((tPos.getY() - p.toImmutable().getY()) * (tPos.getY() - p.toImmutable().getY()))) <= yRadius) {
                        if (!al.contains(p.toImmutable())) al.add(p.toImmutable());
                    }
                }
            }
        }
        return al;
    }

    public static BlockPos getTrapBlock(PlayerEntity target, double distance) {
        if (target == null) return null;
        for (Direction direction : Direction.values()) {
            if (direction == Direction.UP || direction == Direction.DOWN) continue;

            BlockPos pos = getBlockPos(target).up().offset(direction);

            if (isCombatBlock(pos) && isWithinRange(pos, distance))
                return pos;
        }
        return null;
    }

    public static boolean shouldBurrowBreak() {
        BlockPos b = getBlockPos(BedBomb.target);
        return isCombatBlock(b) && mc.player.getPos().distanceTo(getCenterVec3d(b)) < 4.5;
    }

    public static boolean shouldTrapBreak() {
        return isSurrounded(BedBomb.target) && isTrapped(BedBomb.target) && getTrapBlock(BedBomb.target, 4.5) != null;
    }

    public static boolean shouldStringBreak() {
        List<BlockPos> strings = new ArrayList<>();
        for (CardinalDirection d : CardinalDirection.values()) {
            BlockPos cPos = getBlockPos(BedBomb.target).up();

            if (getBlock(cPos).asItem().equals(Items.STRING) && mc.player.getPos().distanceTo(getCenterVec3d(cPos)) < 4.5)
                strings.add(cPos);

            if (getBlock(cPos.offset(d.toDirection())).asItem().equals(Items.STRING) && mc.player.getPos().distanceTo(getCenterVec3d(cPos.offset(d.toDirection()))) < 4.5)
                strings.add(cPos.offset(d.toDirection()));
        }
        return !strings.isEmpty() && !shouldTrapBreak();
    }


    //some damage calc
    public static double getDamage(PlayerEntity player, Vec3d cVec, boolean predictMovement, boolean collision, int i, boolean ignoreTerrain) {
        if (player != null && isCreative(player)) return 0;

        Vec3d pVec = returnPredictVec(player, collision, i);
        ((IVec3d) vec3d).set(player.getPos().x, player.getPos().y, player.getPos().z);
        if (predictMovement) ((IVec3d) vec3d).set(pVec.getX(), pVec.getY(), pVec.getZ());

        double modDistance = Math.sqrt(vec3d.squaredDistanceTo(cVec));
        if (modDistance > 10) return 0;

        double exposure = getExposure(cVec, player, predictMovement, collision, i, raycastContext, ignoreTerrain);
        double impact = (1.0 - (modDistance / 10.0)) * exposure;
        double damage = (impact * impact + impact) / 2 * 7 * (5 * 2) + 1;

        damage = getDamageForDifficulty(damage);
        damage = resistanceReduction(player, damage);
        damage = DamageUtil.getDamageLeft(player, (float) damage, mc.world.getDamageSources().explosion(explosion), (float) player.getArmor(), (float) player.getAttributeInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS).getValue());
        ((IExplosion) explosion).set(cVec, 5, true);
        damage = blastProtReduction(player, (float) damage, explosion);

        if (damage < 0) damage = 0;
        return damage;
    }

    private static double getDamageForDifficulty(double damage) {
        return switch (mc.world.getDifficulty()) {
            case PEACEFUL -> 0;
            case EASY -> Math.min(damage / 2 + 1, damage);
            case HARD -> damage * 3 / 2;
            default -> damage;
        };
    }

    private static double resistanceReduction(LivingEntity player, double damage) {
        if (player.hasStatusEffect(StatusEffects.RESISTANCE)) {
            int lvl = (player.getStatusEffect(StatusEffects.RESISTANCE).getAmplifier() + 1);
            damage *= (1 - (lvl * 0.2));
        }

        return damage < 0 ? 0 : damage;
    }

    private static float blastProtReduction(Entity player, float damage, Explosion explosion) {
        DamageSource source = mc.world.getDamageSources().explosion(explosion);
        if (source.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY)) return damage;

        int damageProtection = 0;

        for (ItemStack stack : ((LivingEntity) player).getAllArmorItems()) {
            Object2IntMap<RegistryEntry<Enchantment>> enchantments = new Object2IntOpenHashMap<>();
            Utils.getEnchantments(stack, enchantments);

            int protection = Utils.getEnchantmentLevel(enchantments, Enchantments.PROTECTION);

            if (protection > 0) {
                damageProtection += protection;
            }

            int blastProtection = Utils.getEnchantmentLevel(enchantments, Enchantments.BLAST_PROTECTION);

            if (blastProtection > 0 && source.isIn(DamageTypeTags.IS_EXPLOSION)) {
                damageProtection += 2 * blastProtection;
            }
        }
        return DamageUtil.getInflictedDamage(damage, damageProtection);
    }

    private static double getExposure(Vec3d source, Entity entity, boolean predictMovement, boolean collision, int ii, RaycastContext raycastContext, boolean ignoreTerrain) {
        Box box = getBoundingBox((PlayerEntity) entity);
        if (predictMovement) {
            box = returnPredictBox((PlayerEntity) entity, collision, ii);
        }

        double d = 1 / ((box.maxX - box.minX) * 2 + 1);
        double e = 1 / ((box.maxY - box.minY) * 2 + 1);
        double f = 1 / ((box.maxZ - box.minZ) * 2 + 1);
        double g = (1 - Math.floor(1 / d) * d) / 2;
        double h = (1 - Math.floor(1 / f) * f) / 2;

        if (!(d < 0) && !(e < 0) && !(f < 0)) {
            int i = 0;
            int j = 0;

            for (double k = 0; k <= 1; k += d) {
                for (double l = 0; l <= 1; l += e) {
                    for (double m = 0; m <= 1; m += f) {
                        double n = MathHelper.lerp(k, box.minX, box.maxX);
                        double o = MathHelper.lerp(l, box.minY, box.maxY);
                        double p = MathHelper.lerp(m, box.minZ, box.maxZ);

                        ((IVec3d) vec3d).set(n + g, o, p + h);
                        ((IRaycastContext) raycastContext).set(vec3d, source, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, entity);

                        if (raycast(raycastContext, ignoreTerrain).getType() == HitResult.Type.MISS) i++;

                        j++;
                    }
                }
            }

            return (double) i / j;
        }

        return 0;
    }

    private static BlockHitResult raycast(RaycastContext context, boolean ignoreTerrain) {
        return BlockView.raycast(context.getStart(), context.getEnd(), context, (raycastContext, blockpos) -> {
            BlockState blockState = getState(blockpos);
            if (!isBlastResist(blockpos) && ignoreTerrain)
                blockState = Blocks.AIR.getDefaultState();


            Vec3d vec3d = raycastContext.getStart();
            Vec3d vec3d2 = raycastContext.getEnd();

            VoxelShape voxelShape = raycastContext.getBlockShape(blockState, mc.world, blockpos);
            BlockHitResult blockHitResult = mc.world.raycastBlock(vec3d, vec3d2, blockpos, voxelShape, blockState);
            VoxelShape voxelShape2 = VoxelShapes.empty();
            BlockHitResult blockHitResult2 = voxelShape2.raycast(vec3d, vec3d2, blockpos);

            double d = blockHitResult == null ? Double.MAX_VALUE : raycastContext.getStart().squaredDistanceTo(blockHitResult.getPos());
            double e = blockHitResult2 == null ? Double.MAX_VALUE : raycastContext.getStart().squaredDistanceTo(blockHitResult2.getPos());

            return d <= e ? blockHitResult : blockHitResult2;
        }, (raycastContext) -> {
            Vec3d vec3d = raycastContext.getStart().subtract(raycastContext.getEnd());
            Vec3i vec3i = new Vec3i((int) raycastContext.getEnd().x, (int) raycastContext.getEnd().y, (int) raycastContext.getEnd().z);
            return BlockHitResult.createMissed(raycastContext.getEnd(), Direction.getFacing(vec3d.x, vec3d.y, vec3d.z), new BlockPos(vec3i));
        });
    }

    // damage calc stuff
    public static <T> List<List<T>> split(List<T> list, int count) {
        List<List<T>> part = new ArrayList<>();

        for (int i = 0; i < count; i++) part.add(new ArrayList<>());
        for (int i = 0; i < list.size(); i++) {
            part.get(i % count).add(list.get(i));
        }

        return part;
    }

    // render shit
    public static class RenderBlock {
        public BlockPos.Mutable pos = new BlockPos.Mutable();
        public CardinalDirection renderDir;
        public int ticks;

        public RenderBlock set(BlockPos blockPos, CardinalDirection dir) {
            renderDir = dir;
            pos.set(blockPos);
            ticks = 10;
            return this;
        }

        public void tick() {
            ticks--;
        }

        public void render(Render3DEvent event, Color sides, Color lines, ShapeMode shapeMode) {
            if (renderDir != null) {
                int x = pos.getX();
                int y = pos.getY();
                int z = pos.getZ();
                int preSideA = sides.a;
                int preLineA = lines.a;
                sides.a *= (int) ((double) ticks / 5);
                lines.a *= (int) ((double) ticks / 5);
                switch (renderDir) {
                    case South -> event.renderer.box(x, y, z, x + 1, y + 0.56, z + 2, sides, lines, shapeMode, 0);
                    case North -> event.renderer.box(x, y, z - 1, x + 1, y + 0.56, z + 1, sides, lines, shapeMode, 0);
                    case West -> event.renderer.box(x - 1, y, z, x + 1, y + 0.56, z + 1, sides, lines, shapeMode, 0);
                    case East -> event.renderer.box(x, y, z, x + 2, y + 0.56, z + 1, sides, lines, shapeMode, 0);
                }
                sides.a = preSideA;
                lines.a = preLineA;
            }
        }
    }

    public static class RenderBreak {
        public BlockPos.Mutable pos = new BlockPos.Mutable();
        public int ticks;

        public RenderBreak set(BlockPos blockPos) {
            pos.set(blockPos);
            ticks = 50;
            return this;
        }

        public void tick() {
            ticks--;
        }

        public void render(Render3DEvent event, Color sides, Color lines, ShapeMode shapeMode) {
            if (pos != null) {
                int preSideA = sides.a;
                int preLineA = lines.a;
                sides.a += ticks - 1;
                lines.a += ticks - 1;
                event.renderer.box(pos, sides, lines, shapeMode, 0);
                sides.a = preSideA;
                lines.a = preLineA;
            }
        }
    }

    public static class RenderText {
        private final Vector3d vec3 = new Vector3d();
        public BlockPos.Mutable pos = new BlockPos.Mutable();
        public String text;
        public int ticks;

        public RenderText set(BlockPos pos, String text) {
            this.text = text;
            this.pos.set(pos);
            ticks = 30;
            return this;
        }

        public void tick() {
            ticks--;
        }

        public void render(Render2DEvent event, Color textColor) {
            if (text != null) {
                int preTextA = textColor.a;
                textColor.a *= (int) ((double) ticks / 5);
                vec3.set(pos.getX() + 0.5, pos.getY() + 0.2, pos.getZ() + 0.5);

                if (NametagUtils.to2D(vec3, 1.5)) {
                    NametagUtils.begin(vec3);
                    TextRenderer.get().begin(1, false, true);

                    double w = TextRenderer.get().getWidth(text) / 2;
                    TextRenderer.get().render(text, -w, 0, textColor, true);

                    TextRenderer.get().end();
                    NametagUtils.end();
                }

                textColor.a = preTextA;
            }
        }
    }
}

