package me.lethinh.chuyenphatnhanh.commands.util;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class Logger {
    public static void log(Player player, ItemStack item, String action) {
        try {
            String pluginPath = GetPluginFolder.getPluginFolder();

            // Correct: using LocalDate for dd-MM-yyyy
            Path logFile = Path.of(pluginPath, "Log - " +
                    LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + ".txt");

            Files.createDirectories(logFile.getParent());

            ItemMeta meta = item.getItemMeta();
            List<String> lore = (meta != null && meta.hasLore()) ? meta.getLore() : null;
            String displayName = (meta != null && meta.hasDisplayName()) ? meta.getDisplayName() : item.getType().name();

            // Start of log entry
            Files.writeString(logFile, "\n ------- \n\n", StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            Files.writeString(logFile, "Name: " + player.getName() + "\n", StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            Files.writeString(logFile, "Action: " + (action.equals("CHUYEN") ? "Chuyen do" : "Nhan do") + "\n", StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            Files.writeString(logFile, "Time: " + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")) + "\n", StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            Files.writeString(logFile, "Type: " + item.getType() + "\n", StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            Files.writeString(logFile, "Count: " + item.getAmount() + "\n", StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            Files.writeString(logFile, "  display-name: " + displayName + "\n", StandardOpenOption.CREATE, StandardOpenOption.APPEND);

            // Lore lines
            Files.writeString(logFile, "  lore:\n", StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            if (lore != null && !lore.isEmpty()) {
                for (String line : lore) {
                    Files.writeString(logFile, "  - " + line + "\n", StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                }
            } else {
                Files.writeString(logFile, "  - (none)\n", StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            }

            // Enchantments
            Files.writeString(logFile, "enchants:\n", StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            if (!item.getEnchantments().isEmpty()) {
                for (Map.Entry<Enchantment, Integer> entry : item.getEnchantments().entrySet()) {
                    Enchantment enchant = entry.getKey();
                    int level = entry.getValue();
                    Files.writeString(logFile,
                            "  - " + enchant.getName() + " " + level + "\n",
                            StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                }
            } else {
                Files.writeString(logFile, "  - (none)\n", StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            }

            // Unbreakable
            Files.writeString(logFile,
                    "Unbreakable: " + (meta != null && meta.isUnbreakable()) + "\n",
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace(); // You could also log this with Bukkit's logger if you want
        }
    }
}
