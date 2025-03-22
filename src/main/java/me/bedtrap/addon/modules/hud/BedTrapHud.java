package me.bedtrap.addon.modules.hud;

import me.bedtrap.addon.BedTrap;
import meteordevelopment.meteorclient.renderer.GL;
import meteordevelopment.meteorclient.renderer.Renderer2D;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.utils.render.color.RainbowColor;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.util.Identifier;

public class BedTrapHud extends HudElement {
    private static final RainbowColor RAINBOW = new RainbowColor();
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    public final Setting<Boolean> chroma = sgGeneral.add(new BoolSetting.Builder().name("chroma").description("Chroma logo animation.").defaultValue(false).build());
    private final Setting<Double> chromaSpeed = sgGeneral.add(new DoubleSetting.Builder().name("factor").defaultValue(0.10).min(0.01).sliderMax(5).decimalPlaces(4).visible(chroma::get).build());
    private final Setting<SettingColor> color = sgGeneral.add(new ColorSetting.Builder().name("color").defaultValue(new SettingColor(255, 255, 255)).visible(() -> !chroma.get()).build());
    private final Setting<BedLogo> logo = sgGeneral.add(new EnumSetting.Builder<BedLogo>().name("logo").defaultValue(BedLogo.Text).build());
    private final Setting<Double> scale = sgGeneral.add(new DoubleSetting.Builder().name("scale").description("The scale of the logo.").defaultValue(3.5).min(0.1).sliderRange(0.1, 5).build());
    private Identifier image = Identifier.of("bedtrap", "text.png");

    public BedTrapHud() {
        super(INFO);
    }

    @Override
    public void tick(HudRenderer renderer) {
        box.setSize(64 * scale.get(), 64 * scale.get());
    }

    @Override
    public void render(HudRenderer renderer) {
        switch (logo.get()) {
            case Text -> image = Identifier.of("bedtrap", "text.png");
            case OldLogo -> image = Identifier.of("bedtrap", "bt.png");
            case NewLogo -> image = Identifier.of("bedtrap", "icon.png");
        }

        GL.bindTexture(image);
        Renderer2D.TEXTURE.begin();
        if (chroma.get()) {
            RAINBOW.setSpeed(chromaSpeed.get() / 100);
            Renderer2D.TEXTURE.texQuad(this.x, this.y, this.getWidth(), this.getHeight(), RAINBOW.getNext(renderer.delta));
        } else {
            Renderer2D.TEXTURE.texQuad(this.x, this.y, this.getWidth(), this.getHeight(), color.get());
        }
        Renderer2D.TEXTURE.render(null);
    }

    public enum BedLogo {
        Text, OldLogo, NewLogo
    }

    public static final HudElementInfo<BedTrapHud> INFO = new HudElementInfo<>(BedTrap.BEDTRAP_HUD, "bedtrap-logo", "Nice icon for you HUD!", BedTrapHud::new);

}
