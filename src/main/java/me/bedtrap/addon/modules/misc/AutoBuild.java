/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client/).
 * Copyright (c) 2020 Meteor Development.
 */

package me.bedtrap.addon.modules.misc;

import me.bedtrap.addon.BedTrap;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import static me.bedtrap.addon.util.basic.BlockInfo.isReplaceable;
import static me.bedtrap.addon.util.basic.EntityInfo.getStack;

public class AutoBuild extends Module {
    private static final String[] BUILD = new String[4];

    static {
        BUILD[0] = "[NomadHut]";
        BUILD[1] = "[Penis]";
        BUILD[2] = "[Swastika]";
        BUILD[3] = "[Highway]";
    }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgHighway = settings.createGroup("Highway");
    private final Setting<Build> buildPlacement = sgGeneral.add(new EnumSetting.Builder<Build>().name("type").description("Which type gonna build.").defaultValue(Build.Swastika).build());
    private final Setting<Integer> cooldown = sgGeneral.add(new IntSetting.Builder().name("place-delay").description("Block per tick.").defaultValue(0).min(0).sliderMax(20).build());
    int tickskip = cooldown.get();
    private final Setting<Boolean> center = sgGeneral.add(new BoolSetting.Builder().name("center").description("Moves you to the center of the block.").defaultValue(true).build());
    private final Setting<Boolean> selfToggle = sgGeneral.add(new BoolSetting.Builder().name("self-toggle").description("Toggles when you run out of obsidian.").defaultValue(false).build());
    private final Setting<Integer> size = sgHighway.add(new IntSetting.Builder().name("highway-size").description("The size of highway.").defaultValue(3).min(1).sliderMin(1).max(5).sliderMax(5).build());
    private final Setting<Boolean> sideBlocks = sgHighway.add(new BoolSetting.Builder().name("side-blocks").description("Placing side blocks.").defaultValue(true).build());
    private final BlockPos.Mutable blockPos = new BlockPos.Mutable();
    private boolean sentMessage = false;
    private boolean return_;
    private Direction direction;
    private DirectionLite directionLite;

    public AutoBuild() {
        super(BedTrap.Misc, "auto-build", "Places build that you choose.");
    }

