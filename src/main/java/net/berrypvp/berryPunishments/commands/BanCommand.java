package net.berrypvp.berryPunishments.commands;

import net.berrypvp.berryPunishments.BerryPunishments;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static net.berrypvp.berryPunishments.utils.TimeUtil.formatTime;
import static net.berrypvp.berryPunishments.utils.TimeUtil.parseDuration;

public class BanCommand implements CommandExecutor, TabCompleter {
    private final BerryPunishments plugin;

    public BanCommand(BerryPunishments plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("berry.command.punishments.ban")) {
            sender.sendMessage(this.plugin.getConfigManager().getMessages().getString("no-permission", "§cYou do not have permission to execute this command."));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(this.plugin.getConfigManager().getMessages().getString("ban-usage", "§cUsage: /ban <player> <reason>").replace("&", "§"));
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (!target.hasPlayedBefore() && !target.isOnline()) {
            sender.sendMessage(plugin.getConfigManager().getMessages().getString("player-not-found").replace("&", "§").replace("%player%", args[0]));
            return true;
        }

        String reason;
        long duration;

        long parsed = parseDuration(args[1]);
        if (parsed != Long.MIN_VALUE) {
            duration = parsed;
            if (args.length < 3) {
                sender.sendMessage("§cPlease provide a reason for the ban.");
                return true;
            }
            reason = args[2];
        } else {
            duration = -1;
            reason = args[1];
        }

        Player banner = sender instanceof Player ? (Player) sender : null;

        plugin.getBanManager().banPlayer(
                target.getUniqueId(),
                target.getName(),
                banner != null ? banner.getUniqueId() : null,
                banner != null ? banner.getName() : "Console",
                reason,
                duration,
                false,
                null
        );

        sender.sendMessage("§aBanned §f" + target.getName() + " §afor §e" +
                (duration == -1 ? "Permanent" : formatTime(duration) +
                " §aReason: §f" + reason));

        return true;
    }

    @Override
    public java.util.List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
        }
        return java.util.Collections.emptyList();
    }
}
