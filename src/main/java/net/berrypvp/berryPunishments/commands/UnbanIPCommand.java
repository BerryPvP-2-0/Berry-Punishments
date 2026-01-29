package net.berrypvp.berryPunishments.commands;

import net.berrypvp.berryPunishments.BerryPunishments;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class UnbanIPCommand implements CommandExecutor {
    private final BerryPunishments plugin;

    public UnbanIPCommand(BerryPunishments plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("berry.command.punishments.unbanip")) {
            sender.sendMessage(this.plugin.getConfigManager().getMessages().getString("no-permission", "§cYou do not have permission to execute this command."));
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(this.plugin.getConfigManager().getMessages().getString("unbanip-usage", "§cUsage: /unbanip <player>").replace("&", "§"));
            return true;
        }

        String ip = args[0];
        if (this.plugin.getBanManager().unbanIP(ip)) {
            this.plugin.getConfigManager().reloadConfigs();
            String message = this.plugin.getConfigManager().getMessages().getString("ip-unbanned").replace("&", "§");
            sender.sendMessage(message);
        } else {
            String message = this.plugin.getConfigManager().getMessages().getString("ip-not-banned").replace("&", "§");
            sender.sendMessage(message);
        }

        return true;
    }
}