    @Override
    public void onActivate() {
        if (center.get()) PlayerUtils.centerPlayer();


        blockPos.set(mc.player.getBlockPos());
        direction = getDirection(mc.player);
        directionLite = getDirectionLite(mc.player);
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        int obsidianSlot = -1;
        for (int i = 0; i < 9; i++) {
            assert mc.player != null;
            if (getStack(mc.player, i).getItem() == Blocks.OBSIDIAN.asItem()) {
                obsidianSlot = i;
                break;
            }
        }

        if (obsidianSlot == -1 && selfToggle.get()) {
            if (!sentMessage) {
                ChatUtils.warning("No obsidian found… disabling.");
                sentMessage = true;
            }

            this.toggle();
            return;
        } else if (obsidianSlot == -1) return;

        if (buildPlacement.get() == Build.NomadHut) {
            mc.player.getInventory().selectedSlot = obsidianSlot;
            if (tickskip == 0) {
                tickskip = cooldown.get();
            } else {
                tickskip--;
                return;
            }
            return_ = false;
            switch (directionLite) {
                case EAST:
                    //Код строительства в направлении East
                    //layer 1
                    boolean pq1 = place(2, 0, -1);
                    if (return_) return;
                    boolean pq3 = place(2, 0, 1);
                    if (return_) return;
                    //
                    boolean pq4 = place(1, 0, 2);
                    if (return_) return;
                    boolean pq5 = place(0, 0, 2);
                    if (return_) return;
                    boolean pq6 = place(-1, 0, 2);
                    if (return_) return;
                    //
                    boolean pq7 = place(-2, 0, 1);
                    if (return_) return;
                    boolean pq8 = place(-2, 0, 0);
                    if (return_) return;
                    boolean pq9 = place(-2, 0, -1);
                    if (return_) return;
                    //
                    boolean pq10 = place(-1, 0, -2);
                    if (return_) return;
                    boolean pq11 = place(0, 0, -2);
                    if (return_) return;
                    boolean pq12 = place(1, 0, -2);
                    if (return_) return;
                    //layer 2
                    boolean pq13 = place(2, 1, -1);
                    if (return_) return;
                    boolean pq14 = place(2, 1, 1);
                    if (return_) return;
                    //
                    boolean pq15 = place(1, 1, 2);
                    if (return_) return;
                    boolean pq16 = place(-1, 1, 2);
                    if (return_) return;
                    //
                    boolean pq17 = place(-2, 1, 1);
                    if (return_) return;
                    boolean pq18 = place(-2, 1, -1);
                    if (return_) return;
                    //
                    boolean pq19 = place(-1, 1, -2);
                    if (return_) return;
                    boolean pq20 = place(1, 1, -2);
                    if (return_) return;
                    //layer 3
                    boolean pq21 = place(2, 2, -1);
                    if (return_) return;
                    boolean pq22 = place(2, 2, 0);
                    if (return_) return;
                    boolean pq23 = place(2, 2, 1);
                    if (return_) return;
                    //
                    boolean pq24 = place(1, 2, 2);
                    if (return_) return;
                    boolean pq25 = place(0, 2, 2);
                    if (return_) return;
                    boolean pq26 = place(-1, 2, 2);
                    if (return_) return;
                    //
                    boolean pq27 = place(-2, 2, 1);
                    if (return_) return;
                    boolean pq28 = place(-2, 2, 0);
                    if (return_) return;
                    boolean pq29 = place(-2, 2, -1);
                    if (return_) return;
                    //
                    boolean pq30 = place(-1, 2, -2);
                    if (return_) return;
                    boolean pq31 = place(0, 2, -2);
                    if (return_) return;
                    boolean pq32 = place(1, 2, -2);
                    if (return_) return;
                    //layer 4
                    boolean pq33 = place(1, 3, -1);
                    if (return_) return;
                    boolean pq34 = place(1, 3, 0);
                    if (return_) return;
                    boolean pq35 = place(1, 3, 1);
                    if (return_) return;
                    //
                    boolean pq36 = place(0, 3, -1);
                    if (return_) return;
                    boolean pq37 = place(0, 3, 0);
                    if (return_) return;
                    boolean pq38 = place(0, 3, 1);
                    if (return_) return;
                    //
                    boolean pq39 = place(-1, 3, -1);
                    if (return_) return;
                    boolean pq40 = place(-1, 3, 0);
                    if (return_) return;
                    boolean pq41 = place(-1, 3, 1);
                    if (return_) return;
                    if (pq1 && pq3 && pq4 && pq5 && pq6 && pq7 && pq8 && pq9 && pq10 && pq11 && pq12 && pq13 && pq14 && pq15 && pq16 && pq17 && pq18 && pq19 && pq20 && pq21 && pq22 && pq23 && pq24 && pq25 && pq26 && pq27 && pq28 && pq29 && pq30 && pq31 && pq32 && pq33 && pq34 && pq35 && pq36 && pq37 && pq38 && pq39 && pq40 && pq41) {
                        this.toggle();
                    } else {
                        return;
                    }
                    break;
                case NORTH:
                    //Код строительства в направлении North
                    boolean pw1 = place(2, 0, -1);
                    if (return_) return;
                    boolean pw2 = place(2, 0, 0);
                    if (return_) return;
                    boolean pw3 = place(2, 0, 1);
                    if (return_) return;
                    //
                    boolean pw4 = place(1, 0, 2);
                    if (return_) return;
                    boolean pw5 = place(0, 0, 2);
                    if (return_) return;
                    boolean pw6 = place(-1, 0, 2);
                    if (return_) return;
                    //
                    boolean pw7 = place(-2, 0, 1);
                    if (return_) return;
                    boolean pw8 = place(-2, 0, 0);
                    if (return_) return;
                    boolean pw9 = place(-2, 0, -1);
                    if (return_) return;
                    //
                    boolean pw10 = place(-1, 0, -2);
                    if (return_) return;
                    boolean pw12 = place(1, 0, -2);
                    if (return_) return;
                    //layer 2
                    boolean pw13 = place(2, 1, -1);
                    if (return_) return;
                    boolean pw14 = place(2, 1, 1);
                    if (return_) return;
                    //
                    boolean pw15 = place(1, 1, 2);
                    if (return_) return;
                    boolean pw16 = place(-1, 1, 2);
                    if (return_) return;
                    //
                    boolean pw17 = place(-2, 1, 1);
                    if (return_) return;
                    boolean pw18 = place(-2, 1, -1);
                    if (return_) return;
                    //
                    boolean pw19 = place(-1, 1, -2);
                    if (return_) return;
                    boolean pw20 = place(1, 1, -2);
                    if (return_) return;
                    //layer 3
                    boolean pw21 = place(2, 2, -1);
                    if (return_) return;
                    boolean pw22 = place(2, 2, 0);
                    if (return_) return;
                    boolean pw23 = place(2, 2, 1);
                    if (return_) return;
                    //
                    boolean pw24 = place(1, 2, 2);
                    if (return_) return;
                    boolean pw25 = place(0, 2, 2);
                    if (return_) return;
                    boolean pw26 = place(-1, 2, 2);
                    if (return_) return;
                    //
                    boolean pw27 = place(-2, 2, 1);
                    if (return_) return;
                    boolean pw28 = place(-2, 2, 0);
                    if (return_) return;
                    boolean pw29 = place(-2, 2, -1);
                    if (return_) return;
                    //
                    boolean pw30 = place(-1, 2, -2);
                    if (return_) return;
                    boolean pw31 = place(0, 2, -2);
                    if (return_) return;
                    boolean pw32 = place(1, 2, -2);
                    if (return_) return;
                    //layer 4
                    boolean pw33 = place(1, 3, -1);
                    if (return_) return;
                    boolean pw34 = place(1, 3, 0);
                    if (return_) return;
                    boolean pw35 = place(1, 3, 1);
                    if (return_) return;
                    //
                    boolean pw36 = place(0, 3, -1);
                    if (return_) return;
                    boolean pw37 = place(0, 3, 0);
                    if (return_) return;
                    boolean pw38 = place(0, 3, 1);
                    if (return_) return;
                    //
                    boolean pw39 = place(-1, 3, -1);
                    if (return_) return;
                    boolean pw40 = place(-1, 3, 0);
                    if (return_) return;
                    boolean pw41 = place(-1, 3, 1);
                    if (return_) return;
                    if (pw1 && pw2 && pw3 && pw4 && pw5 && pw6 && pw7 && pw8 && pw9 && pw10 && pw12 && pw13 && pw14 && pw15 && pw16 && pw17 && pw18 && pw19 && pw20 && pw21 && pw22 && pw23 && pw24 && pw25 && pw26 && pw27 && pw28 && pw29 && pw30 && pw31 && pw32 && pw33 && pw34 && pw35 && pw36 && pw37 && pw38 && pw39 && pw40 && pw41) {
                        this.toggle();
                    } else {
                        return;
                    }
                    break;
                case WEST:
                    //Код строительства в направлении West
                    boolean pe1 = place(2, 0, -1);
                    if (return_) return;
                    boolean pe2 = place(2, 0, 0);
                    if (return_) return;
                    boolean pe3 = place(2, 0, 1);
                    if (return_) return;
                    //
                    boolean pe4 = place(1, 0, 2);
                    if (return_) return;
                    boolean pe5 = place(0, 0, 2);
                    if (return_) return;
                    boolean pe6 = place(-1, 0, 2);
                    if (return_) return;
                    //
                    boolean pe7 = place(-2, 0, 1);
                    if (return_) return;
                    boolean pe9 = place(-2, 0, -1);
                    if (return_) return;
                    //
                    boolean pe10 = place(-1, 0, -2);
                    if (return_) return;
                    boolean pe11 = place(0, 0, -2);
                    if (return_) return;
                    boolean pe12 = place(1, 0, -2);
                    if (return_) return;
                    //layer 2
                    boolean pe13 = place(2, 1, -1);
                    if (return_) return;
                    boolean pe14 = place(2, 1, 1);
                    if (return_) return;
                    //
                    boolean pe15 = place(1, 1, 2);
                    if (return_) return;
                    boolean pe16 = place(-1, 1, 2);
                    if (return_) return;
                    //
                    boolean pe17 = place(-2, 1, 1);
                    if (return_) return;
                    boolean pe18 = place(-2, 1, -1);
                    if (return_) return;
                    //
                    boolean pe19 = place(-1, 1, -2);
                    if (return_) return;
                    boolean pe20 = place(1, 1, -2);
                    if (return_) return;
                    //layer 3
                    boolean pe21 = place(2, 2, -1);
                    if (return_) return;
                    boolean pe22 = place(2, 2, 0);
                    if (return_) return;
                    boolean pe23 = place(2, 2, 1);
                    if (return_) return;
                    //
                    boolean pe24 = place(1, 2, 2);
                    if (return_) return;
                    boolean pe25 = place(0, 2, 2);
                    if (return_) return;
                    boolean pe26 = place(-1, 2, 2);
                    if (return_) return;
                    //
                    boolean pe27 = place(-2, 2, 1);
                    if (return_) return;
                    boolean pe28 = place(-2, 2, 0);
                    if (return_) return;
                    boolean pe29 = place(-2, 2, -1);
                    if (return_) return;
                    //
                    boolean pe30 = place(-1, 2, -2);
                    if (return_) return;
                    boolean pe31 = place(0, 2, -2);
                    if (return_) return;
                    boolean pe32 = place(1, 2, -2);
                    if (return_) return;
                    //layer 4
                    boolean pe33 = place(1, 3, -1);
                    if (return_) return;
                    boolean pe34 = place(1, 3, 0);
                    if (return_) return;
                    boolean pe35 = place(1, 3, 1);
                    if (return_) return;
                    //
                    boolean pe36 = place(0, 3, -1);
                    if (return_) return;
                    boolean pe37 = place(0, 3, 0);
                    if (return_) return;
                    boolean pe38 = place(0, 3, 1);
                    if (return_) return;
                    //
                    boolean pe39 = place(-1, 3, -1);
                    if (return_) return;
                    boolean pe40 = place(-1, 3, 0);
                    if (return_) return;
                    boolean pe41 = place(-1, 3, 1);
                    if (return_) return;
                    if (pe1 && pe2 && pe3 && pe4 && pe5 && pe6 && pe7 && pe9 && pe10 && pe11 && pe12 && pe13 && pe14 && pe15 && pe16 && pe17 && pe18 && pe19 && pe20 && pe21 && pe22 && pe23 && pe24 && pe25 && pe26 && pe27 && pe28 && pe29 && pe30 && pe31 && pe32 && pe33 && pe34 && pe35 && pe36 && pe37 && pe38 && pe39 && pe40 && pe41) {
                        this.toggle();
                    } else {
                        return;
                    }
                    break;
                case SOUTH:
                    //Код строительства в направлении South
                    boolean pr1 = place(2, 0, -1);
                    if (return_) return;
                    boolean pr2 = place(2, 0, 0);
                    if (return_) return;
                    boolean pr3 = place(2, 0, 1);
                    if (return_) return;
                    //
                    boolean pr4 = place(1, 0, 2);
                    if (return_) return;
                    boolean pr6 = place(-1, 0, 2);
                    if (return_) return;
                    //
                    boolean pr7 = place(-2, 0, 1);
                    if (return_) return;
                    boolean pr8 = place(-2, 0, 0);
                    if (return_) return;
                    boolean pr9 = place(-2, 0, -1);
                    if (return_) return;
                    //
                    boolean pr10 = place(-1, 0, -2);
                    if (return_) return;
                    boolean pr11 = place(0, 0, -2);
                    if (return_) return;
                    boolean pr12 = place(1, 0, -2);
                    if (return_) return;
                    //layer 2
                    boolean pr13 = place(2, 1, -1);
                    if (return_) return;
                    boolean pr14 = place(2, 1, 1);
                    if (return_) return;
                    //
                    boolean pr15 = place(1, 1, 2);
                    if (return_) return;
                    boolean pr16 = place(-1, 1, 2);
                    if (return_) return;
                    //
                    boolean pr17 = place(-2, 1, 1);
                    if (return_) return;
                    boolean pr18 = place(-2, 1, -1);
                    if (return_) return;
                    //
                    boolean pr19 = place(-1, 1, -2);
                    if (return_) return;
                    boolean pr20 = place(1, 1, -2);
                    if (return_) return;
                    //layer 3
                    boolean pr21 = place(2, 2, -1);
                    if (return_) return;
                    boolean pr22 = place(2, 2, 0);
                    if (return_) return;
                    boolean pr23 = place(2, 2, 1);
                    if (return_) return;
                    //
                    boolean pr24 = place(1, 2, 2);
                    if (return_) return;
                    boolean pr25 = place(0, 2, 2);
                    if (return_) return;
                    boolean pr26 = place(-1, 2, 2);
                    if (return_) return;
                    //
                    boolean pr27 = place(-2, 2, 1);
                    if (return_) return;
                    boolean pr28 = place(-2, 2, 0);
                    if (return_) return;
                    boolean pr29 = place(-2, 2, -1);
                    if (return_) return;
                    //
                    boolean pr30 = place(-1, 2, -2);
                    if (return_) return;
                    boolean pr31 = place(0, 2, -2);
                    if (return_) return;
                    boolean pr32 = place(1, 2, -2);
                    if (return_) return;
                    //layer 4
                    boolean pr33 = place(1, 3, -1);
                    if (return_) return;
                    boolean pr34 = place(1, 3, 0);
                    if (return_) return;
                    boolean pr35 = place(1, 3, 1);
                    if (return_) return;
                    //
                    boolean pr36 = place(0, 3, -1);
                    if (return_) return;
                    boolean pr37 = place(0, 3, 0);
                    if (return_) return;
                    boolean pr38 = place(0, 3, 1);
                    if (return_) return;
                    //
                    boolean pr39 = place(-1, 3, -1);
                    if (return_) return;
                    boolean pr40 = place(-1, 3, 0);
                    if (return_) return;
                    boolean pr41 = place(-1, 3, 1);
                    if (return_) return;
                    if (pr1 && pr2 && pr3 && pr4 && pr6 && pr7 && pr8 && pr9 && pr10 && pr11 && pr12 && pr13 && pr14 && pr15 && pr16 && pr17 && pr18 && pr19 && pr20 && pr21 && pr22 && pr23 && pr24 && pr25 && pr26 && pr27 && pr28 && pr29 && pr30 && pr31 && pr32 && pr33 && pr34 && pr35 && pr36 && pr37 && pr38 && pr39 && pr40 && pr41) {
                        this.toggle();
                    } else {
                        return;
                    }
                    break;
            }
        }
        if (buildPlacement.get() == Build.Penis) {
            mc.player.getInventory().selectedSlot = obsidianSlot;
            if (tickskip == 0) {
                tickskip = cooldown.get();
            } else {
                tickskip--;
                return;
            }
            return_ = false;
            switch (directionLite) {
                case EAST:
                    //Код строительства в направлении East
                    boolean p1 = place(1, 0, -1);
                    if (return_) return;
                    boolean p2 = place(1, 0, 0);
                    if (return_) return;
                    boolean p3 = place(1, 0, 1);
                    if (return_) return;
                    boolean p4 = place(1, 1, 0);
                    if (return_) return;
                    boolean p5 = place(1, 2, 0);
                    if (return_) return;
                    boolean p6 = place(1, 3, 0);
                    if (return_) return;
                    if (p1 && p2 && p3 && p4 && p5 && p6) {
                        this.toggle();
                    } else {
                        return;
                    }
                    break;
                case NORTH:
                    //Код строительства в направлении North
                    boolean p7 = place(-1, 0, -1);
                    if (return_) return;
                    boolean p8 = place(0, 0, -1);
                    if (return_) return;
                    boolean p9 = place(1, 0, -1);
                    if (return_) return;
                    boolean p10 = place(0, 1, -1);
                    if (return_) return;
                    boolean p11 = place(0, 2, -1);
                    if (return_) return;
                    boolean p12 = place(0, 3, -1);
                    if (return_) return;
                    if (p7 && p8 && p9 && p10 && p11 && p12) {
                        this.toggle();
                    } else {
                        return;
                    }
                    break;
                case WEST:
                    //Код строительства в направлении West
                    boolean p13 = place(-1, 0, -1);
                    if (return_) return;
                    boolean p14 = place(-1, 0, 0);
                    if (return_) return;
                    boolean p15 = place(-1, 0, 1);
                    if (return_) return;
                    boolean p16 = place(-1, 1, 0);
                    if (return_) return;
                    boolean p17 = place(-1, 2, 0);
                    if (return_) return;
                    boolean p18 = place(-1, 3, 0);
                    if (return_) return;
                    if (p13 && p14 && p15 && p16 && p17 && p18) {
                        this.toggle();
                    } else {
                        return;
                    }
                    break;
                case SOUTH:
                    //Код строительства в направлении South;
                    boolean p19 = place(-1, 0, 1);
                    if (return_) return;
                    boolean p20 = place(0, 0, 1);
                    if (return_) return;
                    boolean p21 = place(1, 0, 1);
                    if (return_) return;
                    boolean p22 = place(0, 1, 1);
                    if (return_) return;
                    boolean p23 = place(0, 2, 1);
                    if (return_) return;
                    boolean p24 = place(0, 3, 1);
                    if (return_) return;
                    if (p19 && p20 && p21 && p22 && p23 && p24) {
                        this.toggle();
                    } else {
                        return;
                    }
                    break;
            }
        }
        if (buildPlacement.get() == Build.Swastika) {
            mc.player.getInventory().selectedSlot = obsidianSlot;
            if (tickskip == 0) {
                tickskip = cooldown.get();
            } else {
                tickskip--;
                return;
            }
            return_ = false;
            switch (directionLite) {
                case EAST:
                    //Код строительства в направлении East
                    boolean w1 = place(1, 0, 2);
                    if (return_) return;
                    boolean w2 = place(1, 0, 0);
                    if (return_) return;
                    boolean w3 = place(1, 0, -1);
                    if (return_) return;
                    boolean w4 = place(1, 0, -2);
                    if (return_) return;
                    boolean w5 = place(1, 1, 2);
                    if (return_) return;
                    boolean w6 = place(1, 1, 0);
                    if (return_) return;
                    boolean w7 = place(1, 2, 2);
                    if (return_) return;
                    boolean w8 = place(1, 2, 1);
                    if (return_) return;
                    boolean w9 = place(1, 2, 0);
                    if (return_) return;
                    boolean w10 = place(1, 2, -1);
                    if (return_) return;
                    boolean w11 = place(1, 2, -2);
                    if (return_) return;
                    boolean w12 = place(1, 3, 0);
                    if (return_) return;
                    boolean w13 = place(1, 3, -2);
                    if (return_) return;
                    boolean w14 = place(1, 4, 2);
                    if (return_) return;
                    boolean w15 = place(1, 4, 1);
                    if (return_) return;
                    boolean w16 = place(1, 4, 0);
                    if (return_) return;
                    boolean w17 = place(1, 4, -2);
                    if (return_) return;
                    if (w1 && w2 && w3 && w4 && w5 && w6 && w7 && w8 && w9 && w10 && w11 && w12 && w13 && w14 && w15 && w16 && w17) {
                        this.toggle();
                    } else {
                        return;
                    }
                    break;
                case NORTH:
                    //Код строительства в направлении North
                    boolean s1 = place(2, 0, -1);
                    if (return_) return;
                    boolean s2 = place(0, 0, -1);
                    if (return_) return;
                    boolean s3 = place(-1, 0, -1);
                    if (return_) return;
                    boolean s4 = place(-2, 0, -1);
                    if (return_) return;
                    boolean s5 = place(2, 1, -1);
                    if (return_) return;
                    boolean s6 = place(0, 1, -1);
                    if (return_) return;
                    boolean s7 = place(2, 2, -1);
                    if (return_) return;
                    boolean s8 = place(1, 2, -1);
                    if (return_) return;
                    boolean s9 = place(0, 2, -1);
                    if (return_) return;
                    boolean s10 = place(-1, 2, -1);
                    if (return_) return;
                    boolean s11 = place(-2, 2, -1);
                    if (return_) return;
                    boolean s12 = place(0, 3, -1);
                    if (return_) return;
                    boolean s13 = place(-2, 3, -1);
                    if (return_) return;
                    boolean s14 = place(2, 4, -1);
                    if (return_) return;
                    boolean s15 = place(1, 4, -1);
                    if (return_) return;
                    boolean s16 = place(0, 4, -1);
                    if (return_) return;
                    boolean s17 = place(-2, 4, -1);
                    if (return_) return;
                    if (s1 && s2 && s3 && s4 && s5 && s6 && s7 && s8 && s9 && s10 && s11 && s12 && s13 && s14 && s15 && s16 && s17) {
                        this.toggle();
                    } else {
                        return;
                    }
                    break;
                case WEST:
                    //Код строительства в направлении West
                    boolean e1 = place(-1, 0, -2);
                    if (return_) return;
                    boolean e2 = place(-1, 0, 0);
                    if (return_) return;
                    boolean e3 = place(-1, 0, 1);
                    if (return_) return;
                    boolean e4 = place(-1, 0, 2);
                    if (return_) return;
                    boolean e5 = place(-1, 1, -2);
                    if (return_) return;
                    boolean e6 = place(-1, 1, 0);
                    if (return_) return;
                    boolean e7 = place(-1, 2, -2);
                    if (return_) return;
                    boolean e8 = place(-1, 2, -1);
                    if (return_) return;
                    boolean e9 = place(-1, 2, 0);
                    if (return_) return;
                    boolean e10 = place(-1, 2, 1);
                    if (return_) return;
                    boolean e11 = place(-1, 2, 2);
                    if (return_) return;
                    boolean e12 = place(-1, 3, 0);
                    if (return_) return;
                    boolean e13 = place(-1, 3, 2);
                    if (return_) return;
                    boolean e14 = place(-1, 4, -2);
                    if (return_) return;
                    boolean e15 = place(-1, 4, -1);
                    if (return_) return;
                    boolean e16 = place(-1, 4, 0);
                    if (return_) return;
                    boolean e17 = place(-1, 4, 2);
                    if (return_) return;
                    if (e1 && e2 && e3 && e4 && e5 && e6 && e7 && e8 && e9 && e10 && e11 && e12 && e13 && e14 && e15 && e16 && e17) {
                        this.toggle();
                    } else {
                        return;
                    }
                    break;
                case SOUTH:
                    //Код строительства в направлении South
                    boolean n1 = place(-2, 0, 1);
                    if (return_) return;
                    boolean n2 = place(0, 0, 1);
                    if (return_) return;
                    boolean n3 = place(1, 0, 1);
                    if (return_) return;
                    boolean n4 = place(2, 0, 1);
                    if (return_) return;
                    boolean n5 = place(-2, 1, 1);
                    if (return_) return;
                    boolean n6 = place(0, 1, 1);
                    if (return_) return;
                    boolean n7 = place(-2, 2, 1);
                    if (return_) return;
                    boolean n8 = place(-1, 2, 1);
                    if (return_) return;
                    boolean n9 = place(0, 2, 1);
                    if (return_) return;
                    boolean n10 = place(1, 2, 1);
                    if (return_) return;
                    boolean n11 = place(2, 2, 1);
                    if (return_) return;
                    boolean n12 = place(0, 3, 1);
                    if (return_) return;
                    boolean n13 = place(2, 3, 1);
                    if (return_) return;
                    boolean n14 = place(-2, 4, 1);
                    if (return_) return;
                    boolean n15 = place(-1, 4, 1);
                    if (return_) return;
                    boolean n16 = place(0, 4, 1);
                    if (return_) return;
                    boolean n17 = place(2, 4, 1);
                    if (return_) return;
                    if (n1 && n2 && n3 && n4 && n5 && n6 && n7 && n8 && n9 && n10 && n11 && n12 && n13 && n14 && n15 && n16 && n17) {
                        this.toggle();
                    } else {
                        return;
                    }
                    break;
            }
        }
        if (buildPlacement.get() == Build.Highway) {
            mc.player.getInventory().selectedSlot = obsidianSlot;
            if (tickskip == 0) {
                tickskip = cooldown.get();
            } else {
                tickskip--;
                return;
            }
            return_ = false;
            switch (direction) {
                case EAST:
                    //Если размер Highway == 1
                    if (size.get() == 1) {
                        boolean eh1 = place(1, -1, 0);
                        if (return_) return;
                        //Если боковые блоки вкл.
                        if (sideBlocks.get()) {
                            boolean Seh1 = place(1, 0, 1);
                            if (return_) return;
                            boolean Seh2 = place(1, 0, -1);
                            if (return_) return;
                            if (Seh1 && Seh2) {
                                return;
                            }
                        }
                        if (eh1) {
                            return;
                        }
                    }
                    //Если размер Highway == 2
                    if (size.get() == 2) {
                        boolean eh1 = place(1, -1, 0);
                        if (return_) return;
                        boolean eh2 = place(1, -1, 1);
                        if (return_) return;
                        boolean eh3 = place(1, -1, -1);
                        if (return_) return;
                        //Если боковые блоки вкл.

                        if (sideBlocks.get()) {
                            boolean Seh1 = place(1, 0, 2);
                            if (return_) return;
                            boolean Seh2 = place(1, 0, -2);
                            if (return_) return;
                            if (Seh1 && Seh2) {
                                return;
                            }
                        }
                        if (eh1 && eh2 && eh3) {
                            return;
                        }
                    }
                    //Если размер Highway == 3
                    if (size.get() == 3) {
                        boolean eh1 = place(1, -1, 0);
                        if (return_) return;
                        boolean eh2 = place(1, -1, 1);
                        if (return_) return;
                        boolean eh3 = place(1, -1, -1);
                        if (return_) return;
                        boolean eh4 = place(1, -1, 2);
                        if (return_) return;
                        boolean eh5 = place(1, -1, -2);
                        if (return_) return;
                        //Если боковые блоки вкл.

                        if (sideBlocks.get()) {
                            boolean Seh1 = place(1, 0, 3);
                            if (return_) return;
                            boolean Seh2 = place(1, 0, -3);
                            if (return_) return;
                            if (Seh1 && Seh2) {
                                return;
                            }
                        }
                        if (eh1 && eh2 && eh3 && eh4 && eh5) {
                            return;
                        }
                    }
                    //Если размер Highway == 4
                    if (size.get() == 4) {
                        boolean eh1 = place(1, -1, 0);
                        if (return_) return;
                        boolean eh2 = place(1, -1, 1);
                        if (return_) return;
                        boolean eh3 = place(1, -1, -1);
                        if (return_) return;
                        boolean eh4 = place(1, -1, 2);
                        if (return_) return;
                        boolean eh5 = place(1, -1, -2);
                        if (return_) return;
                        boolean eh6 = place(1, -1, 3);
                        if (return_) return;
                        boolean eh7 = place(1, -1, -3);
                        if (return_) return;
                        //Если боковые блоки вкл.

                        if (sideBlocks.get()) {
                            boolean Seh1 = place(1, 0, 4);
                            if (return_) return;
                            boolean Seh2 = place(1, 0, -4);
                            if (return_) return;
                            if (Seh1 && Seh2) {
                                return;
                            }
                        }
                        if (eh1 && eh2 && eh3 && eh4 && eh5 && eh6 && eh7) {
                            return;
                        }
                    }
                    //Если размер Highway == 5
                    if (size.get() == 5) {
                        boolean eh1 = place(1, -1, 0);
                        if (return_) return;
                        boolean eh2 = place(1, -1, 1);
                        if (return_) return;
                        boolean eh3 = place(1, -1, -1);
                        if (return_) return;
                        boolean eh4 = place(1, -1, 2);
                        if (return_) return;
                        boolean eh5 = place(1, -1, -2);
                        if (return_) return;
                        boolean eh6 = place(1, -1, 3);
                        if (return_) return;
                        boolean eh7 = place(1, -1, -3);
                        if (return_) return;
                        boolean eh8 = place(1, -1, 4);
                        if (return_) return;
                        boolean eh9 = place(1, -1, -4);
                        if (return_) return;
                        //Если боковые блоки вкл.

                        if (sideBlocks.get()) {
                            boolean Seh1 = place(1, 0, 5);
                            if (return_) return;
                            boolean Seh2 = place(1, 0, -5);
                            if (return_) return;
                            if (Seh1 && Seh2) {
                                return;
                            }
                        }
                        if (eh1 && eh2 && eh3 && eh4 && eh5 && eh6 && eh7 && eh8 && eh9) {
                            return;
                        }
                    }
                    break;
                case NORTH:
                    //Если размер Highway == 1
                    if (size.get() == 1) {
                        boolean eh1 = place(0, -1, -1);
                        if (return_) return;
                        //Если боковые блоки вкл.

                        if (sideBlocks.get()) {
                            boolean Seh1 = place(1, 0, -1);
                            if (return_) return;
                            boolean Seh2 = place(-1, 0, -1);
                            if (return_) return;
                            if (Seh1 && Seh2) {
                                return;
                            }
                        }
                        if (eh1) {
                            return;
                        }
                    }
                    //Если размер Highway == 2
                    if (size.get() == 2) {
                        boolean eh1 = place(0, -1, -1);
                        if (return_) return;
                        boolean eh2 = place(1, -1, -1);
                        if (return_) return;
                        boolean eh3 = place(-1, -1, -1);
                        if (return_) return;
                        //Если боковые блоки вкл.

                        if (sideBlocks.get()) {
                            boolean Seh1 = place(2, 0, -1);
                            if (return_) return;
                            boolean Seh2 = place(-2, 0, -1);
                            if (return_) return;
                            if (Seh1 && Seh2) {
                                return;
                            }
                        }
                        if (eh1 && eh2 && eh3) {
                            return;
                        }
                    }
                    //Если размер Highway == 3
                    if (size.get() == 3) {
                        boolean eh1 = place(0, -1, -1);
                        if (return_) return;
                        boolean eh2 = place(1, -1, -1);
                        if (return_) return;
                        boolean eh3 = place(-1, -1, -1);
                        if (return_) return;
                        boolean eh4 = place(2, -1, -1);
                        if (return_) return;
                        boolean eh5 = place(-2, -1, -1);
                        if (return_) return;
                        //Если боковые блоки вкл.

                        if (sideBlocks.get()) {
                            boolean Seh1 = place(3, 0, -1);
                            if (return_) return;
                            boolean Seh2 = place(-3, 0, -1);
                            if (return_) return;
                            if (Seh1 && Seh2) {
                                return;
                            }
                        }
                        if (eh1 && eh2 && eh3 && eh4 && eh5) {
                            return;
                        }
                    }
                    //Если размер Highway == 4
                    if (size.get() == 4) {
                        boolean eh1 = place(0, -1, -1);
                        if (return_) return;
                        boolean eh2 = place(1, -1, -1);
                        if (return_) return;
                        boolean eh3 = place(-1, -1, -1);
                        if (return_) return;
                        boolean eh4 = place(2, -1, -1);
                        if (return_) return;
                        boolean eh5 = place(-2, -1, -1);
                        if (return_) return;
                        boolean eh6 = place(3, -1, -1);
                        if (return_) return;
                        boolean eh7 = place(-3, -1, -1);
                        if (return_) return;
                        //Если боковые блоки вкл.

                        if (sideBlocks.get()) {
                            boolean Seh1 = place(4, 0, -1);
                            if (return_) return;
                            boolean Seh2 = place(-4, 0, -1);
                            if (return_) return;
                            if (Seh1 && Seh2) {
                                return;
                            }
                        }
                        if (eh1 && eh2 && eh3 && eh4 && eh5 && eh6 && eh7) {
                            return;
                        }
                    }
                    //Если размер Highway == 5
                    if (size.get() == 5) {
                        boolean eh1 = place(0, -1, -1);
                        if (return_) return;
                        boolean eh2 = place(1, -1, -1);
                        if (return_) return;
                        boolean eh3 = place(-1, -1, -1);
                        if (return_) return;
                        boolean eh4 = place(2, -1, -1);
                        if (return_) return;
                        boolean eh5 = place(-2, -1, -1);
                        if (return_) return;
                        boolean eh6 = place(3, -1, -1);
                        if (return_) return;
                        boolean eh7 = place(-3, -1, -1);
                        if (return_) return;
                        boolean eh8 = place(4, -1, -1);
                        if (return_) return;
                        boolean eh9 = place(-4, -1, -1);
                        if (return_) return;
                        //Если боковые блоки вкл.

                        if (sideBlocks.get()) {
                            boolean Seh1 = place(5, 0, -1);
                            if (return_) return;
                            boolean Seh2 = place(-5, 0, -1);
                            if (return_) return;
                            if (Seh1 && Seh2) {
                                return;
                            }
                        }
                        if (eh1 && eh2 && eh3 && eh4 && eh5 && eh6 && eh7 && eh8 && eh9) {
                            return;
                        }
                    }
                    break;
                case WEST:
                    //Если размер Highway == 1
                    if (size.get() == 1) {
                        boolean eh1 = place(-1, -1, 0);
                        if (return_) return;
                        //Если боковые блоки вкл.

                        if (sideBlocks.get()) {
                            boolean Seh1 = place(-1, 0, 1);
                            if (return_) return;
                            boolean Seh2 = place(-1, 0, -1);
                            if (return_) return;
                            if (Seh1 && Seh2) {
                                return;
                            }
                        }
                        if (eh1) {
                            return;
                        }
                    }
                    //Если размер Highway == 2
                    if (size.get() == 2) {
                        boolean eh1 = place(-1, -1, 0);
                        if (return_) return;
                        boolean eh2 = place(-1, -1, 1);
                        if (return_) return;
                        boolean eh3 = place(-1, -1, -1);
                        if (return_) return;
                        //Если боковые блоки вкл.

                        if (sideBlocks.get()) {
                            boolean Seh1 = place(-1, 0, 2);
                            if (return_) return;
                            boolean Seh2 = place(-1, 0, -2);
                            if (return_) return;
                            if (Seh1 && Seh2) {
                                return;
                            }
                        }
                        if (eh1 && eh2 && eh3) {
                            return;
                        }
                    }
                    //Если размер Highway == 3
                    if (size.get() == 3) {
                        boolean eh1 = place(-1, -1, 0);
                        if (return_) return;
                        boolean eh2 = place(-1, -1, 1);
                        if (return_) return;
                        boolean eh3 = place(-1, -1, -1);
                        if (return_) return;
                        boolean eh4 = place(-1, -1, 2);
                        if (return_) return;
                        boolean eh5 = place(-1, -1, -2);
                        if (return_) return;
                        //Если боковые блоки вкл.

                        if (sideBlocks.get()) {
                            boolean Seh1 = place(-1, 0, 3);
                            if (return_) return;
                            boolean Seh2 = place(-1, 0, -3);
                            if (return_) return;
                            if (Seh1 && Seh2) {
                                return;
                            }
                        }
                        if (eh1 && eh2 && eh3 && eh4 && eh5) {
                            return;
                        }
                    }
                    //Если размер Highway == 4
                    if (size.get() == 4) {
                        boolean eh1 = place(-1, -1, 0);
                        if (return_) return;
                        boolean eh2 = place(-1, -1, 1);
                        if (return_) return;
                        boolean eh3 = place(-1, -1, -1);
                        if (return_) return;
                        boolean eh4 = place(-1, -1, 2);
                        if (return_) return;
                        boolean eh5 = place(-1, -1, -2);
                        if (return_) return;
                        boolean eh6 = place(-1, -1, 3);
                        if (return_) return;
                        boolean eh7 = place(-1, -1, -3);
                        if (return_) return;
                        //Если боковые блоки вкл.

                        if (sideBlocks.get()) {
                            boolean Seh1 = place(-1, 0, 4);
                            if (return_) return;
                            boolean Seh2 = place(-1, 0, -4);
                            if (return_) return;
                            if (Seh1 && Seh2) {
                                return;
                            }
                        }
                        if (eh1 && eh2 && eh3 && eh4 && eh5 && eh6 && eh7) {
                            return;
                        }
                    }
                    //Если размер Highway == 5
                    if (size.get() == 5) {
                        boolean eh1 = place(-1, -1, 0);
                        if (return_) return;
                        boolean eh2 = place(-1, -1, 1);
                        if (return_) return;
                        boolean eh3 = place(-1, -1, -1);
                        if (return_) return;
                        boolean eh4 = place(-1, -1, 2);
                        if (return_) return;
                        boolean eh5 = place(-1, -1, -2);
                        if (return_) return;
                        boolean eh6 = place(-1, -1, 3);
                        if (return_) return;
                        boolean eh7 = place(-1, -1, -3);
                        if (return_) return;
                        boolean eh8 = place(-1, -1, 4);
                        if (return_) return;
                        boolean eh9 = place(-1, -1, -4);
                        if (return_) return;
                        //Если боковые блоки вкл.

                        if (sideBlocks.get()) {
                            boolean Seh1 = place(-1, 0, 5);
                            if (return_) return;
                            boolean Seh2 = place(-1, 0, -5);
                            if (return_) return;
                            if (Seh1 && Seh2) {
                                return;
                            }
                        }
                        if (eh1 && eh2 && eh3 && eh4 && eh5 && eh6 && eh7 && eh8 && eh9) {
                            return;
                        }
                    }
                    break;
                case SOUTH:
                    //Если размер Highway == 1
                    if (size.get() == 1) {
                        boolean eh1 = place(0, -1, 1);
                        if (return_) return;
                        //Если боковые блоки вкл.

                        if (sideBlocks.get()) {
                            boolean Seh1 = place(1, 0, 1);
                            if (return_) return;
                            boolean Seh2 = place(-1, 0, 1);
                            if (return_) return;
                            if (Seh1 && Seh2) {
                                return;
                            }
                        }
                        if (eh1) {
                            return;
                        }
                    }
                    //Если размер Highway == 2
                    if (size.get() == 2) {
                        boolean eh1 = place(0, -1, 1);
                        if (return_) return;
                        boolean eh2 = place(1, -1, 1);
                        if (return_) return;
                        boolean eh3 = place(-1, -1, 1);
                        if (return_) return;
                        //Если боковые блоки вкл.

                        if (sideBlocks.get()) {
                            boolean Seh1 = place(2, 0, 1);
                            if (return_) return;
                            boolean Seh2 = place(-2, 0, 1);
                            if (return_) return;
                            if (Seh1 && Seh2) {
                                return;
                            }
                        }
                        if (eh1 && eh2 && eh3) {
                            return;
                        }
                    }
                    //Если размер Highway == 3
                    if (size.get() == 3) {
                        boolean eh1 = place(0, -1, 1);
                        if (return_) return;
                        boolean eh2 = place(1, -1, 1);
                        if (return_) return;
                        boolean eh3 = place(-1, -1, 1);
                        if (return_) return;
                        boolean eh4 = place(2, -1, 1);
                        if (return_) return;
                        boolean eh5 = place(-2, -1, 1);
                        if (return_) return;
                        //Если боковые блоки вкл.

                        if (sideBlocks.get()) {
                            boolean Seh1 = place(3, 0, 1);
                            if (return_) return;
                            boolean Seh2 = place(-3, 0, 1);
                            if (return_) return;
                            if (Seh1 && Seh2) {
                                return;
                            }
                        }
                        if (eh1 && eh2 && eh3 && eh4 && eh5) {
                            return;
                        }
                    }
                    //Если размер Highway == 4
                    if (size.get() == 4) {
                        boolean eh1 = place(0, -1, 1);
                        if (return_) return;
                        boolean eh2 = place(1, -1, 1);
                        if (return_) return;
                        boolean eh3 = place(-1, -1, 1);
                        if (return_) return;
                        boolean eh4 = place(2, -1, 1);
                        if (return_) return;
                        boolean eh5 = place(-2, -1, 1);
                        if (return_) return;
                        boolean eh6 = place(3, -1, 1);
                        if (return_) return;
                        boolean eh7 = place(-3, -1, 1);
                        if (return_) return;
                        //Если боковые блоки вкл.

                        if (sideBlocks.get()) {
                            boolean Seh1 = place(4, 0, 1);
                            if (return_) return;
                            boolean Seh2 = place(-4, 0, 1);
                            if (return_) return;
                            if (Seh1 && Seh2) {
                                return;
                            }
                        }
                        if (eh1 && eh2 && eh3 && eh4 && eh5 && eh6 && eh7) {
                            return;
                        }
                    }
                    //Если размер Highway == 5
                    if (size.get() == 5) {
                        boolean eh1 = place(0, -1, 1);
                        if (return_) return;
                        boolean eh2 = place(1, -1, 1);
                        if (return_) return;
                        boolean eh3 = place(-1, -1, 1);
                        if (return_) return;
                        boolean eh4 = place(2, -1, 1);
                        if (return_) return;
                        boolean eh5 = place(-2, -1, 1);
                        if (return_) return;
                        boolean eh6 = place(3, -1, 1);
                        if (return_) return;
                        boolean eh7 = place(-3, -1, 1);
                        if (return_) return;
                        boolean eh8 = place(4, -1, 1);
                        if (return_) return;
                        boolean eh9 = place(-4, -1, 1);
                        if (return_) return;
                        //Если боковые блоки вкл.

                        if (sideBlocks.get()) {
                            boolean Seh1 = place(5, 0, 1);
                            if (return_) return;
                            boolean Seh2 = place(-5, 0, 1);
                            if (return_) return;
                            if (Seh1 && Seh2) {
                                return;
                            }
                        }
                        if (eh1 && eh2 && eh3 && eh4 && eh5 && eh6 && eh7 && eh8 && eh9) {
                            return;
                        }
                    }
                    break;
                case EAST_SOUTH:
                    //Если размер Highway == 1
                    if (size.get() == 1) {
                        boolean eh1 = place(0, -1, 1);
                        if (return_) return;
                        boolean eh2 = place(1, -1, 1);
                        if (return_) return;
                        boolean eh3 = place(1, -1, 0);
                        if (return_) return;
                        //Если боковые блоки вкл.

                        if (sideBlocks.get()) {
                            boolean Seh1 = place(2, 0, 0);
                            if (return_) return;
                            boolean Seh2 = place(0, 0, 2);
                            if (return_) return;
                            if (Seh1 && Seh2) {
                                return;
                            }
                        }
                        if (eh1 && eh2 && eh3) {
                            return;
                        }
                    }
                    //Если размер Highway == 2
                    if (size.get() == 2) {
                        boolean eh1 = place(0, -1, 1);
                        if (return_) return;
                        boolean eh2 = place(1, -1, 1);
                        if (return_) return;
                        boolean eh3 = place(1, -1, 0);
                        if (return_) return;
                        boolean eh4 = place(2, -1, 0);
                        if (return_) return;
                        boolean eh5 = place(0, -1, 2);
                        if (return_) return;
                        //Если боковые блоки вкл.

                        if (sideBlocks.get()) {
                            boolean Seh1 = place(2, 0, -1);
                            if (return_) return;
                            boolean Seh2 = place(-1, 0, 2);
                            if (return_) return;
                            if (Seh1 && Seh2) {
                                return;
                            }
                        }
                        if (eh1 && eh2 && eh3 && eh4 && eh5) {
                            return;
                        }
                    }
                    //Если размер Highway == 3
                    if (size.get() == 3) {
                        boolean eh1 = place(0, -1, 1);
                        if (return_) return;
                        boolean eh2 = place(1, -1, 1);
                        if (return_) return;
                        boolean eh3 = place(1, -1, 0);
                        if (return_) return;
                        boolean eh4 = place(2, -1, 0);
                        if (return_) return;
                        boolean eh5 = place(0, -1, 2);
                        if (return_) return;
                        boolean eh6 = place(2, -1, -1);
                        if (return_) return;
                        boolean eh7 = place(-1, -1, 2);
                        if (return_) return;
                        //Если боковые блоки вкл.

                        if (sideBlocks.get()) {
                            boolean Seh1 = place(3, 0, -1);
                            if (return_) return;
                            boolean Seh2 = place(-1, 0, 3);
                            if (return_) return;
                            if (Seh1 && Seh2) {
                                return;
                            }
                        }
                        if (eh1 && eh2 && eh3 && eh4 && eh5 && eh6 && eh7) {
                            return;
                        }
                    }
                    //Если размер Highway == 4
                    if (size.get() == 4) {
                        boolean eh1 = place(0, -1, 1);
                        if (return_) return;
                        boolean eh2 = place(1, -1, 1);
                        if (return_) return;
                        boolean eh3 = place(1, -1, 0);
                        if (return_) return;
                        boolean eh4 = place(2, -1, 0);
                        if (return_) return;
                        boolean eh5 = place(0, -1, 2);
                        if (return_) return;
                        boolean eh6 = place(2, -1, -1);
                        if (return_) return;
                        boolean eh7 = place(-1, -1, 2);
                        if (return_) return;
                        boolean eh8 = place(3, -1, -1);
                        if (return_) return;
                        boolean eh9 = place(-1, -1, 3);
                        if (return_) return;
                        //Если боковые блоки вкл.

                        if (sideBlocks.get()) {
                            boolean Seh1 = place(3, 0, -2);
                            if (return_) return;
                            boolean Seh2 = place(-2, 0, 3);
                            if (return_) return;
                            if (Seh1 && Seh2) {
                                return;
                            }
                        }
                        if (eh1 && eh2 && eh3 && eh4 && eh5 && eh6 && eh7 && eh8 && eh9) {
                            return;
                        }
                    }
                    //Если размер Highway == 5
                    if (size.get() == 5) {
                        boolean eh1 = place(0, -1, 1);
                        if (return_) return;
                        boolean eh2 = place(1, -1, 1);
                        if (return_) return;
                        boolean eh3 = place(1, -1, 0);
                        if (return_) return;
                        boolean eh4 = place(2, -1, 0);
                        if (return_) return;
                        boolean eh5 = place(0, -1, 2);
                        if (return_) return;
                        boolean eh6 = place(2, -1, -1);
                        if (return_) return;
                        boolean eh7 = place(-1, -1, 2);
                        if (return_) return;
                        boolean eh8 = place(3, -1, -1);
                        if (return_) return;
                        boolean eh9 = place(-1, -1, 3);
                        if (return_) return;
                        boolean eh10 = place(3, -1, -2);
                        if (return_) return;
                        boolean eh11 = place(-2, -1, 3);
                        if (return_) return;
                        //Если боковые блоки вкл.

                        if (sideBlocks.get()) {
                            boolean Seh1 = place(4, 0, -2);
                            if (return_) return;
                            boolean Seh2 = place(-2, 0, 4);
                            if (return_) return;
                            if (Seh1 && Seh2) {
                                return;
                            }
                        }
                        if (eh1 && eh2 && eh3 && eh4 && eh5 && eh6 && eh7 && eh8 && eh9 && eh10 && eh11) {
                            return;
                        }
                    }
                    break;
                case SOUTH_WEST:
                    //Если размер Highway == 1
                    if (size.get() == 1) {
                        boolean eh1 = place(0, -1, 1);
                        if (return_) return;
                        boolean eh2 = place(-1, -1, 1);
                        if (return_) return;
                        boolean eh3 = place(-1, -1, 0);
                        if (return_) return;
                        //Если боковые блоки вкл.

                        if (sideBlocks.get()) {
                            boolean Seh1 = place(0, 0, 2);
                            if (return_) return;
                            boolean Seh2 = place(-2, 0, 0);
                            if (return_) return;
                            if (Seh1 && Seh2) {
                                return;
                            }
                        }
                        if (eh1 && eh2 && eh3) {
                            return;
                        }
                    }
                    //Если размер Highway == 2
                    if (size.get() == 2) {
                        boolean eh1 = place(0, -1, 1);
                        if (return_) return;
                        boolean eh2 = place(-1, -1, 1);
                        if (return_) return;
                        boolean eh3 = place(-1, -1, 0);
                        if (return_) return;
                        boolean eh4 = place(0, -1, 2);
                        if (return_) return;
                        boolean eh5 = place(-2, -1, 0);
                        if (return_) return;
                        //Если боковые блоки вкл.

                        if (sideBlocks.get()) {
                            boolean Seh1 = place(-2, 0, -1);
                            if (return_) return;
                            boolean Seh2 = place(1, 0, 2);
                            if (return_) return;
                            if (Seh1 && Seh2) {
                                return;
                            }
                        }
                        if (eh1 && eh2 && eh3 && eh4 && eh5) {
                            return;
                        }
                    }
                    //Если размер Highway == 3
                    if (size.get() == 3) {
                        boolean eh1 = place(0, -1, 1);
                        if (return_) return;
                        boolean eh2 = place(-1, -1, 1);
                        if (return_) return;
                        boolean eh3 = place(-1, -1, 0);
                        if (return_) return;
                        boolean eh4 = place(0, -1, 2);
                        if (return_) return;
                        boolean eh5 = place(-2, -1, 0);
                        if (return_) return;
                        boolean eh6 = place(1, -1, 2);
                        if (return_) return;
                        boolean eh7 = place(-2, -1, -1);
                        if (return_) return;
                        //Если боковые блоки вкл.

                        if (sideBlocks.get()) {
                            boolean Seh1 = place(-3, 0, -1);
                            if (return_) return;
                            boolean Seh2 = place(1, 0, 3);
                            if (return_) return;
                            if (Seh1 && Seh2) {
                                return;
                            }
                        }
                        if (eh1 && eh2 && eh3 && eh4 && eh5 && eh6 && eh7) {
                            return;
                        }
                    }
                    //Если размер Highway == 4
                    if (size.get() == 4) {
                        boolean eh1 = place(0, -1, 1);
                        if (return_) return;
                        boolean eh2 = place(-1, -1, 1);
                        if (return_) return;
                        boolean eh3 = place(-1, -1, 0);
                        if (return_) return;
                        boolean eh4 = place(0, -1, 2);
                        if (return_) return;
                        boolean eh5 = place(-2, -1, 0);
                        if (return_) return;
                        boolean eh6 = place(1, -1, 2);
                        if (return_) return;
                        boolean eh7 = place(-2, -1, -1);
                        if (return_) return;
                        boolean eh8 = place(-3, -1, -1);
                        if (return_) return;
                        boolean eh9 = place(1, -1, 3);
                        if (return_) return;
                        //Если боковые блоки вкл.

                        if (sideBlocks.get()) {
                            boolean Seh1 = place(-3, 0, -2);
                            if (return_) return;
                            boolean Seh2 = place(2, 0, 3);
                            if (return_) return;
                            if (Seh1 && Seh2) {
                                return;
                            }
                        }
                        if (eh1 && eh2 && eh3 && eh4 && eh5 && eh6 && eh7 && eh8 && eh9) {
                            return;
                        }
                    }
                    //Если размер Highway == 5
                    if (size.get() == 5) {
                        boolean eh1 = place(0, -1, 1);
                        if (return_) return;
                        boolean eh2 = place(-1, -1, 1);
                        if (return_) return;
                        boolean eh3 = place(-1, -1, 0);
                        if (return_) return;
                        boolean eh4 = place(0, -1, 2);
                        if (return_) return;
                        boolean eh5 = place(-2, -1, 0);
                        if (return_) return;
                        boolean eh6 = place(1, -1, 2);
                        if (return_) return;
                        boolean eh7 = place(-2, -1, -1);
                        if (return_) return;
                        boolean eh8 = place(-3, -1, -1);
                        if (return_) return;
                        boolean eh9 = place(1, -1, 3);
                        if (return_) return;
                        boolean eh10 = place(-3, -1, -2);
                        if (return_) return;
                        boolean eh11 = place(2, -1, 3);
                        if (return_) return;
                        //Если боковые блоки вкл.

                        if (sideBlocks.get()) {
                            boolean Seh1 = place(2, 0, 4);
                            if (return_) return;
                            boolean Seh2 = place(-4, 0, -2);
                            if (return_) return;
                            if (Seh1 && Seh2) {
                                return;
                            }
                        }
                        if (eh1 && eh2 && eh3 && eh4 && eh5 && eh6 && eh7 && eh8 && eh9 && eh10 && eh11) {
                            return;
                        }
                    }
                    break;
                case WEST_NORTH:
                    if (size.get() == 1) {
                        boolean eh1 = place(-1, -1, 0);
                        if (return_) return;
                        boolean eh2 = place(-1, -1, -1);
                        if (return_) return;
                        boolean eh3 = place(0, -1, -1);
                        if (return_) return;
                        //Если боковые блоки вкл.

                        if (sideBlocks.get()) {
                            boolean Seh1 = place(0, 0, -2);
                            if (return_) return;
                            boolean Seh2 = place(-2, 0, 0);
                            if (return_) return;
                            if (Seh1 && Seh2) {
                                return;
                            }
                        }
                        if (eh1 && eh2 && eh3) {
                            return;
                        }
                    }
                    if (size.get() == 2) {
                        boolean eh1 = place(-1, -1, 0);
                        if (return_) return;
                        boolean eh2 = place(-1, -1, -1);
                        if (return_) return;
                        boolean eh3 = place(0, -1, -1);
                        if (return_) return;
                        boolean eh4 = place(0, -1, -2);
                        if (return_) return;
                        boolean eh5 = place(-2, -1, 0);
                        if (return_) return;
                        //Если боковые блоки вкл.

                        if (sideBlocks.get()) {
                            boolean Seh1 = place(-2, 0, 1);
                            if (return_) return;
                            boolean Seh2 = place(1, 0, -2);
                            if (return_) return;
                            if (Seh1 && Seh2) {
                                return;
                            }
                        }
                        if (eh1 && eh2 && eh3 && eh4 && eh5) {
                            return;
                        }
                    }
                    if (size.get() == 3) {
                        boolean eh1 = place(-1, -1, 0);
                        if (return_) return;
                        boolean eh2 = place(-1, -1, -1);
                        if (return_) return;
                        boolean eh3 = place(0, -1, -1);
                        if (return_) return;
                        boolean eh4 = place(0, -1, -2);
                        if (return_) return;
                        boolean eh5 = place(-2, -1, 0);
                        if (return_) return;
                        boolean eh6 = place(-2, -1, 1);
                        if (return_) return;
                        boolean eh7 = place(1, -1, -2);
                        if (return_) return;
                        //Если боковые блоки вкл.

                        if (sideBlocks.get()) {
                            boolean Seh1 = place(-3, 0, 1);
                            if (return_) return;
                            boolean Seh2 = place(1, 0, -3);
                            if (return_) return;
                            if (Seh1 && Seh2) {
                                return;
                            }
                        }
                        if (eh1 && eh2 && eh3 && eh4 && eh5 && eh6 && eh7) {
                            return;
                        }
                    }
                    if (size.get() == 4) {
                        boolean eh1 = place(-1, -1, 0);
                        if (return_) return;
                        boolean eh2 = place(-1, -1, -1);
                        if (return_) return;
                        boolean eh3 = place(0, -1, -1);
                        if (return_) return;
                        boolean eh4 = place(0, -1, -2);
                        if (return_) return;
                        boolean eh5 = place(-2, -1, 0);
                        if (return_) return;
                        boolean eh6 = place(-2, -1, 1);
                        if (return_) return;
                        boolean eh7 = place(1, -1, -2);
                        if (return_) return;
                        boolean eh8 = place(-3, -1, 1);
                        if (return_) return;
                        boolean eh9 = place(1, -1, -3);
                        if (return_) return;
                        //Если боковые блоки вкл.

                        if (sideBlocks.get()) {
                            boolean Seh1 = place(-3, 0, 2);
                            if (return_) return;
                            boolean Seh2 = place(2, 0, -3);
                            if (return_) return;
                            if (Seh1 && Seh2) {
                                return;
                            }
                        }
                        if (eh1 && eh2 && eh3 && eh4 && eh5 && eh6 && eh7 && eh8 && eh9) {
                            return;
                        }
                    }
                    if (size.get() == 5) {
                        boolean eh1 = place(-1, -1, 0);
                        if (return_) return;
                        boolean eh2 = place(-1, -1, -1);
                        if (return_) return;
                        boolean eh3 = place(0, -1, -1);
                        if (return_) return;
                        boolean eh4 = place(0, -1, -2);
                        if (return_) return;
                        boolean eh5 = place(-2, -1, 0);
                        if (return_) return;
                        boolean eh6 = place(-2, -1, 1);
                        if (return_) return;
                        boolean eh7 = place(1, -1, -2);
                        if (return_) return;
                        boolean eh8 = place(-3, -1, 1);
                        if (return_) return;
                        boolean eh9 = place(1, -1, -3);
                        if (return_) return;
                        boolean eh10 = place(-3, -1, 2);
                        if (return_) return;
                        boolean eh11 = place(2, -1, -3);
                        if (return_) return;
                        //Если боковые блоки вкл.

                        if (sideBlocks.get()) {
                            boolean Seh1 = place(-4, 0, 2);
                            if (return_) return;
                            boolean Seh2 = place(2, 0, -4);
                            if (return_) return;
                            if (Seh1 && Seh2) {
                                return;
                            }
                        }
                        if (eh1 && eh2 && eh3 && eh4 && eh5 && eh6 && eh7 && eh8 && eh9 && eh10 && eh11) {
                            return;
                        }
                    }
                case NORTH_EAST:
                    //Если размер Highway == 1
                    if (size.get() == 1) {
                        boolean eh1 = place(1, -1, 0);
                        if (return_) return;
                        boolean eh2 = place(1, -1, -1);
                        if (return_) return;
                        boolean eh3 = place(0, -1, -1);
                        if (return_) return;
                        //Если боковые блоки вкл.

                        if (sideBlocks.get()) {
                            boolean Seh1 = place(0, 0, -2);
                            if (return_) return;
                            boolean Seh2 = place(2, 0, 0);
                            if (return_) return;
                            if (Seh1 && Seh2) {
                                return;
                            }
                        }
                        if (eh1 && eh2 && eh3) {
                            return;
                        }
                    }
                    //Если размер Highway == 2
                    if (size.get() == 2) {
                        boolean eh1 = place(1, -1, 0);
                        if (return_) return;
                        boolean eh2 = place(1, -1, -1);
                        if (return_) return;
                        boolean eh3 = place(0, -1, -1);
                        if (return_) return;
                        boolean eh4 = place(0, -1, -2);
                        if (return_) return;
                        boolean eh5 = place(2, -1, 0);
                        if (return_) return;
                        //Если боковые блоки вкл.

                        if (sideBlocks.get()) {
                            boolean Seh1 = place(-1, 0, -2);
                            if (return_) return;
                            boolean Seh2 = place(2, 0, 1);
                            if (return_) return;
                            if (Seh1 && Seh2) {
                                return;
                            }
                        }
                        if (eh1 && eh2 && eh3 && eh4 && eh5) {
                            return;
                        }
                    }
                    //Если размер Highway == 3
                    if (size.get() == 3) {
                        boolean eh1 = place(1, -1, 0);
                        if (return_) return;
                        boolean eh2 = place(1, -1, -1);
                        if (return_) return;
                        boolean eh3 = place(0, -1, -1);
                        if (return_) return;
                        boolean eh4 = place(0, -1, -2);
                        if (return_) return;
                        boolean eh5 = place(2, -1, 0);
                        if (return_) return;
                        boolean eh6 = place(2, -1, 1);
                        if (return_) return;
                        boolean eh7 = place(-1, -1, -2);
                        if (return_) return;
                        //Если боковые блоки вкл.

                        if (sideBlocks.get()) {
                            boolean Seh1 = place(-1, 0, -3);
                            if (return_) return;
                            boolean Seh2 = place(3, 0, 1);
                            if (return_) return;
                            if (Seh1 && Seh2) {
                                return;
                            }
                        }
                        if (eh1 && eh2 && eh3 && eh4 && eh5 && eh6 && eh7) {
                            return;
                        }
                    }
                    //Если размер Highway == 4
                    if (size.get() == 4) {
                        boolean eh1 = place(1, -1, 0);
                        if (return_) return;
                        boolean eh2 = place(1, -1, -1);
                        if (return_) return;
                        boolean eh3 = place(0, -1, -1);
                        if (return_) return;
                        boolean eh4 = place(0, -1, -2);
                        if (return_) return;
                        boolean eh5 = place(2, -1, 0);
                        if (return_) return;
                        boolean eh6 = place(2, -1, 1);
                        if (return_) return;
                        boolean eh7 = place(-1, -1, -2);
                        if (return_) return;
                        boolean eh8 = place(-1, -1, -3);
                        if (return_) return;
                        boolean eh9 = place(3, -1, 1);
                        if (return_) return;
                        //Если боковые блоки вкл.

                        if (sideBlocks.get()) {
                            boolean Seh1 = place(-2, 0, -3);
                            if (return_) return;
                            boolean Seh2 = place(3, 0, 2);
                            if (return_) return;
                            if (Seh1 && Seh2) {
                                return;
                            }
                        }
                        if (eh1 && eh2 && eh3 && eh4 && eh5 && eh6 && eh7 && eh8 && eh9) {
                            return;
                        }
                    }
                    //Если размер Highway == 5
                    if (size.get() == 5) {
                        boolean eh1 = place(1, -1, 0);
                        if (return_) return;
                        boolean eh2 = place(1, -1, -1);
                        if (return_) return;
                        boolean eh3 = place(0, -1, -1);
                        if (return_) return;
                        boolean eh4 = place(0, -1, -2);
                        if (return_) return;
                        boolean eh5 = place(2, -1, 0);
                        if (return_) return;
                        boolean eh6 = place(2, -1, 1);
                        if (return_) return;
                        boolean eh7 = place(-1, -1, -2);
                        if (return_) return;
                        boolean eh8 = place(-1, -1, -3);
                        if (return_) return;
                        boolean eh9 = place(3, -1, 1);
                        if (return_) return;
                        boolean eh10 = place(-2, -1, -3);
                        if (return_) return;
                        boolean eh11 = place(3, -1, 2);
                        if (return_) return;
                        //Если боковые блоки вкл.

                        if (sideBlocks.get()) {
                            boolean Seh1 = place(-2, 0, -4);
                            if (return_) return;
                            boolean Seh2 = place(4, 0, 2);
                            if (return_) return;
                            if (Seh1 && Seh2) {
                                return;
                            }
                        }
                        if (eh1 && eh2 && eh3 && eh4 && eh5 && eh6 && eh7 && eh8 && eh9 && eh10 && eh11) {
                            return;
                        }
                    }
                    break;
            }
        }
    }

