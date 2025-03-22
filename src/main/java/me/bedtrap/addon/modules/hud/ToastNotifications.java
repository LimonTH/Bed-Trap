package me.bedtrap.addon.modules.hud;

import me.bedtrap.addon.BedTrap;
import meteordevelopment.meteorclient.renderer.Renderer2D;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.hud.Alignment;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.SoundEvents;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

import static me.bedtrap.addon.modules.combat.AutoMinecart.Interaction.mc;

public class ToastNotifications extends HudElement {
    public static final List<TosterNotifications> toasts = new ArrayList<>();

    private static String renderingMes = "";

    private static int timer1;
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    public final Setting<Boolean> toggleMessage = sgGeneral.add(new BoolSetting.Builder().name("toggle-message").description("Sends info about toggled modules.").defaultValue(true).build());
    public final Setting<List<Module>> toggleList = sgGeneral.add(new ModuleListSetting.Builder().name("Modules for displaying").defaultValue(Modules.get().getGroup(BedTrap.Combat)).visible(toggleMessage::get).build());
    public final Setting<Boolean> sound = sgGeneral.add(new BoolSetting.Builder().name("sound").defaultValue(true).build());
    public final Setting<Boolean> invertedX = sgGeneral.add(new BoolSetting.Builder().name("invert-X").description("Invert slide X of toasts.").defaultValue(false).build());
    public final Setting<Boolean> invertedY = sgGeneral.add(new BoolSetting.Builder().name("invert-Y").description("Invert spawn Y of toasts.").defaultValue(false).build());
    private final Setting<Integer> removeDelay = sgGeneral.add(new IntSetting.Builder().name("remove-delay").description("Delay to clean latest message.").defaultValue(7).min(1).sliderMax(10).build());
    private final Setting<Double> scale = sgGeneral.add(new DoubleSetting.Builder().name("box-scale").description("The scale of box.").defaultValue(1.0D).min(1.0D).sliderMax(5.0D).build());
    private final Setting<Double> paddingScale = sgGeneral.add(new DoubleSetting.Builder().name("padding-scale").description("The scale of text padding.").defaultValue(0.0D).min(0.0D).sliderMax(10.0D).build());


    private final Setting<SettingColor> backgroundColor = sgGeneral.add(new ColorSetting.Builder().name("background-color").defaultValue(new Color(50, 50, 50, 255)).build());
    private final Setting<SettingColor> textColor = sgGeneral.add(new ColorSetting.Builder().name("text-color").defaultValue(new Color(255, 255, 255, 255)).build());
    private final Setting<SettingColor> notificationColor = sgGeneral.add(new ColorSetting.Builder().name("error-color").defaultValue(new Color(255, 237, 0, 255)).build());
    private final Setting<SettingColor> offColor = sgGeneral.add(new ColorSetting.Builder().name("disabled-color").defaultValue(new Color(255, 0, 0, 255)).build());
    private final Setting<SettingColor> onColor = sgGeneral.add(new ColorSetting.Builder().name("enabled-color").defaultValue(new Color(0, 255, 0, 255)).build());

    public static final HudElementInfo<ToastNotifications> INFO = new HudElementInfo<>(BedTrap.BEDTRAP_HUD, "toast-notifications", "Displays toast notifications on hud.", ToastNotifications::new);

    public ToastNotifications() {
        super(INFO);
    }

    public static ToastNotifications getInstance() {
        return new ToastNotifications();
    }

