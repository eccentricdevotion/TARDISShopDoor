package me.eccentric_nz.tardisshopdoor;

import java.io.File;
import java.sql.SQLException;
import java.util.HashMap;
import org.bukkit.plugin.java.JavaPlugin;

public class TardisShopDoor extends JavaPlugin {

    public static TardisShopDoor plugin;
    TardisShopDoorDatabase service = TardisShopDoorDatabase.getInstance();
    public HashMap<String, TardisShopDoorAdd> trackAdd = new HashMap<String, TardisShopDoorAdd>();

    @Override
    public void onDisable() {
        try {
            service.connection.close();
        } catch (SQLException e) {
            System.err.println("[TARDIS Shop Door] Could not close database connection: " + e);
        }
    }

    @Override
    public void onEnable() {
        plugin = this;
        saveDefaultConfig();
        loadDatabase();
        getServer().getPluginManager().registerEvents(new TardisShopDoorListener(this), this);
        getCommand("tsd").setExecutor(new TardisShopDoorCommands(this));
    }

    private void loadDatabase() {
        try {
            String path = getDataFolder() + File.separator + "doors.db";
            service.setConnection(path);
            service.createTables();
        } catch (Exception e) {
            System.err.println("[TARDIS Shop Door] Connection and Tables Error: " + e);
        }
    }

    public enum DIRECTION {

        EAST, SOUTH, WEST, NORTH;
    }
}