    private boolean place(int x, int y, int z) {
        setBlockPos(x, y, z);
        assert mc.world != null;

        if (!isReplaceable(blockPos)) return true;

        if (BlockUtils.place(blockPos, InvUtils.findInHotbar(Items.OBSIDIAN), false, 100, true)) {
            return_ = true;
        }

        return false;
    }

    private void setBlockPos(int x, int y, int z) {
        assert mc.player != null;
        blockPos.set(mc.player.getX() + x, mc.player.getY() + y, mc.player.getZ() + z);
    }

    private DirectionLite getDirectionLite(PlayerEntity player) {
        double yaw = player.getYaw();
        if (yaw == 0) return DirectionLite.SOUTH;
        if (yaw < 0) {
            yaw = yaw - MathHelper.ceil(yaw / 360) * 360;
            if (yaw < -180) {
                yaw = 360 + yaw;
            }
        } else {
            yaw = yaw - MathHelper.floor(yaw / 360) * 360;
            if (yaw > 180) {
                yaw = -360 + yaw;
            }
        }

        if (yaw >= 135.5 || yaw < -135.5) return DirectionLite.NORTH;
        if (yaw >= -135.5 && yaw < -44.5) return DirectionLite.EAST;
        if (yaw >= -44.5 && yaw <= 44.5) return DirectionLite.SOUTH;
        if (yaw >= 44.5 && yaw < 135.5) return DirectionLite.WEST;
        return DirectionLite.SOUTH;
    }

