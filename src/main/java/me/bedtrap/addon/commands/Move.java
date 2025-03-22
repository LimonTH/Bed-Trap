package me.bedtrap.addon.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.slot.SlotActionType;

public class Move extends Command {
    public Move() {
        super("move", "Moves the item in hand to the specified slot.");
    }

    public static void move(int slot) {
        assert mc.interactionManager != null;
        mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, PlayerInventory.MAIN_SIZE + mc.player.getInventory().selectedSlot, slot, SlotActionType.SWAP, mc.player);
    }

    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        String[] slots = {"boots", "leggings", "chestplate", "helmet"};
        int[] armorSlots = {36, 37, 38, 39};

        for (int i = 0; i < slots.length; i++) {
            int j = i;
            builder.then(literal(slots[j]).executes((move) -> {
                move(armorSlots[j]);
                return SINGLE_SUCCESS;
            }));
        }
        builder.then(literal("custom").then(argument("slot", IntegerArgumentType.integer()).executes((move) -> {
            int slot = IntegerArgumentType.getInteger(move, "slot");
            move(slot);
            return SINGLE_SUCCESS;
        })));
    }
}
