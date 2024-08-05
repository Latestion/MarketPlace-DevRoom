package dev.latestion.marketplace.utils.data;

import dev.latestion.marketplace.MarketPlace;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;


public class DataManager {

    private final MarketPlace plugin = MarketPlace.get();
    private FileConfiguration dataConfig;
    private File configFile;
    @Getter
    private final String name;

    public DataManager(String name) {
        this.name = name;
        this.dataConfig = null;
        this.configFile = null;
        this.saveDefaultConfig();
    }

    public DataManager(String name, File configFile) {
        this.name = name;
        this.configFile = configFile;
        this.dataConfig = YamlConfiguration.loadConfiguration(configFile);
    }

    public void reloadConfig() {
        if (this.configFile == null) {
            this.configFile = new File(this.plugin.getDataFolder(), name + ".yml");
        }

        this.dataConfig = YamlConfiguration.loadConfiguration(this.configFile);
        final InputStream defaultStream = this.plugin.getResource(name + ".yml");

        if (defaultStream != null) {
            final YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
            this.dataConfig.setDefaults(defaultConfig);
        }

    }

    public FileConfiguration getConfig() {
        if (this.dataConfig == null) {
            this.reloadConfig();
        }
        return this.dataConfig;
    }

    public void saveConfig() {
        if (this.dataConfig == null || this.configFile == null) {
            return;
        }
        try {
            this.getConfig().save(this.configFile);
        } catch (IOException e) {
            this.plugin.getLogger().log(Level.SEVERE, "Could Not Save Config to" + this.configFile, e);
        }
    }

    public void saveDefaultConfig() {
        if (this.configFile == null) {
            this.configFile = new File(this.plugin.getDataFolder(), name + ".yml");
        }
        if (!this.configFile.exists()) {
            this.plugin.saveResource(name + ".yml", false);
        }
    }

    public static List<DataManager> loadYamlFilesInFolder(File folder) {
        List<DataManager> configurations = new ArrayList<>();
        File[] files = folder.listFiles();
        assert files != null;
        for (File file : files) {
            configurations.add(new DataManager(file.getName().substring(0, file.getName().length() - 4), file));
        }
        return configurations;
    }

    public static List<FileConfiguration> loadYamlFiles(File folder) {
        List<FileConfiguration> configurations = new ArrayList<>();
        File[] files = folder.listFiles();
        assert files != null;
        for (File file : files) {
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            configurations.add(config);
        }
        return configurations;
    }


}