    private Direction getDirection(PlayerEntity player) {
        double yaw = player.getYaw();
        if (yaw == 0) return Direction.SOUTH;
        if (yaw < 0) {
            yaw = yaw - MathHelper.ceil(yaw / 360) * 360;
            if (yaw < -180) {
                yaw = 360 + yaw;
            }
        } else {
            yaw = yaw - MathHelper.floor(yaw / 360) * 360;
            if (yaw > 180) {
                yaw = -360 + yaw;
            }
        }

        if (yaw >= 157.5 || yaw < -157.5) return Direction.NORTH;
        if (yaw >= -157.5 && yaw < -112.5) return Direction.NORTH_EAST;
        if (yaw >= -112.5 && yaw < -67.5) return Direction.EAST;
        if (yaw >= -67.5 && yaw < -22.5) return Direction.EAST_SOUTH;
        if ((yaw >= -22.5 && yaw <= 0) || (yaw > 0 && yaw < 22.5)) return Direction.SOUTH;
        if (yaw >= 22.5 && yaw < 67.5) return Direction.SOUTH_WEST;
        if (yaw >= 67.5 && yaw < 112.5) return Direction.WEST;
        if (yaw >= 112.5) return Direction.WEST_NORTH;
        return Direction.SOUTH;
    }

    @Override
    public String getInfoString() {
        if (buildPlacement.get() == Build.NomadHut) return BUILD[0];
        if (buildPlacement.get() == Build.Penis) return BUILD[1];
        if (buildPlacement.get() == Build.Swastika) return BUILD[2];
        if (buildPlacement.get() == Build.Highway) return BUILD[3];
        else return "None";
    }

    public enum Build {
        NomadHut,
        Penis,
        Swastika,
        Highway
    }

    private enum Direction {
        SOUTH,
        SOUTH_WEST,
        WEST,
        WEST_NORTH,
        NORTH,
        NORTH_EAST,
        EAST,
        EAST_SOUTH
    }

    private enum DirectionLite {
        SOUTH,
        WEST,
        NORTH,
        EAST
    }
}
