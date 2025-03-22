package me.bedtrap.addon.modules.info;

import me.bedtrap.addon.BedTrap;
import me.bedtrap.addon.util.other.TextUtils;
import meteordevelopment.meteorclient.events.game.SendMessageEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.config.Config;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;

public class ChatEncrypt extends Module {
    public static String[] encryptors = {"[BT] "};
    private final SettingGroup sgDefault = settings.getDefaultGroup();
    private final Setting<Mode> mode = sgDefault.add(new EnumSetting.Builder<Mode>().name("mode").description("The way to use Chat Encryptor.").defaultValue(Mode.Both).build());
    private final Setting<TextUtils.Color> nameColor = sgDefault.add(new EnumSetting.Builder<TextUtils.Color>().name("name-color").description("Sets the color of names in decrypted messages.").defaultValue(TextUtils.Color.Red).build());
    private final Setting<TextUtils.Color> messageColor = sgDefault.add(new EnumSetting.Builder<TextUtils.Color>().name("message-color").description("Sets the color of message in decrypted messages.").defaultValue(TextUtils.Color.Gray).build());
    private final Setting<Boolean> proximaDecrypt = sgDefault.add(new BoolSetting.Builder().name("proxima-decrypt").description("Decrypts messages from Proxima users.").defaultValue(false).build());

    public ChatEncrypt() {
        super(BedTrap.Info, "chat-encrypt", "Encrypts and decrypts chat messages.");
    }

    @EventHandler
    public void onReceiveMessage(PacketEvent.Receive event) {
        if (!(event.packet instanceof GameMessageS2CPacket) || mode.get() == Mode.Encrypt) return;
        String message = ((GameMessageS2CPacket) event.packet).content().getString();

        String way;
        TextUtils.Decryptor decryptor;

        if (message.contains(encryptors[0])) {
            way = encryptors[0];
            decryptor = TextUtils.Decryptor.BedTrap;
        } else return;

        String name = getSender(message);

        message = message.substring(message.indexOf(way) + 5);
        if (message.isEmpty()) return;

        message = TextUtils.decrypt(message, decryptor);
        ChatUtils.sendMsg(Text.of(TextUtils.getColor(nameColor.get()) + name + Formatting.GRAY + ": " + TextUtils.getColor(messageColor.get()) + message));
        event.cancel();
    }

    private String getSender(String message) {
        ArrayList<PlayerListEntry> entry = new ArrayList<>(mc.getNetworkHandler().getPlayerList());
        for (PlayerListEntry player : entry) {
            if (message.contains(player.getProfile().getName())) return player.getProfile().getName();
        }

        return "null";
    }

    @EventHandler
    public void onSendMessage(SendMessageEvent event) {
        if (mode.get() == Mode.Decrypt) return;

        if (!event.message.startsWith(String.valueOf(Config.get().prefix)) && !event.message.startsWith("/")) {
            event.message = "[BT] " + TextUtils.encrypt(event.message.toLowerCase());
        }
    }

    public enum Mode {
        Encrypt, Decrypt, Both
    }
}
