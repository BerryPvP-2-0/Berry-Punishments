package net.berrypvp.berryPunishments.commands;

import net.berrypvp.berryPunishments.BerryPunishments;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KickCommand implements CommandExecutor {
    private final BerryPunishments plugin;

    public KickCommand(BerryPunishments plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("berry.command.punishments.kick")) {
            sender.sendMessage("§cYou do not have permission to use this command.");
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage("§cUsage: /kick <player> [reason]");
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            sender.sendMessage("§cThat player is not online.");
            return true;
        }

        String reason = "Kicked by staff";
        if (args.length > 1) {
            StringBuilder sb = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                sb.append(args[i]).append(" ");
            }
            reason = sb.toString().trim();
        }

        final String fReason = reason;
        final String staffName = sender.getName();
        final String targetName = target.getName();

        target.kickPlayer("§cYou were kicked from the server\n§7Reason: §f" + fReason);

        Bukkit.getOnlinePlayers().forEach(p -> {
            if (p.hasPermission("berry.command.punishments.kick.seelogs")) {
                p.sendMessage("§c[Kick] §f" + targetName + " §7was kicked by §f" + staffName + " §7for: §f" + fReason);
            }
        });

        // Console feedback
        sender.sendMessage("§aSuccessfully kicked §f" + targetName);

        return true;
    }
}
