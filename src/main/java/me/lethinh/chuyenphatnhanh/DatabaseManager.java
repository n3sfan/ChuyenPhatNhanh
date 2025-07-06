package me.lethinh.chuyenphatnhanh;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    private static HikariDataSource dataSource;

    public static void initializeMySQL(String host, int port, String database, String username, String password) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
        config.setUsername(username);
        config.setPassword(password);

        // Recommended settings for performance and reliability
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.setMaximumPoolSize(10); // Max 10 connections per server instance
        config.setMinimumIdle(5);
        config.setLeakDetectionThreshold(15000);
        config.setConnectionTimeout(30000);

        dataSource = new HikariDataSource(config);
    }

    public static void initializeSQLite(String path) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:sqlite:" + path);

        // Recommended settings for performance and reliability
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.setMaximumPoolSize(10); // Max 10 connections per server instance
        config.setMinimumIdle(5);
        config.setLeakDetectionThreshold(15000);
        config.setConnectionTimeout(30000);

        dataSource = new HikariDataSource(config);
    }

    public static void createTable() throws SQLException {
        Connection conn = getConnection();
        try (Statement stmt = conn.createStatement()) {
            String s = "CREATE TABLE IF NOT EXISTS `pending_items` (\n" +
                    "  `id` INTEGER PRIMARY KEY\n,\n" +
                    "  `target_uuid` VARCHAR(36) NOT NULL,\n" +
                    "  `item_data` MEDIUMTEXT NOT NULL,\n" +
                    "  `origin_server` VARCHAR(50) NULL,\n" +
                    "  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP" +
                    ");";
            stmt.executeUpdate(s);
        }
        try (Statement stmt = conn.createStatement()) {
            String s = "CREATE INDEX IF NOT EXISTS idx_target_uuid ON pending_items(target_uuid);\n";
            stmt.executeUpdate(s);
        }
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}
