package net.berrypvp.berryPunishments.commands;

import net.berrypvp.berryPunishments.BerryPunishments;
import net.berrypvp.berryPunishments.utils.BanData;
import net.berrypvp.berryPunishments.utils.MuteData;
import net.berrypvp.berryPunishments.utils.TimeUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SearchIDCommand implements CommandExecutor {
    private final BerryPunishments plugin;

    public SearchIDCommand(BerryPunishments plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("berry.command.punishments.searchid")) {
            sender.sendMessage(this.plugin.getConfigManager().getMessages().getString("no-permission", "§cYou do not have permission to execute this command."));
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(this.plugin.getConfigManager().getMessages().getString("searchid-usage", "§cUsage: /searchid <banid|muteid>").replace("&", "§"));
            return true;
        }

        String id = args[0];
        if (!id.startsWith("#")) {
            id = "#" + id;
        }

        BanData banData = this.plugin.getBanManager().getBanById(id);
        if (banData != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss");
            String bannedDate = sdf.format(new Date(banData.getBannedAt()));
            String message = this.plugin.getConfigManager().getMessages().getString("searchid-ban-info").replace("&", "§")
                    .replace("%player%", banData.getPlayerName())
                    .replace("%reason%", banData.getReason())
                    .replace("%bannedby%", banData.getBannedByName())
                    .replace("%date%", bannedDate)
                    .replace("%duration%", TimeUtil.formatTime(banData.getDuration()))
                    .replace("%timeleft%", TimeUtil.formatTime(banData.getTimeLeft()))
                    .replace("%active%", banData.isExpired() ? "No" : "Yes")
                    .replace("%banid%", banData.getBanId();
            sender.sendMessage(message);
            return true;
        }

        MuteData muteData = this.plugin.getMuteManager().getMuteById(id);
        if (muteData != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss");
            String muteDate = sdf.format(new Date(muteData.getMutedAt()));
            String message = this.plugin.getConfigManager().getMessages().getString("searchid-ban-info").replace("&", "§")
                    .replace("%player%", muteData.getPlayerName())
                    .replace("%reason%", muteData.getReason())
                    .replace("%mutedby%", muteData.getMutedByName())
                    .replace("%date%", muteDate)
                    .replace("%duration%", TimeUtil.formatTime(muteData.getDuration()))
                    .replace("%timeleft%", TimeUtil.formatTime(muteData.getTimeLeft()))
                    .replace("%active%", muteData.isExpired() ? "No" : "Yes")
                    .replace("%muteid%", muteData.getMuteId();
            sender.sendMessage(message);
            return true;
        }

        sender.sendMessage(this.plugin.getConfigManager().getMessages().getString("id-not-found", "§cNo ban or mute found with ID %id%").replace("&", "§").replace("%id%", id));
        return true;
    }
}
