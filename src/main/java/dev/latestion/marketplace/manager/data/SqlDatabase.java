package dev.latestion.marketplace.manager.data;

import dev.latestion.marketplace.MarketPlace;
import dev.latestion.marketplace.utils.data.Tuple;
import dev.latestion.marketplace.utils.item.Base64ItemStack;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * MySQL Transaction Data
 */
public class SqlDatabase {

    private Connection connection;

    private Connection getConnection() throws SQLException {

        if(connection != null){
            return connection;
        }

        FileConfiguration config = MarketPlace.get().getConfig();

        this.connection = DriverManager.getConnection("jdbc:mysql://" +
                        config.getString("sql.host") + ":" +
                        config.getInt("sql.port") + "/" +
                        config.getString("sql.database") + "?useSSL=false",
                config.getString("sql.user"), config.getString("sql.password"));

        System.out.println("Connected to sql-database: " + (connection == null));
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


    public void innitItemDatabase() throws SQLException {
        Statement statement = getConnection().createStatement();
        String createTableSQL = "CREATE TABLE IF NOT EXISTS marketplace_items ("
                + "main_uuid CHAR(36) NOT NULL, "
                + "inner_uuid CHAR(36) NOT NULL, "
                + "item_stack TEXT NOT NULL, "
                + "value BIGINT NOT NULL, "
                + "PRIMARY KEY (main_uuid, inner_uuid)"
                + ");";

        statement.execute(createTableSQL);
        statement.close();
    }

    public void insertItemData(Map<UUID, Tuple<UUID, ItemStack, Long>> data) {
        String insertSQL = "INSERT INTO marketplace_items (main_uuid, inner_uuid, item_stack, value) "
                + "VALUES (?, ?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE item_stack = VALUES(item_stack), value = VALUES(value);";

        try (PreparedStatement preparedStatement = getConnection().prepareStatement(insertSQL)) {

            for (Map.Entry<UUID, Tuple<UUID, ItemStack, Long>> entry : data.entrySet()) {

                UUID mainUUID = entry.getKey();
                Tuple<UUID, ItemStack, Long> tuple = entry.getValue();
                UUID innerUUID = tuple.a();
                ItemStack itemStack = tuple.b();
                long value = tuple.c();

                preparedStatement.setString(1, mainUUID.toString());
                preparedStatement.setString(2, innerUUID.toString());
                preparedStatement.setString(3, Base64ItemStack.encode(itemStack));
                preparedStatement.setLong(4, value);

                preparedStatement.addBatch(); // Add to batch
            }

            preparedStatement.executeBatch(); // Execute all at once
            System.out.println("Data inserted successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Map<UUID, Tuple<UUID, String, Long>> getAllItems() {
        Map<UUID, Tuple<UUID, String, Long>> data = new HashMap<>();
        String querySQL = "SELECT main_uuid, inner_uuid, item_stack, value FROM marketplace_items;";

        try (PreparedStatement preparedStatement = getConnection().prepareStatement(querySQL);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                UUID mainUUID = UUID.fromString(resultSet.getString("main_uuid"));
                UUID innerUUID = UUID.fromString(resultSet.getString("inner_uuid"));
                String base64ItemStack = resultSet.getString("item_stack");
                Long value = resultSet.getLong("value");


                Tuple<UUID, String, Long> tuple = new Tuple<>(innerUUID, base64ItemStack, value);
                data.put(mainUUID, tuple);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return data;
    }

    public void clearItemDatabase() {
        String clearSQL = "DELETE FROM marketplace_items;";

        try (PreparedStatement preparedStatement = getConnection().prepareStatement(clearSQL)) {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
