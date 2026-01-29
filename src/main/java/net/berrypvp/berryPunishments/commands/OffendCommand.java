package net.berrypvp.berryPunishments.commands;

import net.berrypvp.berryPunishments.BerryPunishments;
import net.berrypvp.berryPunishments.managers.BanManager;
import net.berrypvp.berryPunishments.managers.MuteManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class OffendCommand implements CommandExecutor, TabCompleter {

    private final BerryPunishments plugin;

    public OffendCommand(BerryPunishments plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("berry.command.punishments.offend")) {
            sender.sendMessage(msg("no-permission"));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage("&cUsage: /offend <player> <reason>");
            return true;
        }

        String targetName = args[0];
        String reasonKey = args[1];

        OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
        if (!target.hasPlayedBefore() && !target.isOnline()) {
            sender.sendMessage(msg("player-not-found").replace("%player%", targetName));
            return true;
        }

        long banDuration = getBanDuration(reasonKey);
        long muteDuration = getMuteDuration(reasonKey);

        BanManager banManager = plugin.getBanManager();
        MuteManager muteManager = plugin.getMuteManager();

        String bannedId = null;
        String mutedId = null;

        if (banDuration >= 0) {
            bannedId = banManager.banPlayer(target.getUniqueId(), target.getName(),
                    (sender instanceof Player p ? p.getUniqueId() : null),
                    sender.getName(), reasonKey, banDuration, false, null);
        }

        if (muteDuration >= 0) {
            mutedId = muteManager.mutePlayer(target.getUniqueId(), target.getName(),
                    (sender instanceof Player p ? p.getUniqueId() : null),
                    sender.getName(), reasonKey, muteDuration);
        }

        sender.sendMessage("§eOffence applied to §f" + target.getName() + "§e:");
        if (bannedId != null) sender.sendMessage(" §cBanned§7 (ID: " + bannedId + ")");
        if (mutedId != null) sender.sendMessage(" §6Muted§7 (ID: " + mutedId + ")");

        final OfflinePlayer fTarget = target;
        final String fReason = reasonKey;
        final String fBannedId = bannedId;
        final String fMutedId = mutedId;

        Bukkit.getOnlinePlayers().forEach(p -> {
            if (p.hasPermission("berry.command.punishments.offend.seelogs")) {
                StringBuilder sb = new StringBuilder("§c[Offence] §f" + fTarget.getName() + " §7committed an offence: §f" + fReason);
                if (fBannedId != null) sb.append(" §cBanned");
                if (fMutedId != null) sb.append(" §6Muted");
                p.sendMessage(sb.toString());
            }
        });


        return true;
    }

    private long getBanDuration(String reason) {
        ConfigurationSection section = plugin.getConfigManager().getBanReasons();
        if (section == null || !section.contains(reason)) return -1;
        List<String> durations = section.getStringList(reason + ".durations");
        if (durations.isEmpty()) return -1;
        return plugin.getConfigManager().parseDuration(durations.get(0));
    }

    private long getMuteDuration(String reason) {
        ConfigurationSection section = plugin.getConfigManager().getMuteReasons();
        if (section == null || !section.contains(reason)) return -1;
        List<String> durations = section.getStringList(reason + ".durations");
        if (durations.isEmpty()) return -1;
        return plugin.getConfigManager().parseDuration(durations.get(0));
    }

    private String msg(String path) {
        return plugin.getConfigManager().getMessages()
                .getString(path, "§cMessage missing")
                .replace("&", "§");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            final String typed = args[0];
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(n -> n.toLowerCase().startsWith(typed.toLowerCase()))
                    .collect(Collectors.toList());
        } else if (args.length == 2) {
            final String typedReason = args[1];
            Set<String> reasons = new HashSet<>();
            ConfigurationSection banReasons = plugin.getConfigManager().getBanReasons();
            ConfigurationSection muteReasons = plugin.getConfigManager().getMuteReasons();

            if (banReasons != null) reasons.addAll(banReasons.getKeys(false));
            if (muteReasons != null) reasons.addAll(muteReasons.getKeys(false));

            return reasons.stream()
                    .filter(r -> r.toLowerCase().startsWith(typedReason.toLowerCase()))
                    .sorted()
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}
