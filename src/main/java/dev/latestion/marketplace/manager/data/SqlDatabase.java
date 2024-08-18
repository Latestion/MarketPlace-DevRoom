package dev.latestion.marketplace.manager.data;

import dev.latestion.marketplace.MarketPlace;
import dev.latestion.marketplace.utils.data.Tuple;
import dev.latestion.marketplace.utils.item.Base64ItemStack;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Level;

/**
 * MySQL Transaction Data
 */
public class SqlDatabase extends BukkitRunnable {

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

        Bukkit.getLogger().log(Level.FINE, "Connected to sql-database: " + (connection == null));
        return connection;

    }

    public void initTransactionDatabase() throws SQLException {
        Statement statement = getConnection().createStatement();
        String sql = "CREATE TABLE IF NOT EXISTS marketplace_transactions ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "uuid VARCHAR(36), "
                + "quantity INT, "
                + "price LONG, "
                + "time TIMESTAMP, "
                + "item_name VARCHAR(1000)"
                + ")";
        statement.execute(sql);
        statement.close();

        runTaskTimerAsynchronously(MarketPlace.get(), 60 * 20, 60 * 20);
    }

    private final Map<UUID, List<Transaction>> transactions = new HashMap<>();
    private final List<Transaction> transactQueue = new ArrayList<>();

    public void addTransaction(UUID uuid, int quantity, long price, LocalDateTime time, String itemName) {

        List<Transaction> playerTransactions = transactions.get(uuid);
        Transaction transaction = new Transaction(quantity, price, time, itemName, uuid.toString());
        playerTransactions.add(transaction);
        transactions.put(uuid, playerTransactions);
        transactQueue.add(transaction);

    }

    public List<Transaction> getTransactions(UUID uuid) throws SQLException {

        if (transactions.containsKey(uuid)) {
            return transactions.get(uuid);
        }

        String sql = "SELECT * FROM marketplace_transactions WHERE uuid = ?";
        List<Transaction> transactions = new ArrayList<>();

        try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
            statement.setString(1, uuid.toString());
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Transaction transaction = new Transaction(
                            resultSet.getInt("quantity"),
                            resultSet.getLong("price"),
                            resultSet.getTimestamp("time").toLocalDateTime(),
                            resultSet.getString("item_name"),
                            resultSet.getString("uuid")
                    );
                    transactions.add(transaction);
                }
            }
        }

        this.transactions.put(uuid, transactions);
        return transactions;
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

    @SneakyThrows @Override
    public void run() {

        String sql = "INSERT INTO marketplace_transactions (uuid, quantity, price, time, item_name) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
            for (Transaction transaction : transactQueue) {
                statement.setString(1, transaction.uuid());
                statement.setInt(2, transaction.quantity());
                statement.setLong(3, transaction.price());
                statement.setTimestamp(4, Timestamp.valueOf(transaction.time()));
                statement.setString(5, transaction.itemName());
                statement.addBatch();
            }

            statement.executeBatch();
        }

        transactQueue.clear();
    }
}
