package net.berrypvp.berryPunishments.listeners;

import net.berrypvp.berryPunishments.BerryPunishments;
import net.berrypvp.berryPunishments.utils.BanData;
import net.berrypvp.berryPunishments.utils.BanScreen;
import net.berrypvp.berryPunishments.utils.MuteData;
import net.berrypvp.berryPunishments.utils.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.Set;
import java.util.stream.Collectors;

public class PlayerListener implements Listener {
    private final BerryPunishments plugin;

    public PlayerListener(BerryPunishments plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        if (this.plugin.getBanManager().isBanned(player.getUniqueId())) {
            BanData banData = this.plugin.getBanManager().getBan(player.getUniqueId());
            event.disallow(PlayerLoginEvent.Result.KICK_BANNED, BanScreen.createBanScreen(banData));
            return;
        }
        String ip = event.getAddress().getHostAddress();
        if (this.plugin.getBanManager().isIPBanned(ip)) {
            BanData banData = this.plugin.getBanManager().getBanByIP(ip);
            event.disallow(PlayerLoginEvent.Result.KICK_BANNED, BanScreen.createBanScreen(banData));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (this.plugin.getMuteManager().isMuted(player.getUniqueId())) {
            MuteData muteData = this.plugin.getMuteManager().getMute(player.getUniqueId());
            String messageTemplate = this.plugin.getConfigManager().getMessages().getString("you-are-muted");
            if (messageTemplate == null)
                messageTemplate = "&cYou are muted for %reason% - Time left: %timeleft%";
            String message = messageTemplate.replace("&", "").replace("%reason%", muteData.getReason()).replace("%duration%", TimeUtil.formatTime(muteData.getDuration())).replace("%timeleft%", TimeUtil.formatTime(muteData.getTimeLeft()));
            player.sendMessage(message);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            // Get player's IP
            Set<String> ips = plugin.getBanManager().getAllActiveBans().stream()
                    .filter(BanData::isIpBan)
                    .filter(bd -> bd.getPlayerUUID() != null && bd.getPlayerUUID().equals(player.getUniqueId()))
                    .map(BanData::getIp)
                    .collect(Collectors.toSet());

            if (player.getAddress() != null) {
                ips.add(player.getAddress().getAddress().getHostAddress());
            }

            // Check for banned accounts sharing IP
            for (String ip : ips) {
                Set<String> bannedAlts = plugin.getBanManager().getAllActiveBans().stream()
                        .filter(BanData::isIpBan)
                        .filter(bd -> bd.getIp() != null && bd.getIp().equals(ip))
                        .map(BanData::getPlayerName)
                        .collect(Collectors.toSet());

                if (!bannedAlts.isEmpty()) {
                    // Notify staff only
                    for (Player staff : Bukkit.getOnlinePlayers()) {
                        if (staff.hasPermission("berry.command.punishments.alts.seelogs")) {
                            bannedAlts.forEach(name ->
                                    staff.sendMessage("§c[ALTS] §f" + name + " is banned and associated with §f" + player.getName())
                            );
                        }
                    }
                }
            }
        }, 20L); // 1 second delay to ensure IP is loaded
    }
}
