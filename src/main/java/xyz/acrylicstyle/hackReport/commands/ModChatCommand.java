package xyz.acrylicstyle.hackReport.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.permissions.ServerOperator;
import util.ICollectionList;
import xyz.acrylicstyle.hackReport.HackReport;
import xyz.acrylicstyle.joinChecker.JoinCheckerManager;
import xyz.acrylicstyle.joinChecker.utils.Utils;
import xyz.acrylicstyle.tomeito_api.command.PlayerCommandExecutor;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;

public class ModChatCommand extends PlayerCommandExecutor {
    public static final String PREFIX = ChatColor.GREEN + "ModChat " + ChatColor.LIGHT_PURPLE + ">> " + ChatColor.YELLOW;

    @Override
    public void onCommand(Player player, String[] args) {
        new Thread(() -> {
            if (Utils.modCheck(player)) return;
            if (args.length != 0) {
                Do(player.getName(), ICollectionList.asList(args).join(" "));
                return;
            }
            if (HackReport.modChat.contains(player.getUniqueId())) {
                HackReport.modChat.remove(player.getUniqueId());
                player.sendMessage(PREFIX + "ModChatをオフにしました。");
            } else {
                if (HackReport.opChat.contains(player.getUniqueId())) {
                    player.sendMessage(ChatColor.RED + "> OPChatとModChatは同時に使用できません。");
                    return;
                }
                HackReport.modChat.add(player.getUniqueId());
                player.sendMessage(PREFIX + "ModChatをオンにしました。");
            }
        }).start();
    }

    @SuppressWarnings("RedundantCast")
    public static void Do(String name, String message) {
        JoinCheckerManager.moderators
                .toMap()
                .then(map -> {
                    map.keysList()
                            .map((Function<UUID, Player>) Bukkit::getPlayer)
                            .filter(Objects::nonNull)
                            .concat(new xyz.acrylicstyle.joinChecker.libs.util.CollectionList<>(Bukkit.getOnlinePlayers()).map(p -> (Player) p).filter(ServerOperator::isOp))
                            .unique()
                            .forEach(player -> player.sendMessage(ModChatCommand.PREFIX
                                    + ChatColor.GOLD + name
                                    + ChatColor.WHITE + ": "
                                    + ChatColor.YELLOW + ChatColor.translateAlternateColorCodes('&', message)));
                    return null;
                }).queue();
    }
}
