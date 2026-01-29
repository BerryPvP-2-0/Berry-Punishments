package net.berrypvp.berryPunishments.commands;

import net.berrypvp.berryPunishments.BerryPunishments;
import net.berrypvp.berryPunishments.utils.BanData;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class AltsCommand implements CommandExecutor, TabCompleter {

    private final BerryPunishments plugin;

    public AltsCommand(BerryPunishments plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("berry.command.punishments.alts")) {
            sender.sendMessage(msg("no-permission"));
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(msg("alts-usage"));
            return true;
        }

        String targetName = args[0];
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);

        if (!target.hasPlayedBefore() && !target.isOnline()) {
            sender.sendMessage(msg("player-not-found").replace("%player%", targetName));
            return true;
        }

        sender.sendMessage(msg("alts-scanning").replace("%player%", target.getName()));

        scanAlts(sender, target);

        return true;
    }

    /** Scan alts and output only to the sender */
    public void scanAlts(CommandSender sender, OfflinePlayer target) {
        Set<String> foundAlts = new HashSet<>();

        // Collect all IPs from banned accounts
        Set<String> ips = plugin.getBanManager().getAllActiveBans().stream()
                .filter(BanData::isIpBan)
                .filter(bd -> bd.getPlayerUUID() != null && bd.getPlayerUUID().equals(target.getUniqueId()))
                .map(BanData::getIp)
                .collect(Collectors.toSet());

        // Add current IP if online
        if (target.isOnline() && target.getPlayer().getAddress() != null) {
            ips.add(target.getPlayer().getAddress().getAddress().getHostAddress());
        }

        // Find all accounts using these IPs
        for (String ip : ips) {
            // Add banned accounts
            plugin.getBanManager().getAllActiveBans().forEach(bd -> {
                if (bd.isIpBan() && bd.getIp() != null && bd.getIp().equals(ip)) {
                    foundAlts.add(bd.getPlayerName());
                }
            });
            // Add online players not banned
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getAddress() != null && p.getAddress().getAddress().getHostAddress().equals(ip)) {
                    foundAlts.add(p.getName());
                }
            }
        }

        if (foundAlts.isEmpty()) {
            sender.sendMessage(msg("alts-no-alts").replace("%player%", target.getName()));
            return;
        }

        // Build colored output
        StringBuilder output = new StringBuilder();
        for (String name : foundAlts) {
            OfflinePlayer op = Bukkit.getOfflinePlayer(name);
            if (plugin.getBanManager().isBanned(op.getUniqueId())) {
                output.append("§c").append(name).append(" "); // Red = banned
            } else if (op.isOnline()) {
                output.append("§a").append(name).append(" "); // Green = online
            } else {
                output.append("§7").append(name).append(" "); // Gray = offline
            }
        }

        sender.sendMessage(output.toString().trim());
    }

    @Override
    public java.util.List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
        }
        return Collections.emptyList();
    }

    private String msg(String path) {
        return plugin.getConfigManager().getMessages()
                .getString(path, "§cMessage missing")
                .replace("&", "§");
    }
}
