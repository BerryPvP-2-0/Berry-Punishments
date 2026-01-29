package net.berrypvp.berryPunishments.commands;

import net.berrypvp.berryPunishments.BerryPunishments;
import net.berrypvp.berryPunishments.utils.BanData;
import net.berrypvp.berryPunishments.utils.BanScreen;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.regex.Pattern;

public class BanIPCommand implements CommandExecutor, TabCompleter {

    private final BerryPunishments plugin;
    private final Pattern ipPattern = Pattern.compile(
            "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)(\\.|$)){4}$"
    );

    public BanIPCommand(BerryPunishments plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("berry.command.punishments.banip")) {
            sender.sendMessage(msg("no-permission"));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage("§cUsage: /banip <ip> [duration] <reason>");
            return true;
        }

        String ip = args[0];
        if (!isValidIP(ip)) {
            sender.sendMessage("§cInvalid IP address!");
            return true;
        }

        long duration;
        String reason;

        // If second argument is a duration
        long parsed = parseDuration(args[1]);
        if (parsed != Long.MIN_VALUE) {
            duration = parsed;
            if (args.length < 3) {
                sender.sendMessage("§cYou must specify a reason.");
                return true;
            }
            reason = joinArgs(args, 2);
        } else {
            duration = -1;
            reason = joinArgs(args, 1);
        }

        Player banner = sender instanceof Player ? (Player) sender : null;

        // Ban by IP
        String banId = plugin.getBanManager().banPlayer(
                null, // No UUID
                ip,
                banner != null ? banner.getUniqueId() : null,
                banner != null ? banner.getName() : "Console",
                reason,
                duration,
                true, // IP ban
                ip
        );

        // Kick all online players with this IP
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online.getAddress() != null && online.getAddress().getAddress().getHostAddress().equals(ip)) {
                online.kickPlayer(BanScreen.createBanScreen(
                        plugin.getBanManager().getBanById(banId)
                ));
            }
        }

        sender.sendMessage("§aIP Banned §f" + ip + " §afor §e" +
                (duration == -1 ? "Permanent" : formatDuration(duration)) +
                " §aReason: §f" + reason);

        return true;
    }

    private boolean isValidIP(String ip) {
        return ipPattern.matcher(ip).matches();
    }

    private String joinArgs(String[] args, int start) {
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < args.length; i++) {
            sb.append(args[i]);
            if (i != args.length - 1) sb.append(" ");
        }
        return sb.toString();
    }

    @Override
    public java.util.List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return java.util.Collections.emptyList(); // no suggestions
    }

    private long parseDuration(String input) {
        if (input.equalsIgnoreCase("-1")) return -1;

        try {
            long value = Long.parseLong(input.substring(0, input.length() - 1));
            char unit = input.charAt(input.length() - 1);

            return switch (unit) {
                case 'm' -> value * 60_000;
                case 'h' -> value * 3_600_000;
                case 'd' -> value * 86_400_000;
                default -> Long.MIN_VALUE;
            };
        } catch (Exception e) {
            return Long.MIN_VALUE;
        }
    }

    private String formatDuration(long ms) {
        long days = ms / 86_400_000;
        if (days > 0) return days + "d";
        long hours = ms / 3_600_000;
        if (hours > 0) return hours + "h";
        long minutes = ms / 60_000;
        return minutes + "m";
    }

    private String msg(String path) {
        return plugin.getConfigManager().getMessages()
                .getString(path, "§cMessage missing")
                .replace("&", "§");
    }
}
