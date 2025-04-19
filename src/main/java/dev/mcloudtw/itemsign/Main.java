package dev.mcloudtw.itemsign;

import dev.mcloudtw.itemsign.commands.SignCommand;
import dev.mcloudtw.itemsign.commands.SignInfoCommand;
import dev.mcloudtw.itemsign.commands.UnsignCommand;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class Main extends JavaPlugin {
    static List<String> signableItems;

    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getPluginManager().registerEvents(new Events(), this);
        saveDefaultConfig();

//        System.out.println(getConfig());
        signableItems = getConfig().getStringList("item-sign-white-list");

        SignCommand.command().register();
        SignInfoCommand.command().register();
        UnsignCommand.command().register();

    }

    public static boolean testSignable(Material material) {
//        System.out.println(material.getKey().toString());
        return signableItems.contains(material.getKey().toString());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
