package me.lethinh.chuyenphatnhanh;

import me.lethinh.chuyenphatnhanh.commands.CommandChuyenItem;
import me.lethinh.chuyenphatnhanh.commands.CommandNhanItem;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public final class ChuyenPhatNhanh extends JavaPlugin {
    public static ChuyenPhatNhanh plugin;
    @Override
    public void onEnable() {
        plugin = this;
        this.getCommand("chuyenitem").setExecutor(new CommandChuyenItem());
        this.getCommand("nhanitem").setExecutor(new CommandNhanItem());
        saveDefaultConfig();
        String dbType = getConfig().getString("database-type", "").toLowerCase();
        try {
            if (dbType.equals("mysql")) {
                String host = getConfig().getString("database.host", "localhost");
                int port = getConfig().getInt("database.port", 3306);
                String database = getConfig().getString("database.name");
                String username = getConfig().getString("database.username");
                String password = getConfig().getString("database.password");
                DatabaseManager.initializeMySQL(host, port, database, username, password);
            } else if (dbType.equals("sqlite")) {
                DatabaseManager.initializeSQLite(getConfig().getString("db-sqlite.file"));
            }

            DatabaseManager. createTable();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
