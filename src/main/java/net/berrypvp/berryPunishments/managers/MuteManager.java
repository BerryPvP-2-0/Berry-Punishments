package net.berrypvp.berryPunishments.managers;

import net.berrypvp.berryPunishments.BerryPunishments;
import net.berrypvp.berryPunishments.utils.BanData;
import net.berrypvp.berryPunishments.utils.MuteData;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.stream.Collectors;

public class MuteManager {
    private final BerryPunishments plugin;

    private final Map<UUID, MuteData> activeMutes;

    private final Map<String, MuteData> muteHistory;

    public MuteManager(BerryPunishments plugin) {
        this.plugin = plugin;
        this.activeMutes = new HashMap<>();
        this.muteHistory = new HashMap<>();
        loadMutes();
        startMuteCheckTask();
    }

    private void loadMutes() {
        FileConfiguration banned = this.plugin.getConfigManager().getBanned();
        if (banned.contains("mutes") && banned.isConfigurationSection("mutes")) {
            ConfigurationSection mutesSection = banned.getConfigurationSection("mutes");
            if (mutesSection != null)
                for (String muteId : mutesSection.getKeys(false)) {
                    ConfigurationSection muteSection = mutesSection.getConfigurationSection(muteId);
                    if (muteSection != null)
                        try {
                            UUID playerUUID = UUID.fromString(muteSection.getString("uuid"));
                            String playerName = muteSection.getString("name");
                            UUID mutedBy = UUID.fromString(muteSection.getString("mutedBy"));
                            String mutedByName = muteSection.getString("mutedByName");
                            String reason = muteSection.getString("reason");
                            long mutedAt = muteSection.getLong("mutedAt");
                            long duration = muteSection.getLong("duration");
                            MuteData muteData = new MuteData(muteId, playerUUID, playerName, mutedBy, mutedByName, reason, mutedAt, duration);
                            this.muteHistory.put(muteId, muteData);
                            if (!muteData.isExpired())
                                this.activeMutes.put(playerUUID, muteData);
                        } catch (Exception e) {
                            this.plugin.getLogger().warning("Failed to load mute " + muteId + ": " + e.getMessage());
                        }
                }
        }
    }

    public void saveMutes() {
        FileConfiguration banned = this.plugin.getConfigManager().getBanned();
        banned.set("mutes", null);
        for (MuteData muteData : this.muteHistory.values()) {
            String path = "mutes." + muteData.getMuteId();
            banned.set(path + ".uuid", muteData.getPlayerUUID().toString());
            banned.set(path + ".name", muteData.getPlayerName());
            banned.set(path + ".mutedBy", muteData.getMutedBy().toString());
            banned.set(path + ".mutedByName", muteData.getMutedByName());
            banned.set(path + ".reason", muteData.getReason());
            banned.set(path + ".mutedAt", Long.valueOf(muteData.getMutedAt()));
            banned.set(path + ".duration", Long.valueOf(muteData.getDuration()));
        }
        this.plugin.getConfigManager().saveBanned();
    }

    public String mutePlayer(UUID playerUUID, String playerName, UUID mutedBy, String mutedByName, String reason, long duration) {
        String muteId = generateMuteId();
        MuteData muteData = new MuteData(muteId, playerUUID, playerName, mutedBy, mutedByName, reason, System.currentTimeMillis(), duration);
        this.activeMutes.put(playerUUID, muteData);
        this.muteHistory.put(muteId, muteData);
        saveMutes();
        return muteId;
    }

    public boolean unmutePlayer(UUID playerUUID) {
        MuteData muteData = this.activeMutes.remove(playerUUID);
        if (muteData != null) {
            FileConfiguration banned = this.plugin.getConfigManager().getBanned();
            banned.set("mutes." + muteData.getMuteId(), null);
            this.plugin.getConfigManager().saveBanned();
            this.muteHistory.remove(muteData.getMuteId());
            return true;
        }
        return false;
    }

    public boolean isMuted(UUID playerUUID) {
        MuteData muteData = this.activeMutes.get(playerUUID);
        if (muteData != null && muteData.isExpired()) {
            this.activeMutes.remove(playerUUID);
            saveMutes();
            return false;
        }
        return (muteData != null);
    }

    public MuteData getMute(UUID playerUUID) {
        return this.activeMutes.get(playerUUID);
    }

    public MuteData getMuteById(String muteId) {
        return this.muteHistory.get(muteId);
    }

    public List<MuteData> getMuteHistory(UUID playerUUID) {
        return (List<MuteData>)this.muteHistory.values().stream()
                .filter(md -> md.getPlayerUUID().equals(playerUUID))
                .collect(Collectors.toList());
    }

    public List<MuteData> getAllActiveMutes() {
        return new ArrayList<>(this.activeMutes.values());
    }

    private String generateMuteId() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++)
            sb.append(chars.charAt(random.nextInt(chars.length())));
        return "#" + sb.toString();
    }


    private void startMuteCheckTask() {
        Bukkit.getScheduler().runTaskTimer((Plugin)this.plugin, () -> {
            List<UUID> toRemove = new ArrayList<>();
            for (Map.Entry<UUID, MuteData> entry : this.activeMutes.entrySet()) {
                if (entry.getValue().isExpired()) {
                    toRemove.add(entry.getKey());
                }
            }

            if (!toRemove.isEmpty()) {
                saveMutes();
            }
        }, 1200L, 1200L);
    }

}
