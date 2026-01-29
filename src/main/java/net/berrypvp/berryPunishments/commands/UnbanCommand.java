package net.berrypvp.berryPunishments.commands;

import net.berrypvp.berryPunishments.BerryPunishments;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class UnbanCommand implements CommandExecutor {
    private final BerryPunishments plugin;

    public UnbanCommand(BerryPunishments plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("berry.command.punishments.unban")) {
            sender.sendMessage(this.plugin.getConfigManager().getMessages().getString("no-permission", "§cYou do not have permission to execute this command."));
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(this.plugin.getConfigManager().getMessages().getString("unban-usage", "§cUsage: /unban <player>").replace("&", "§"));
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (this.plugin.getBanManager().unbanPlayer(target.getUniqueId())) {
            this.plugin.getConfigManager().reloadConfigs();
            String message = this.plugin.getConfigManager().getMessages().getString("player-unbanned").replace("&", "§").replace("%player%", target.getName());
            sender.sendMessage(message);
        } else {
            String message = this.plugin.getConfigManager().getMessages().getString("player-not-banned").replace("&", "§").replace("%player%", target.getName());
            sender.sendMessage(message);
        }

        return true;
    }
}
