package me.eccentric_nz.tardisshopdoor;

import java.io.File;
import java.sql.SQLException;
import java.util.HashMap;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class TardisShopDoor extends JavaPlugin {

    public static TardisShopDoor plugin;
    TardisShopDoorDatabase service = TardisShopDoorDatabase.getInstance();
    TardisPrefsDatabase keyprefs = TardisPrefsDatabase.getInstance();
    public HashMap<String, TardisShopDoorAdd> trackAdd = new HashMap<String, TardisShopDoorAdd>();
    public Plugin tardis;

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
        getCommand("tsd").setExecutor(new TardisShopDoorCommands(this));
    }

    private void loadDatabases() {
        try {
            // doors database
            String path = getDataFolder() + File.separator + "doors.db";
            service.setConnection(path);
            service.createTables();
            // TARDIS dbatabase
            tardis = getServer().getPluginManager().getPlugin("TARDIS");
            String keypath = tardis.getDataFolder() + File.separator + "TARDIS.db";
            keyprefs.setConnection(keypath);
        } catch (Exception e) {
            System.err.println("[TARDIS Shop Door] Connection and Tables Error: " + e);
        }
    }

    public enum DIRECTION {

        EAST, SOUTH, WEST, NORTH;
    }
}
