package me.bedtrap.addon.modules.misc;

import me.bedtrap.addon.BedTrap;
import me.bedtrap.addon.util.other.Task;
import me.bedtrap.addon.util.other.TextUtils;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Comparator;

import static me.bedtrap.addon.util.basic.BlockInfo.*;
import static me.bedtrap.addon.util.basic.EntityInfo.getBlockPos;

public class ChestExplorer extends Module {
    //private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Task chatTask = new Task();
    private FindItemResult axe;
    private BlockPos pos;
    private int x, y, z;
    private Stage stage;

    public ChestExplorer() {
        super(BedTrap.Misc, "chest-explorer", "Automatically goes to closest chest, looting it and breaking.");
    }

    @Override
    public void onActivate() {
        stage = Stage.Find;

        chatTask.reset();
        pos = null;
    }

    @Override
    public void onDeactivate() {
        TextUtils.sendPlayerMsg("#stop");
    }

    @EventHandler
    public void onTick(TickEvent.Pre event) {
        axe = InvUtils.find(itemStack -> itemStack.getItem() == Items.NETHERITE_AXE || itemStack.getItem() == Items.DIAMOND_AXE);

        switch (stage) {
            case Find -> {
                pos = findChests();

                if (pos == null) {
                    chatTask.reset();
                    stage = Stage.Stuck;
                    //info("There's no chests in your view distance.");
                    //toggle();
                    return;
                }

                x = pos.getX();
                y = pos.getY();
                z = pos.getZ();

                chatTask.reset();
                stage = Stage.Move;
            }
            case Move -> {
                chatTask.run(() -> TextUtils.sendPlayerMsg("#goto " + x + " " + y + " " + z));

                if (getBlockPos(mc.player).equals(pos)) stage = Stage.Reset;
            }
            case Reset -> {
                chatTask.reset();

                stage = Stage.Find;
            }
            case Stuck -> {
                if (findChests() != null) stage = Stage.Find;
                else
                    chatTask.run(() -> TextUtils.sendPlayerMsg("#goto " + getDirection().getX() + " " + y + " " + getDirection().getZ()));
            }
        }
    }

    private BlockPos getDirection() {
        int x = X(mc.player.getBlockPos());
        int y = Y(mc.player.getBlockPos());
        int z = Z(mc.player.getBlockPos());

        if (x > 0) x = 30000000;
        else x = -30000000;

        if (y > 0) y = 30000000;
        else y = -30000000;

        return new BlockPos(x, y, z);
    }

    private BlockPos findChests() {
        ArrayList<BlockPos> pos = new ArrayList<>();

        for (BlockEntity entity : Utils.blockEntities()) {
            if (entity instanceof ChestBlockEntity chestBlock) {
                if (!pos.contains(chestBlock.getPos())) pos.add(chestBlock.getPos());
            }
        }

        if (pos.isEmpty()) return null;

        pos.sort(Comparator.comparingDouble(PlayerUtils::distanceTo));
        return pos.getFirst();
    }

    public enum Stage {
        Find,
        Move,
        Reset,
        Stuck
    }
}
