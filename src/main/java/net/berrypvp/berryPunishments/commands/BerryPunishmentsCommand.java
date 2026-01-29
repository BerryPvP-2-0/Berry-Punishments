package net.berrypvp.berryPunishments.commands;

import net.berrypvp.berryPunishments.BerryPunishments;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class BerryPunishmentsCommand implements CommandExecutor {
    private final BerryPunishments plugin;

    public BerryPunishmentsCommand(BerryPunishments plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("berry.command.punishments.berrypunishments")) {
            sender.sendMessage(this.plugin.getConfigManager().getMessages().getString("no-permission", "§cYou do not have permission to execute this command."));
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("berry.command.punishments.berrypunishments.reload")) {
                sender.sendMessage(this.plugin.getConfigManager().getMessages().getString("no-permission", "§cYou do not have permission to execute this command."));
                return true;
            }
            this.plugin.getConfigManager().reloadConfigs();
            sender.sendMessage(this.plugin.getConfigManager().getMessages().getString("config-reloaded", "§aBerryPunishments configuration reloaded successfully."));
            return true;
        }

        if (!sender.hasPermission("berry.command.punishments.berrypunishments.help")) {
            sender.sendMessage(this.plugin.getConfigManager().getMessages().getString("no-permission", "§cYou do not have permission to execute this command."));
            return true;
        }
        sender.sendMessage("§6=== §eBerryPunishments §6===");
        sender.sendMessage("§e/berrypunishments reload §7- §eReload the plugin configuration.");
        sender.sendMessage("§e/searchid <id> §7- §eSearch for a ban/mute by ID.");
        sender.sendMessage("§e/unban <player> §7- §eUnban a player.");
        sender.sendMessage("§e/unbanip <ip> §7- §eUnban an IP address.");
        sender.sendMessage("§e/unmute <player> §7- §eUnmute a player.");
        return true;
    }
}