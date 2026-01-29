package net.berrypvp.berryPunishments.commands;

import net.berrypvp.berryPunishments.BerryPunishments;
import net.berrypvp.berryPunishments.utils.BanData;
import net.berrypvp.berryPunishments.utils.MuteData;
import net.berrypvp.berryPunishments.utils.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HistoryCommand implements CommandExecutor {

    private final BerryPunishments plugin;

    public HistoryCommand(BerryPunishments plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("berry.command.punishments.history")) {
            sender.sendMessage(color(msg("no-permission")));
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(color(msg("history-usage")));
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

        List<BanData> bans = plugin.getBanManager().getBanHistory(target.getUniqueId());
        List<MuteData> mutes = plugin.getMuteManager().getMuteHistory(target.getUniqueId());

        List<HistoryEntry> history = new ArrayList<>();

        if (bans != null) {
            for (BanData ban : bans) {
                history.add(new HistoryEntry(
                        ban.getBanId(),
                        ban.getReason(),
                        ban.getBannedByName(),
                        ban.getBannedAt(),
                        ban.getDuration(),
                        !ban.isExpired()
                ));
            }
        }

        if (mutes != null) {
            for (MuteData mute : mutes) {
                history.add(new HistoryEntry(
                        mute.getMuteId(),
                        mute.getReason(),
                        mute.getMutedByName(),
                        mute.getMutedAt(),
                        mute.getDuration(),
                        !mute.isExpired()
                ));
            }
        }

        if (history.isEmpty()) {
            sender.sendMessage(color(
                    msg("no-ban-history").replace("%player%", target.getName())
            ));
            return true;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss");

        sender.sendMessage(color(
                msg("history-header")
                        .replace("%player%", target.getName())
                        .replace("%count%", String.valueOf(history.size()))
        ));

        for (HistoryEntry entry : history) {
            String date = sdf.format(new Date(entry.date));
            String duration = entry.duration <= 0
                    ? "Permanent"
                    : TimeUtil.formatTime(entry.duration);

            sender.sendMessage(color(
                    msg("history-entry")
                            .replace("%id%", entry.id)
                            .replace("%reason%", entry.reason)
                            .replace("%punishedby%", entry.punishedBy)
                            .replace("%date%", date)
                            .replace("%duration%", duration)
                            .replace("%active%", entry.active ? "&cYes" : "&aNo")
            ));
        }

        return true;
    }

    private String msg(String path) {
        return plugin.getConfigManager().getMessages().getString(path);
    }

    private String color(String s) {
        return s == null ? "" : s.replace("&", "§");
    }

    private static class HistoryEntry {
        private final String id;
        private final String reason;
        private final String punishedBy;
        private final long date;
        private final long duration;
        private final boolean active;

        private HistoryEntry(String id, String reason, String punishedBy, long date, long duration, boolean active) {
            this.id = id;
            this.reason = reason;
            this.punishedBy = punishedBy;
            this.date = date;
            this.duration = duration;
            this.active = active;
        }
    }
}