    public static void addToast(String text, Color color) {
        addToggled(text, color);
        MinecraftClient mc = MinecraftClient.getInstance();
        if (getInstance().sound.get())
            mc.player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1F, 1F);
    }

    public static void addToast(String text) {
        if (toasts.isEmpty()) timer1 = 0;
        toasts.add(new TosterNotifications(text, null));
        MinecraftClient mc = MinecraftClient.getInstance();
        if (getInstance().sound.get())
            mc.player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1F, 1F);
    }

    public static void addToggled(String text, Color color) {
        if (toasts.isEmpty()) timer1 = 0;
        toasts.add(new TosterNotifications(text, color));
    }

    public static void addToggled(Module module, String mes) {
        String nameToTitle = Utils.nameToTitle(module.name);

        toasts.removeIf(toasts -> toasts.text.endsWith("OFF"));
        toasts.removeIf(toasts -> toasts.text.endsWith("ON"));

        if (mes.contains("F")) {
            addToggled(nameToTitle + " OFF", staticOffColor);
        } else {
            addToggled(nameToTitle + " ON", staticOnColor);
        }
        if (toasts.isEmpty()) timer1 = 0;
    }

    private static double moveX(double start, double end) {
        double speed = (end - start) * 0.1;

        if (speed > 0) {
            speed = Math.max(0.1, speed);
            speed = Math.min(end - start, speed);
        } else if (speed < 0) {
            speed = Math.min(-0.1, speed);
            speed = Math.max(end - start, speed);
        }
        return start + speed;
    }

    private static SettingColor staticNotificationColor = new SettingColor(new SettingColor(255, 237, 0, 255));
    private static SettingColor staticOnColor = new SettingColor(new SettingColor(0, 255, 0, 255));
    private static SettingColor staticOffColor = new SettingColor(new SettingColor(255, 0, 0, 255));

    @Override
    public void tick(HudRenderer renderer) {
        updator();
        staticNotificationColor = notificationColor.get();
        staticOnColor = onColor.get();
        staticOffColor = offColor.get();

        if (mc.player == null || isInEditor()) {
            setSize(renderer.textWidth("toast messages", true, scale.get()), renderer.textHeight(true, scale.get()));
            return;
        }

        double width = 0;
        double height = 0;

        width = Math.max(width, renderer.textWidth(renderingMes.isEmpty() ? " " : renderingMes, true, scale.get()));
        height += renderer.textHeight(true, scale.get());

        box.setSize(width, height);
    }


    @Override
    public void render(HudRenderer renderer) {
        try {
            updator();

            int w = this.getWidth();
            int h = this.getHeight();

            double x = this.getX() - 0.5;
            double y = this.getY() - 0.5;

            if (isInEditor()) {
                renderer.text("toast-messages", x, y, textColor.get(), true, scale.get());
                renderer.quad(x, y, w, h, new Color(backgroundColor.get().r, backgroundColor.get().g, backgroundColor.get().b, 50));
                Renderer2D.COLOR.render(null);
                return;
            }
            int i = 0;

            if (toasts.isEmpty()) {
                renderer.text("", x + box.alignX(renderer.textWidth(""), this.getWidth(), Alignment.Auto), y, textColor.get(), true, scale.get());
            } else {
                for (TosterNotifications mes : toasts) {

                    double textWidth = renderer.textWidth(mes.text, true, scale.get());
                    double textHeight = renderer.textHeight(true, scale.get());

                    double dynamicPaddingX = paddingScale.get() * scale.get();
                    double dynamicPaddingY = 2 * scale.get();

                    double boxWidth = textWidth + (dynamicPaddingX * 2);
                    double boxHeight = textHeight + (dynamicPaddingY * 2);

                    double startY = y;
                    double end = invertedX.get() ? this.getX() + (boxWidth + 4) : this.getX() + this.getWidth() - (boxWidth + 4);
                    double start = invertedX.get() ? this.getX() - (boxWidth + 4) : end + (boxWidth + 4);


                    if (mes.pos < (boxWidth + 4))
                        mes.pos = moveX(mes.pos, (boxWidth + 4) + 1);

                    if (mes.pos > (boxWidth + 4))
                        mes.pos = (boxWidth + 4);

                    if (i == 0 && timer1 >= removeDelay.get() * 140 - 100) {
                        mes.pos = moveX(mes.pos,-((boxWidth + 4) + 1));
                    }

                    start = invertedX.get() ? start + mes.pos + 6 : start - mes.pos;
                    renderingMes = mes.text;
                    /*
                    start - X коррдината рендеринга тоста (X)
                    X---------|
                    X         |
                    X---------|

                    dynamicPending - пробел между текстом и гранью бокса (X)
                    |X.Текст.X|
                     */

                    double boxCenterX = start + (boxWidth / 2);
                    double boxCenterY = startY + (boxHeight / 2);
                    double textStartX = boxCenterX - (textWidth / 2);
                    double textStartY = boxCenterY - (textHeight / 2);

                    renderer.quad(start - 2, startY, boxWidth + 4, boxHeight, mes.color); //Оконтовка бокса
                    renderer.quad(start, startY, boxWidth, boxHeight, backgroundColor.get()); // внутреняя часть
                    renderer.text(mes.text, textStartX, textStartY, textColor.get(), true, scale.get()); // текст

                    y += invertedY.get() ? -(boxHeight + 4) : boxHeight + 4;
                    if (i >= 0) y += invertedY.get() ? -1 : 1;
                    i++;
                }
            }
        } catch (ConcurrentModificationException e) {
            e.fillInStackTrace();
        }
    }


    private void updator() {
        if (toasts.size() > 7) toasts.removeFirst();
        if (toasts.isEmpty()) return;
        if (timer1 >= removeDelay.get() * 140) {
            toasts.removeFirst();
            timer1 = 0;
        } else timer1++;
    }

    public static class TosterNotifications {
        public final String text;
        public final Color color;
        public double pos = -1;

        public TosterNotifications(String text, Color color) {
            if (color == null) color = staticNotificationColor;
            this.text = text;
            this.color = color;
        }
    }
}