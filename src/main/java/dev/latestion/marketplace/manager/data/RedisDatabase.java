package dev.latestion.marketplace.manager.data;

import dev.latestion.marketplace.manager.Manager;
import dev.latestion.marketplace.utils.data.Tuple;
import dev.latestion.marketplace.utils.item.Base64ItemStack;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class RedisDatabase {

    private final JedisPool jedisPool;
    private final Map<UUID, Tuple<UUID, ItemStack, Long>> map = new HashMap<>();

    public RedisDatabase(String host, int port) {
        this.jedisPool = new JedisPool(new JedisPoolConfig(), host, port);
    }

    public UUID addItem(UUID player, String itemStack, long price) {
        UUID uuid = UUID.randomUUID();
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.hset("market_items", uuid.toString(), itemStack);
            jedis.hset("market_price", uuid.toString(), String.valueOf(price));
            jedis.hset("market_owner", uuid.toString(), player.toString());
        } catch (Exception e) {
            map.put(uuid, new Tuple<>(player, Base64ItemStack.decode(itemStack), price));
        }
        return uuid;
    }

    public Map<UUID, Tuple<UUID, ItemStack, Long>> getAllItems() {
        try (Jedis jedis = jedisPool.getResource()) {
            Map<String, String> items = jedis.hgetAll("market_items");
            Map<UUID, Tuple<UUID, ItemStack, Long>> result = new HashMap<>();
            for (Map.Entry<String, String> entry : items.entrySet()) {
                result.put(UUID.fromString(entry.getKey()),
                        new Tuple<>(
                                UUID.fromString(jedis.hget("market-owner", entry.getKey())),
                                Base64ItemStack.decode(entry.getValue()),
                                Long.parseLong(jedis.hget("market_price", entry.getKey()))
                        )
                );
            }
            return result;
        } catch (Exception e) {
            return map;
        }
    }

    public void removeItem(UUID uuid) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.hdel("market_items", uuid.toString());
            jedis.hdel("market_price", uuid.toString());
            jedis.hdel("market-owner", uuid.toString());
        } catch (Exception e) {
            map.remove(uuid);
        }
    }

    public void close() {
        try {
            jedisPool.close();
        }
        catch (Exception e) {
            map.clear();
        }
    }

    public void loadFromSQL(Manager manager) {
        manager.getSql().getAllItems().forEach((uuid, tuple) ->
               manager.addItem(Bukkit.getOfflinePlayer(tuple.a()), Base64ItemStack.decode(tuple.b()), tuple.c()));
        manager.getSql().clearItemDatabase();
    }
}