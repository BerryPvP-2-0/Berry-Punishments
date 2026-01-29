package net.berrypvp.berryPunishments.managers;

import net.berrypvp.berryPunishments.BerryPunishments;
import net.berrypvp.berryPunishments.utils.BanData;
import net.berrypvp.berryPunishments.utils.BanScreen;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.stream.Collectors;

public class BanManager {
    private final BerryPunishments plugin;

    private final Map<UUID, BanData> activeBans;

    private final Map<String, BanData> activeBansByIP;

    private final Map<String, BanData> banHistory;

    public BanManager(BerryPunishments plugin) {
        this.plugin = plugin;
        this.activeBans = new HashMap<>();
        this.activeBansByIP = new HashMap<>();
        this.banHistory = new HashMap<>();
        loadBans();
        startBanCheckTask();
    }

    private void loadBans() {
        FileConfiguration banned = this.plugin.getConfigManager().getBanned();
        if (banned.contains("bans") && banned.isConfigurationSection("bans")) {
            ConfigurationSection bansSection = banned.getConfigurationSection("bans");
            if (bansSection != null)
                for (String banId : bansSection.getKeys(false)) {
                    ConfigurationSection banSection = bansSection.getConfigurationSection(banId);
                    if (banSection != null)
                        try {
                            UUID playerUUID = UUID.fromString(banSection.getString("uuid"));
                            String playerName = banSection.getString("name");
                            UUID bannedBy = UUID.fromString(banSection.getString("bannedBy"));
                            String bannedByName = banSection.getString("bannedByName");
                            String reason = banSection.getString("reason");
                            long bannedAt = banSection.getLong("bannedAt");
                            long duration = banSection.getLong("duration");
                            boolean ipBan = banSection.getBoolean("ipBan", false);
                            String ip = banSection.getString("ip");
                            BanData banData = new BanData(banId, playerUUID, playerName, bannedBy, bannedByName, reason, bannedAt, duration, ipBan, ip);
                            this.banHistory.put(banId, banData);
                            if (!banData.isExpired()) {
                                this.activeBans.put(playerUUID, banData);
                                if (ipBan && ip != null)
                                    this.activeBansByIP.put(ip, banData);
                            }
                        } catch (Exception e) {
                            this.plugin.getLogger().warning("Failed to load ban " + banId + ": " + e.getMessage());
                        }
                }
        }
    }

    public void saveBans() {
        FileConfiguration banned = this.plugin.getConfigManager().getBanned();
        banned.set("bans", null);
        for (BanData banData : this.banHistory.values()) {
            String path = "bans." + banData.getBanId();
            banned.set(path + ".uuid", banData.getPlayerUUID().toString());
            banned.set(path + ".name", banData.getPlayerName());
            banned.set(path + ".bannedBy", banData.getBannedBy().toString());
            banned.set(path + ".bannedByName", banData.getBannedByName());
            banned.set(path + ".reason", banData.getReason());
            banned.set(path + ".bannedAt", Long.valueOf(banData.getBannedAt()));
            banned.set(path + ".duration", Long.valueOf(banData.getDuration()));
            banned.set(path + ".ipBan", Boolean.valueOf(banData.isIpBan()));
            if (banData.isIpBan())
                for (String ip : this.activeBansByIP.keySet()) {
                    if (((BanData)this.activeBansByIP.get(ip)).getBanId().equals(banData.getBanId()))
                        banned.set(path + ".ip", ip);
                }
        }
        this.plugin.getConfigManager().saveBanned();
    }

    public String banPlayer(UUID playerUUID, String playerName, UUID bannedBy, String bannedByName, String reason, long duration, boolean ipBan, String ip) {
        String banId = generateBanId();
        BanData banData = new BanData(banId, playerUUID, playerName, bannedBy, bannedByName, reason, System.currentTimeMillis(), duration, ipBan, ip);
        this.activeBans.put(playerUUID, banData);
        this.banHistory.put(banId, banData);
        if (ipBan && ip != null)
            this.activeBansByIP.put(ip, banData);
        saveBans();
        Player player = Bukkit.getPlayer(playerUUID);
        if (player != null && player.isOnline())
            player.kickPlayer(BanScreen.createBanScreen(banData));
        return banId;
    }

    public boolean unbanPlayer(UUID playerUUID) {
        BanData banData = this.activeBans.remove(playerUUID);
        if (banData != null) {
            if (banData.isIpBan())
                this.activeBansByIP.values().removeIf(bd -> bd.getPlayerUUID().equals(playerUUID));
            FileConfiguration banned = this.plugin.getConfigManager().getBanned();
            banned.set("bans." + banData.getBanId(), null);
            this.plugin.getConfigManager().saveBanned();
            this.banHistory.remove(banData.getBanId());
            return true;
        }
        return false;
    }

    public boolean unbanIP(String ip) {
        BanData banData = this.activeBansByIP.remove(ip);
        if (banData != null) {
            this.activeBans.remove(banData.getPlayerUUID());
            FileConfiguration banned = this.plugin.getConfigManager().getBanned();
            banned.set("bans." + banData.getBanId(), null);
            this.plugin.getConfigManager().saveBanned();
            this.banHistory.remove(banData.getBanId());
            return true;
        }
        return false;
    }

    public boolean isBanned(UUID playerUUID) {
        BanData banData = this.activeBans.get(playerUUID);
        if (banData != null && banData.isExpired()) {
            this.activeBans.remove(playerUUID);
            saveBans();
            return false;
        }
        return (banData != null);
    }

    public boolean isIPBanned(String ip) {
        BanData banData = this.activeBansByIP.get(ip);
        if (banData != null && banData.isExpired()) {
            this.activeBansByIP.remove(ip);
            this.activeBans.remove(banData.getPlayerUUID());
            saveBans();
            return false;
        }
        return (banData != null);
    }

    public BanData getBan(UUID playerUUID) {
        return this.activeBans.get(playerUUID);
    }

    public BanData getBanByIP(String ip) {
        return this.activeBansByIP.get(ip);
    }

    public BanData getBanById(String banId) {
        return this.banHistory.get(banId);
    }

    public List<BanData> getBanHistory(UUID playerUUID) {
        return (List<BanData>)this.banHistory.values().stream()
                .filter(bd -> bd.getPlayerUUID().equals(playerUUID))
                .collect(Collectors.toList());
    }

    public List<BanData> getAllActiveBans() {
        return new ArrayList<>(this.activeBans.values());
    }

    private String generateBanId() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++)
            sb.append(chars.charAt(random.nextInt(chars.length())));
        return "#" + sb.toString();
    }

    private void startBanCheckTask() {
        Bukkit.getScheduler().runTaskTimer((Plugin)this.plugin, () -> {
            List<UUID> toRemove = new ArrayList<>();
            for (Map.Entry<UUID, BanData> entry : this.activeBans.entrySet()) {
                if (entry.getValue().isExpired()) {
                    toRemove.add(entry.getKey());
                }
            }

            toRemove.forEach(uuid -> {
                BanData bd = this.activeBans.remove(uuid);
                if (bd != null && bd.isIpBan()) {
                    this.activeBansByIP.values().removeIf(b -> b.getPlayerUUID().equals(uuid));
                }
            });

            if (!toRemove.isEmpty()) {
                saveBans();
            }
        }, 1200L, 1200L);
    }

}
