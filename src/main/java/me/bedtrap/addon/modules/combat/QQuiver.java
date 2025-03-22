package me.bedtrap.addon.modules.combat;

import com.google.common.collect.Lists;
import me.bedtrap.addon.BedTrap;
import me.bedtrap.addon.util.other.TimerUtils;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class QQuiver extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<List<StatusEffect>> effects = sgGeneral.add(new StatusEffectListSetting.Builder().name("effects").description("Which effects to shoot you with.").defaultValue(StatusEffects.STRENGTH.value()).build());
    private final Setting<Boolean> lowMode = sgGeneral.add(new BoolSetting.Builder().name("low-mode").description("Works only w/ 1-2 types of arrows.").defaultValue(true).build());
    private final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder().name("release-interval").defaultValue(4).min(0).max(7).sliderRange(0, 7).build());
    private final Setting<Boolean> onlyOnGround = sgGeneral.add(new BoolSetting.Builder().name("only-on-ground").defaultValue(true).build());
    private final Setting<Boolean> checkEffects = sgGeneral.add(new BoolSetting.Builder().name("check-existing-effects").description("Won't shoot you with effects you already have.").defaultValue(true).build());
    private final Setting<Boolean> silentBow = sgGeneral.add(new BoolSetting.Builder().name("silent-bow").defaultValue(true).build());

    private final List<Integer> arrowSlots = new ArrayList<>();
    TimerUtils afterTimer = new TimerUtils();
    int interval;
    int prevBowSlot;

    public QQuiver() {
        super(BedTrap.Combat, "QQuiver", "Offhand, but smarter.");
    }

    @Override
    public void onActivate() {
        afterTimer.reset();
        interval = 0;
        prevBowSlot = -1;

        FindItemResult bow = InvUtils.find(Items.BOW);

        if (!bow.found()) {
            toggle();
            return;
        }

        if (silentBow.get() && !bow.isHotbar()) {
            prevBowSlot = bow.slot();
            InvUtils.move().from(bow.slot()).to(mc.player.getInventory().selectedSlot);
        } else if (!bow.isHotbar()) {
            ChatUtils.error("No bow in inventory found.");
            toggle();
            return;
        }

        mc.options.useKey.setPressed(false);
        mc.interactionManager.stopUsingItem(mc.player);

        if (!silentBow.get()) InvUtils.swap(bow.slot(), true);

        arrowSlots.clear();

        List<StatusEffect> usedEffects = new ArrayList<>();

        for (int i = mc.player.getInventory().size(); i > 0; i--) {
            if (i == mc.player.getInventory().selectedSlot) continue;

            ItemStack item = mc.player.getInventory().getStack(i);

            if (item.getItem() != Items.TIPPED_ARROW) continue;

            List<StatusEffectInstance> effects = Lists.newArrayList();
            if (!item.isEmpty()) {
                // Получаем итератор эффектов

                // Проходим по всем элементам итератора и добавляем их в список
                for (StatusEffectInstance effect : item.getComponents().get(DataComponentTypes.POTION_CONTENTS).getEffects()) {
                    effects.add(effect);
                }
            }
            if (effects.isEmpty()) continue;

            StatusEffect effect = effects.getFirst().getEffectType().value();

            if (this.effects.get().contains(effect)
                    && !usedEffects.contains(effect)
                    && (!hasEffect(effect) || !checkEffects.get())) {
                usedEffects.add(effect);
                arrowSlots.add(i);
            }
        }
    }

    private boolean hasEffect(StatusEffect effect) {
        for (StatusEffectInstance statusEffect : mc.player.getStatusEffects()) {
            if (Objects.equals(statusEffect.getEffectType().value(), effect)) return true;
        }

        return false;
    }

    @Override
    public void onDeactivate() {
        if (lowMode.get()) mc.options.sneakKey.setPressed(false);
        if (silentBow.get() && prevBowSlot != -1)
            InvUtils.move().from(mc.player.getInventory().selectedSlot).to(prevBowSlot);
        else
            InvUtils.swapBack();
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (onlyOnGround.get() && !mc.player.isOnGround()) return;
        if (arrowSlots.isEmpty() || !InvUtils.findInHotbar(Items.BOW).isMainHand()) {
            if (afterTimer.passedS(1)) toggle();
            return;
        }

        interval--;
        if (interval > 0) return;

        boolean charging = mc.options.useKey.isPressed();
        double charge = lowMode.get() ? 0.1 : 0.12;
        if (charging) {
            if (BowItem.getPullProgress(mc.player.getItemUseTime()) >= charge) {
                int targetSlot = arrowSlots.getFirst();
                arrowSlots.removeFirst();

                mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(mc.player.getYaw(), -90, mc.player.isOnGround()));
                mc.options.useKey.setPressed(false);
                mc.interactionManager.stopUsingItem(mc.player);
                if (targetSlot != 9) InvUtils.move().from(9).to(targetSlot);
                if (arrowSlots.isEmpty() && lowMode.get()) mc.options.sneakKey.setPressed(true);
                interval = delay.get();
                afterTimer.reset();
            }
        } else {
            InvUtils.move().from(arrowSlots.getFirst()).to(9);
            mc.options.useKey.setPressed(true);
        }
    }
}
