package dev.latestion.marketplace.manager;

import dev.latestion.marketplace.MarketPlace;
import org.bukkit.configuration.file.FileConfiguration;
import org.yaml.snakeyaml.error.Mark;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * MySQL Transaction Data
 */
public class Database {
    private Connection connection;
    private Connection getConnection() throws SQLException {

        if(connection != null){
            return connection;
        }

        FileConfiguration config = MarketPlace.get().getConfig();

        this.connection = DriverManager.getConnection(
                config.getString("sql.url", ""),
                config.getString("sql.user", ""),
                config.getString("sql.password", "")
        );

        System.out.println("Connected to sql-database.");
        return connection;

    }

    public void innitTransactionDatabase() throws SQLException {
        Statement statement = getConnection().createStatement();
        String sql = "CREATE TABLE IF NOT EXISTS marketplace_transactions ("
                + "uuid varchar(36) primary key, "
                + "quantity int, "
                + "price long, "
                + "time DATE, "
                + "item_name varchar(1000)" // Add this line
                + ")";
        statement.execute(sql);
        statement.close();
    }
    

}
