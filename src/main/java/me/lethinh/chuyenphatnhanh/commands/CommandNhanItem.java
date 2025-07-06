package me.lethinh.chuyenphatnhanh.commands;

import me.lethinh.chuyenphatnhanh.ChuyenPhatNhanh;
import me.lethinh.chuyenphatnhanh.ItemTransfer;
import me.lethinh.chuyenphatnhanh.commands.util.Logger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static me.lethinh.chuyenphatnhanh.ItemTransfer.receiveItems;
import static org.bukkit.ChatColor.translateAlternateColorCodes;

public class CommandNhanItem implements CommandExecutor {

    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private static final long COOLDOWN_SECONDS = Integer.parseInt(ChuyenPhatNhanh.plugin.getConfig().getString("NhanItemCooldown"));

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be run by a player.");
            return true;
        }

        Player player = (Player) sender;
        UUID playerUUID = player.getUniqueId();

        if (cooldowns.containsKey(playerUUID)) {
            long lastExecutionTime = cooldowns.get(playerUUID);
            long currentTime = System.currentTimeMillis();
            long timeElapsed = currentTime - lastExecutionTime;
            long cooldownMillis = TimeUnit.SECONDS.toMillis(COOLDOWN_SECONDS);

            if (timeElapsed < cooldownMillis) {
                ItemTransfer.receiveItems(player, false);
                player.sendMessage(translateAlternateColorCodes('&', ChuyenPhatNhanh.plugin.getConfig().getString("NhanThanhCongMessage")));
                for (ItemStack item : receiveItems(player, true)) {
                    Logger.log(player, item, "NHAN");
                }
                cooldowns.clear();
                return true;
            }
        }
        int itemCount = 0;
        for(ItemStack itemC : ItemTransfer.receiveItems(player, true)){
            itemCount += itemC.getAmount();
        }

        player.sendMessage(translateAlternateColorCodes('&', ChuyenPhatNhanh.plugin.getConfig().getString("XacNhanMessage").replace("%n%", String.valueOf(itemCount))));
        cooldowns.put(playerUUID, System.currentTimeMillis());

        return true;
    }
}
