package net.berrypvp.berryPunishments.commands;

import net.berrypvp.berryPunishments.BerryPunishments;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class MuteCommand implements CommandExecutor, TabCompleter {

    private final BerryPunishments plugin;

    public MuteCommand(BerryPunishments plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("berry.command.punishments.mute")) {
            sender.sendMessage(msg("no-permission"));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage("§cUsage: /mute <player> [duration] <reason>");
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (!target.hasPlayedBefore() && !target.isOnline()) {
            sender.sendMessage(msg("player-not-found").replace("%player%", args[0]));
            return true;
        }

        long duration;
        String reason;

        long parsed = parseDuration(args[1]);
        if (parsed != Long.MIN_VALUE) {
            duration = parsed;
            if (args.length < 3) {
                sender.sendMessage("§cYou must specify a reason.");
                return true;
            }
            reason = joinArgs(args, 2);
        } else {
            duration = -1; // permanent mute
            reason = joinArgs(args, 1);
        }

        Player muter = sender instanceof Player ? (Player) sender : null;

        plugin.getMuteManager().mutePlayer(
                target.getUniqueId(),
                target.getName(),
                muter != null ? muter.getUniqueId() : null,
                muter != null ? muter.getName() : "Console",
                reason,
                duration
        );

        sender.sendMessage("§aMuted §f" + target.getName() + " §afor §e" +
                (duration == -1 ? "Permanent" : formatDuration(duration)) +
                " §aReason: §f" + reason);

        return true;
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
        if (args.length == 1) {
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
        }
        return java.util.Collections.emptyList(); // no tab suggestions for freeform reason
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
