package me.bedtrap.addon.modules.misc;

import me.bedtrap.addon.BedTrap;
import me.bedtrap.addon.util.advanced.QuaternionUtils;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.util.Hand;

public class HandTweaks extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgMainhand = settings.createGroup("Main Hand");
    private final SettingGroup sgOffhand = settings.createGroup("Off Hand");

    private final Setting<Boolean> noSwing = sgGeneral.add(new BoolSetting.Builder().name("no-swing").description("Preventing client-side swing animation.").defaultValue(false).build());
    private final Setting<Double> scale = sgGeneral.add(new DoubleSetting.Builder().name("scale").description("The scale of your hands.").defaultValue(1).sliderMax(5).build());

    private final Setting<Integer> speedX = sgMainhand.add(new IntSetting.Builder().name("x-animation").description("The speed of X orientation of your main hand.").defaultValue(0).sliderMin(-100).sliderMax(100).build());
    private final Setting<Integer> speedY = sgMainhand.add(new IntSetting.Builder().name("y-animation").description("The speed of Y orientation of your main hand.").defaultValue(0).sliderMin(-100).sliderMax(100).build());
    private final Setting<Integer> speedZ = sgMainhand.add(new IntSetting.Builder().name("z-animation").description("The speed of Z orientation of your main hand.").defaultValue(0).sliderMin(-100).sliderMax(100).build());

    private final Setting<Integer> offspeedX = sgOffhand.add(new IntSetting.Builder().name("x-animation").description("The speed of X orientation of your off hand.").defaultValue(0).sliderMin(-100).sliderMax(100).build());
    private final Setting<Integer> offspeedY = sgOffhand.add(new IntSetting.Builder().name("y-animation").description("The speed of Y orientation of your off hand.").defaultValue(0).sliderMin(-100).sliderMax(100).build());
    private final Setting<Integer> offspeedZ = sgOffhand.add(new IntSetting.Builder().name("z-animation").description("The speed of Z orientation of your off hand.").defaultValue(0).sliderMin(-100).sliderMax(100).build());

    private float nextRotationX = 0, nextRotationY = 0, nextRotationZ = 0;

    public HandTweaks() {
        super(BedTrap.Misc, "hand-tweaks", "Tweaks for main and off hands.");
    }

    public void transform(MatrixStack matrices, Hand hand) {
        if (!isActive()) return;
        float defRotation = 0;

        matrices.scale(scale.get().floatValue(), scale.get().floatValue(), scale.get().floatValue());

        if (hand == Hand.MAIN_HAND) {
            if (!speedX.get().equals(0)) {
                float finalRotationX = (nextRotationX++ / speedX.get());
                matrices.multiply(QuaternionUtils.fromEulerXyz(finalRotationX, defRotation, defRotation));
            }
            if (!speedY.get().equals(0)) {
                float finalRotationY = (nextRotationY++ / speedY.get());
                matrices.multiply(QuaternionUtils.fromEulerXyz(defRotation, finalRotationY, defRotation));
            }
            if (!speedZ.get().equals(0)) {
                float finalRotationZ = (nextRotationZ++ / speedZ.get());
                matrices.multiply(QuaternionUtils.fromEulerXyz(defRotation, defRotation, finalRotationZ));
            }
        } else {
            if (!offspeedX.get().equals(0)) {
                float finalRotationX = (nextRotationX++ / offspeedX.get());
                matrices.multiply(QuaternionUtils.fromEulerXyz(finalRotationX, defRotation, defRotation));
            }
            if (!offspeedY.get().equals(0)) {
                float finalRotationY = (nextRotationY++ / offspeedY.get());
                matrices.multiply(QuaternionUtils.fromEulerXyz(defRotation, finalRotationY, defRotation));
            }
            if (!offspeedZ.get().equals(0)) {
                float finalRotationZ = (nextRotationZ++ / offspeedZ.get());
                matrices.multiply(QuaternionUtils.fromEulerXyz(defRotation, defRotation, finalRotationZ));
            }
        }
    }

    @EventHandler
    private void onPacketSend(PacketEvent.Send event) {
        if (noSwing.get() && event.packet instanceof HandSwingC2SPacket) mc.player.handSwinging = false;
    }

    @EventHandler
    private void onPacketRecieve(PacketEvent.Receive event) {
        if (noSwing.get() && event.packet instanceof HandSwingC2SPacket) mc.player.handSwinging = false;
    }
}
