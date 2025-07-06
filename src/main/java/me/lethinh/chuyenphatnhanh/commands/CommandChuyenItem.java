package me.lethinh.chuyenphatnhanh.commands;

import me.clip.placeholderapi.PlaceholderAPI;
import me.lethinh.chuyenphatnhanh.ChuyenPhatNhanh;
import me.lethinh.chuyenphatnhanh.ItemTransfer;
import me.lethinh.chuyenphatnhanh.commands.util.Logger;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.bukkit.ChatColor.translateAlternateColorCodes;

public class CommandChuyenItem implements CommandExecutor{
    PlayerPointsAPI ppAPI;
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be run by a player.");
            return true;
        }
        if (Bukkit.getPluginManager().isPluginEnabled("PlayerPoints")) {
            this.ppAPI = PlayerPoints.getInstance().getAPI();
        }


        Player player = (Player) sender;
        ItemStack item  = player.getInventory().getItemInMainHand();
        ConfigurationSection DieuKienRiengTungItem = ChuyenPhatNhanh.plugin.getConfig().getConfigurationSection("DieuKienRiengTungItem");
        ConfigurationSection DieuKienChungNhomItem = ChuyenPhatNhanh.plugin.getConfig().getConfigurationSection("DieuKienChungNhomItem");

        //DieuKienRiengTungItem
        if(canTransfer(item, DieuKienRiengTungItem)) {
            if (DieuKienRiengTungItem == null) {
                player.sendMessage(ChatColor.RED + "ERROR: The 'DieuKienRiengTungItem' section was not found in your config.yml!");
                player.sendMessage(ChatColor.RED + "Please check for typos or YAML syntax errors (like using tabs).");
                return true;
            }
            if (canTransfer(item, DieuKienRiengTungItem)) {
                if (ppAPI != null) {
                    if (Integer.parseInt(PlaceholderAPI.setPlaceholders(player, "%playerpoints_points%")) >= 5) {
                        ItemTransfer.sendItem(player.getUniqueId(), item, "");
                        player.getInventory().setItemInMainHand(null);
                        ppAPI.take(player.getUniqueId(), 5);
                        int itemCount = 0;
                        for(ItemStack itemC : ItemTransfer.receiveItems(player, true)){
                            itemCount += itemC.getAmount();
                        }
                        player.sendMessage(translateAlternateColorCodes('&', ChuyenPhatNhanh.plugin.getConfig().getString("ChuyenThanhCongMessage").replace("%n%", String.valueOf(itemCount))));
                        Logger.log(player, item, "CHUYEN");
                        return true;
                    } else {
                        player.sendMessage(translateAlternateColorCodes('&', ChuyenPhatNhanh.plugin.getConfig().getString("NoRequestMessage")));
                        return true;
                    }
                }
            } else {
                player.sendMessage(translateAlternateColorCodes('&', ChuyenPhatNhanh.plugin.getConfig().getString("ChuyenDieuKienThatbaiMessage")));
            }
        }

        //DieuKienChungNhomItem
        if (DieuKienChungNhomItem == null) {
            player.sendMessage(ChatColor.RED + "ERROR: The 'DieuKienChungNhomItem' section was not found in your config.yml!");
            player.sendMessage(ChatColor.RED + "Please check for typos or YAML syntax errors (like using tabs).");
            return true;
        }
        if(canTransfer(item, DieuKienChungNhomItem)){
            if(ppAPI != null){
                if(Integer.parseInt(PlaceholderAPI.setPlaceholders(player, "%playerpoints_points%")) >= 10) {
                    ItemTransfer.sendItem(player.getUniqueId(), item, "");
                    player.getInventory().setItemInMainHand(null);
                    ppAPI.take(player.getUniqueId(), 10);
                    int itemCount = 0;
                    for(ItemStack itemC : ItemTransfer.receiveItems(player, true)){
                        itemCount += itemC.getAmount();
                    }
                    player.sendMessage(translateAlternateColorCodes('&', ChuyenPhatNhanh.plugin.getConfig().getString("ChuyenThanhCongMessage").replace("%n%", String.valueOf(itemCount))));
                    Logger.log(player, item, "CHUYEN");
                } else {
                    player.sendMessage(translateAlternateColorCodes('&', ChuyenPhatNhanh.plugin.getConfig().getString("NoRequestMessage")));
                }
                return true;
            }
        } else {
            player.sendMessage(translateAlternateColorCodes('&', ChuyenPhatNhanh.plugin.getConfig().getString("ChuyenDieuKienThatbaiMessage")));
        }
        return true;
    }

    public boolean canTransfer(ItemStack item, ConfigurationSection section) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        List<String> actualItemLore = meta.hasLore() ? meta.getLore() : Collections.emptyList();

        List<String> strippedActualLore = actualItemLore.stream()
                .map(ChatColor::stripColor)
                .collect(Collectors.toList());

        for (String ruleKey : section.getKeys(false)) {
            ConfigurationSection ruleSection = section.getConfigurationSection(ruleKey);
            if (ruleSection == null) {
                continue;
            }

            if (!matchesType(item.getType(), ruleSection)) {
                continue;
            }

            if (matchesLore(strippedActualLore, ruleSection)) {
                return true;
            }
        }

        return false;
    }

    private boolean matchesType(Material itemType, ConfigurationSection ruleSection) {
        if (ruleSection.isList("Type")) {
            List<String> requiredTypes = ruleSection.getStringList("Type");
            return requiredTypes.contains(itemType.name());
        }
        else if (ruleSection.isString("Type")) {
            String requiredType = ruleSection.getString("Type");
            return itemType.name().equalsIgnoreCase(requiredType);
        }
        return false;
    }

    private boolean matchesLore(List<String> strippedActualLore, ConfigurationSection ruleSection) {
        if (!ruleSection.contains("Lore")) {
            return true;
        }

        if (ruleSection.isList("Lore")) {
            List<String> requiredLore = ruleSection.getStringList("Lore");
            if (requiredLore.isEmpty()) {
                return true;
            }
            List<String> strippedRequiredLore = requiredLore.stream()
                    .map(ChatColor::stripColor)
                    .collect(Collectors.toList());

            return Collections.indexOfSubList(strippedActualLore, strippedRequiredLore) != -1;
        }
        else if (ruleSection.isString("Lore")) {
            String requiredLoreLine = ruleSection.getString("Lore");
            String strippedRequiredLore = ChatColor.stripColor(requiredLoreLine);

            for (String strippedActualLine : strippedActualLore) {
                if (strippedActualLine.contains(strippedRequiredLore)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }
}
