package net.berrypvp.berryPunishments.utils;

import net.berrypvp.berryPunishments.BerryPunishments;
import org.bukkit.configuration.file.FileConfiguration;

public class BanScreen {
    public static String createBanScreen(BanData banData) {
        String timeLeft;
        BerryPunishments plugin = BerryPunishments.getInstance();
        FileConfiguration config = plugin.getConfigManager().getBanscreen();
        String title = color(config.getString("title", "&7Connection Lost"));
        String line1 = color(config.getString("line1", "&cYou have been banned for {reason}"));
        String line2 = color(config.getString("line2", "&7Time left: &f{time}"));
        String line3 = color(config.getString("line3", "&7Ban ID: &f{banid}"));
        String line4 = color(config.getString("line4", "&7You may be able to appeal this ban on"));
        String line5 = color(config.getString("line5", "&f{discord}"));
        String discord = plugin.getConfigManager().getConfig().getString("discord-server", "discord.example.net");
        String reason = banData.getReason();
        String banId = banData.getBanId();
        if (banData.getDuration() <= 0L) {
            timeLeft = "Permanent";
        } else {
            timeLeft = TimeUtil.formatTime(banData.getTimeLeft());
        }
        line1 = line1.replace("%reason%", reason);
        line2 = line2.replace("%time%", timeLeft);
        line3 = line3.replace("%banid%", banId);
        line5 = line5.replace("%discord%", discord);
        return title + "\n\n" + title + "\n\n" + line1 + "\n\n" + line2 + "\n\n" + line3 + "\n" + line4;
    }

    private static String color(String text) {
        if (text == null)
            return "";
        return text.replace("&", "§");
    }
}
