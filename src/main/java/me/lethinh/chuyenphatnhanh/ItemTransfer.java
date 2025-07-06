package me.lethinh.chuyenphatnhanh;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ItemTransfer {

    /**
     * Saves an item to the database for another player to claim.
     */
    public static void sendItem(UUID targetUuid, ItemStack item, String originServer) {
        String base64Item = ItemSerializer.itemStackToBase64(item);

        String sql = "INSERT INTO pending_items (target_uuid, item_data, origin_server) VALUES (?, ?, ?);";

        // try-with-resources ensures the connection and statement are closed
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, targetUuid.toString());
            pstmt.setString(2, base64Item);
            pstmt.setString(3, originServer);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace(); // Handle this error properly!
        }
    }

    /**
     * Checks the database for items, gives them to the player, and deletes the entries.
     * This operation is ATOMIC.
     */

    public static List<ItemStack> receiveItems(Player player, boolean isFirstTime) {
        String selectSql = "SELECT id, item_data FROM pending_items WHERE target_uuid = ?;";
        List<Integer> claimedIds = new ArrayList<>();
        List<ItemStack> receivedItems = new ArrayList<>();

        // Use a transaction to ensure we don't lose items if the server crashes
        try (Connection conn = DatabaseManager.getConnection()) {
            conn.setAutoCommit(false); // Start transaction

            try (PreparedStatement selectPstmt = conn.prepareStatement(selectSql)) {
                selectPstmt.setString(1, player.getUniqueId().toString());

                ResultSet rs = selectPstmt.executeQuery();
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String base64Item = rs.getString("item_data");

                    try {
                        ItemStack item = ItemSerializer.itemStackFromBase64(base64Item);
                        if (item != null) {
                            receivedItems.add(item);
                            claimedIds.add(id);
                        }
                    } catch (IOException e) {
                        // Log an error: This item data is corrupt in the DB!
                        System.err.println("Failed to deserialize item with ID: " + id);
                        e.printStackTrace();
                    }
                }
            }

            if (!isFirstTime) {
                // If we successfully retrieved items, delete them from the database
                if (!claimedIds.isEmpty()) {
                    String deleteSql = "DELETE FROM pending_items WHERE id = ?;";
                    try (PreparedStatement deletePstmt = conn.prepareStatement(deleteSql)) {
                        for (Integer id : claimedIds) {
                            deletePstmt.setInt(1, id);
                            deletePstmt.addBatch();
                        }
                        deletePstmt.executeBatch();
                    }
                }
            }

            conn.commit(); // Finalize the transaction
            if(!isFirstTime) {
                // Now give the items to the player
                for (ItemStack item : receivedItems) {
                    // Handle full inventory!
                    if (player.getInventory().firstEmpty() == -1) {
                        player.getWorld().dropItem(player.getLocation(), item);
                        player.sendMessage("§cYour inventory was full, an item was dropped at your feet!");
                    } else {
                        player.getInventory().addItem(item);
                    }
                }
//                if (!receivedItems.isEmpty()) {
//                    player.sendMessage("§aYou have claimed " + receivedItems.size() + " item(s)!");
//                }
            }

        } catch (SQLException e) {
            // An error occurred, we should not proceed. The try-with-resources will
            // close the connection, and the database will automatically roll back the transaction.
            e.printStackTrace();
            player.sendMessage("§cAn error occurred while claiming your items. Please try again later.");
        }
        return receivedItems;
    }
}
