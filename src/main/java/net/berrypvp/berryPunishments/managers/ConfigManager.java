package net.berrypvp.berryPunishments.managers;

import net.berrypvp.berryPunishments.BerryPunishments;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ConfigManager {
    private final BerryPunishments plugin;

    private FileConfiguration config;

    private FileConfiguration messages;

    private FileConfiguration banscreen;

    private FileConfiguration reasons;

    private FileConfiguration banned;

    private File configFile;

    private File messagesFile;

    private File banscreenFile;

    private File reasonsFile;

    private File bannedFile;

    public ConfigManager(BerryPunishments plugin) {
        this.plugin = plugin;
    }

    public void loadConfigs() {
        this.plugin.saveDefaultConfig();
        this.config = this.plugin.getConfig();
        this.configFile = new File(this.plugin.getDataFolder(), "config.yml");
        this.messagesFile = new File(this.plugin.getDataFolder(), "messages.yml");
        if (!this.messagesFile.exists())
            this.plugin.saveResource("messages.yml", false);
        this.messages = (FileConfiguration) YamlConfiguration.loadConfiguration(this.messagesFile);
        this.banscreenFile = new File(this.plugin.getDataFolder(), "banscreen.yml");
        if (!this.banscreenFile.exists())
            this.plugin.saveResource("banscreen.yml", false);
        this.banscreen = (FileConfiguration)YamlConfiguration.loadConfiguration(this.banscreenFile);
        this.reasonsFile = new File(this.plugin.getDataFolder(), "reasons.yml");
        if (!this.reasonsFile.exists())
            this.plugin.saveResource("reasons.yml", false);
        this.reasons = (FileConfiguration)YamlConfiguration.loadConfiguration(this.reasonsFile);
        this.bannedFile = new File(this.plugin.getDataFolder(), "banned.yml");
        if (!this.bannedFile.exists())
            try {
                this.bannedFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        this.banned = (FileConfiguration)YamlConfiguration.loadConfiguration(this.bannedFile);
    }

    public void reloadConfigs() {
        this.config = (FileConfiguration)YamlConfiguration.loadConfiguration(this.configFile);
        this.messages = (FileConfiguration)YamlConfiguration.loadConfiguration(this.messagesFile);
        this.banscreen = (FileConfiguration)YamlConfiguration.loadConfiguration(this.banscreenFile);
        this.reasons = (FileConfiguration)YamlConfiguration.loadConfiguration(this.reasonsFile);
        this.banned = (FileConfiguration)YamlConfiguration.loadConfiguration(this.bannedFile);
    }

    public void saveBanned() {
        try {
            this.banned.save(this.bannedFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public long parseDuration(String str) {
        if (str == null) return -1;
        str = str.toLowerCase();
        long multiplier = 0;
        try {
            if (str.endsWith("d")) return Long.parseLong(str.replace("d","")) * 24*60*60*1000L;
            if (str.endsWith("h")) return Long.parseLong(str.replace("h","")) * 60*60*1000L;
            if (str.endsWith("m")) return Long.parseLong(str.replace("m","")) * 60*1000L;
            if (str.equals("-1")) return -1L;
            return Long.parseLong(str); // direct ms
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public FileConfiguration getConfig() {
        return this.config;
    }

    public FileConfiguration getMessages() {
        return this.messages;
    }

    public FileConfiguration getBanscreen() {
        return this.banscreen;
    }

    public FileConfiguration getReasons() {
        return this.reasons;
    }

    public FileConfiguration getBanned() {
        return this.banned;
    }

    public ConfigurationSection getBanReasons() {
        return this.reasons.getConfigurationSection("ban-reasons");
    }

    public ConfigurationSection getMuteReasons() {
        return this.reasons.getConfigurationSection("mute-reasons");
    }
}
