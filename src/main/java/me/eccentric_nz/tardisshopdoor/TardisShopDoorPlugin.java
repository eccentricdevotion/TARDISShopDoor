/*
 * Copyright (C) 2021 eccentric_nz
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package me.eccentric_nz.tardisshopdoor;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class TardisShopDoorPlugin extends JavaPlugin {

    public static TardisShopDoorPlugin plugin;
    public HashMap<UUID, TardisShopDoorAdd> trackAdd = new HashMap<>();
    public Plugin tardis;
    TardisShopDoorDatabase service = TardisShopDoorDatabase.getInstance();
    TardisPrefsDatabase keyPrefs = TardisPrefsDatabase.getInstance();

    @Override
    public void onDisable() {
        try {
            service.connection.close();
            keyPrefs.connection.close();
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
            // TARDIS database
            tardis = getServer().getPluginManager().getPlugin("TARDIS");
            assert tardis != null;
            String keyPath = tardis.getDataFolder() + File.separator + "TARDIS.db";
            keyPrefs.setConnection(keyPath);
        } catch (Exception e) {
            System.err.println("[TARDIS Shop Door] Connection and Tables Error: " + e);
        }
    }

    public enum CardinalDirection {

        EAST,
        SOUTH,
        WEST,
        NORTH
    }
}
