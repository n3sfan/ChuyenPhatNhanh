package me.lethinh.chuyenphatnhanh;

import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public final class ChuyenPhatNhanh extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        String dbType = getConfig().getString("database.type", "mysql").toLowerCase();
        try {
            if (dbType.equals("mysql")) {
                String host = getConfig().getString("database.host", "localhost");
                int port = getConfig().getInt("database.port", 3306);
                String database = getConfig().getString("database.name");
                String username = getConfig().getString("database.username");
                String password = getConfig().getString("database.password");
                DatabaseManager.initializeMySQL(host, port, database, username, password);
            } else if (dbType.equals("sqlite")) {
                DatabaseManager.initializeSQLite(getDataFolder().getAbsolutePath() + "/database.db");
            }

            DatabaseManager.createTable();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
