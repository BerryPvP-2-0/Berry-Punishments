package net.berrypvp.berryPunishments;

import net.berrypvp.berryPunishments.commands.*;
import net.berrypvp.berryPunishments.listeners.PlayerListener;
import net.berrypvp.berryPunishments.managers.BanManager;
import net.berrypvp.berryPunishments.managers.ConfigManager;
import net.berrypvp.berryPunishments.managers.MuteManager;
import net.berrypvp.berryPunishments.managers.ReasonManager;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class BerryPunishments extends JavaPlugin {

    private static BerryPunishments instance;

    private BanManager banManager;
    private ConfigManager configManager;
    private MuteManager muteManager;
    private ReasonManager reasonManager;

    @Override
    public void onEnable() {
        instance = this;
        registerCommands();
        getServer().getPluginManager().registerEvents((Listener) new PlayerListener(this), (Plugin) this);
        getServer().getConsoleSender().sendMessage("§aBerryPunishments has been enabled!");
    }

    @Override
    public void onDisable() {
        getServer().getConsoleSender().sendMessage("§cBerryPunishments has been disabled!");
    }

    private void registerCommands() {
        getCommand("searchid").setExecutor((CommandExecutor) new SearchIDCommand(this));
        getCommand("unban").setExecutor((CommandExecutor) new UnbanCommand(this));
        getCommand("unbanip").setExecutor((CommandExecutor) new UnbanIPCommand(this));
        getCommand("unmute").setExecutor((CommandExecutor) new UnmuteCommand(this));
        getCommand("berrypunishments").setExecutor((CommandExecutor) new BerryPunishmentsCommand(this));
        getCommand("ban").setExecutor((CommandExecutor) new BanCommand(this));
        getCommand("mute").setExecutor((CommandExecutor) new MuteCommand(this));
        getCommand("banip").setExecutor((CommandExecutor) new BanIPCommand(this));
        getCommand("alts").setExecutor((CommandExecutor) new AltsCommand(this));
        getCommand("offend").setExecutor((CommandExecutor) new OffendCommand(this));
        getCommand("kick").setExecutor((CommandExecutor) new KickCommand(this));
        getCommand("history").setExecutor((CommandExecutor) new HistoryCommand(this));

        getCommand("ban").setTabCompleter(new BanCommand(this));
        getCommand("mute").setTabCompleter(new MuteCommand(this));
        getCommand("banip").setTabCompleter(new BanIPCommand(this));
    }

    public static BerryPunishments getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return this.configManager;
    }

    public BanManager getBanManager() {
        return this.banManager;
    }

    public MuteManager getMuteManager() {
        return this.muteManager;
    }

    public ReasonManager getReasonManager() {
        return this.reasonManager;
    }
}
