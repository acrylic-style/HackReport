package xyz.acrylicstyle.hackReport.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;
import util.CollectionList;
import xyz.acrylicstyle.api.MojangAPI;
import xyz.acrylicstyle.hackReport.HackReport;

import java.util.Calendar;
import java.util.UUID;

public class NameChangesCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        new BukkitRunnable() {
            @Override
            public void run() {
                UUID uuid = MojangAPI.getUniqueId(args[0]);
                if (uuid == null) {
                    sender.sendMessage(ChatColor.RED + "プレイヤーが見つかりません。");
                    return;
                }
                CollectionList<String> list = new CollectionList<>();
                MojangAPI.getNameChanges(uuid).reverse().foreach((history, index) -> {
                    String date = "";
                    if (history.getChangedToAt() != null) {
                        Calendar cal = Calendar.getInstance();
                        cal.setTimeInMillis(history.getChangedToAt());
                        int year = cal.get(Calendar.YEAR);
                        int month = cal.get(Calendar.MONTH) + 1;
                        int day = cal.get(Calendar.DAY_OF_MONTH);
                        int hours = cal.get(Calendar.HOUR_OF_DAY);
                        int minutes = cal.get(Calendar.MINUTE);
                        int seconds = cal.get(Calendar.SECOND);
                        date = String.format("%s/%s/%s %s:%s:%s", year, month, day, hours, minutes, seconds);
                    }
                    list.add(ChatColor.YELLOW + "#" + (index + 1) + ChatColor.GREEN + ": " + ChatColor.GOLD + history.getName()
                            + "     " + ChatColor.LIGHT_PURPLE + date);
                });
                list.forEach(sender::sendMessage);
            }
        }.runTaskLaterAsynchronously(HackReport.getInstance(), 1);
        return true;
    }
}
