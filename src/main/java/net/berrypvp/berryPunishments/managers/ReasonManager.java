package net.berrypvp.berryPunishments.managers;

import net.berrypvp.berryPunishments.BerryPunishments;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class ReasonManager {
    private final BerryPunishments plugin;

    public ReasonManager(BerryPunishments plugin) {
        this.plugin = plugin;
    }

    public List<String> getBanReasons() {
        FileConfiguration reasons = this.plugin.getConfigManager().getReasons();
        List<String> reasonList = new ArrayList<>();
        ConfigurationSection banReasons = reasons.getConfigurationSection("ban-reasons");
        if (banReasons != null)
            for (String key : banReasons.getKeys(false))
                reasonList.add(key);
        return reasonList;
    }

    public List<String> getMuteReasons() {
        FileConfiguration reasons = this.plugin.getConfigManager().getReasons();
        List<String> reasonList = new ArrayList<>();
        ConfigurationSection muteReasons = reasons.getConfigurationSection("mute-reasons");
        if (muteReasons != null)
            for (String key : muteReasons.getKeys(false))
                reasonList.add(key);
        return reasonList;
    }

    public String getBanDuration(String reason) {
        FileConfiguration reasons = this.plugin.getConfigManager().getReasons();
        return reasons.getString("ban-reasons." + reason + ".duration", "7d");
    }

    public String getMuteDuration(String reason) {
        FileConfiguration reasons = this.plugin.getConfigManager().getReasons();
        return reasons.getString("mute-reasons." + reason + ".duration", "1h");
    }

    public String getBanDisplay(String reason) {
        FileConfiguration reasons = this.plugin.getConfigManager().getReasons();
        return reasons.getString("ban-reasons." + reason + ".display", reason);
    }

    public String getMuteDisplay(String reason) {
        FileConfiguration reasons = this.plugin.getConfigManager().getReasons();
        return reasons.getString("mute-reasons." + reason + ".display", reason);
    }
}
