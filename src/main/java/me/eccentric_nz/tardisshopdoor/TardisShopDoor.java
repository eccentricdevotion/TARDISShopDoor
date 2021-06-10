package me.eccentric_nz.tardisshopdoor;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class TardisShopDoor extends JavaPlugin {

    public static TardisShopDoor plugin;
    public HashMap<UUID, TardisShopDoorAdd> trackAdd = new HashMap<>();
    public Plugin tardis;
    TardisShopDoorDatabase service = TardisShopDoorDatabase.getInstance();
    TardisPrefsDatabase keyprefs = TardisPrefsDatabase.getInstance();

    @Override
    public void onDisable() {
        try {
            service.connection.close();
            keyprefs.connection.close();
        } catch (SQLException e) {
            System.err.println("[TARDIS Shop Door] Could not close database connections: " + e);
        }
    }

    @Override
    public void onEnable() {
        plugin = this;
        saveDefaultConfig();
        loadDatabases();
        getServer().getPluginManager().registerEvents(new TardisShopDoorListener(this), this);
        Objects.requireNonNull(getCommand("tsd")).setExecutor(new TardisShopDoorCommands(this));
    }

    private void loadDatabases() {
        try {
            // doors database
            String path = getDataFolder() + File.separator + "doors.db";
            service.setConnection(path);
            service.createTables();
            // TARDIS dbatabase
            tardis = getServer().getPluginManager().getPlugin("TARDIS");
            assert tardis != null;
            String keyPath = tardis.getDataFolder() + File.separator + "TARDIS.db";
            keyprefs.setConnection(keyPath);
        } catch (Exception e) {
            System.err.println("[TARDIS Shop Door] Connection and Tables Error: " + e);
        }
    }

    public enum DIRECTION {

        EAST,
        SOUTH,
        WEST,
        NORTH
    }
}
