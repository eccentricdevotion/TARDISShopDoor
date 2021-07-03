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

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

/**
 * @author eccentric_nz
 */
public class TardisShopDoorKey {

    TardisPrefsDatabase service = TardisPrefsDatabase.getInstance();
    Connection connection = service.getConnection();
    FileConfiguration config;
    Material defaultKey;

    public TardisShopDoorKey(Plugin tardis) {
        String keyPath = tardis.getDataFolder() + File.separator + "config.yml";
        config = YamlConfiguration.loadConfiguration(new File(keyPath));
        defaultKey = Material.valueOf(config.getString("preferences.key").toUpperCase());
    }

    public Material getKeyPref(String uuid) {
        Material key = defaultKey;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        String query = "SELECT key FROM player_prefs WHERE uuid = '" + uuid + "'";
        try {
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();
            if (resultSet.isBeforeFirst()) {
                String theKey = resultSet.getString("key");
                key = (!theKey.equals("")) ? Material.valueOf(theKey) : defaultKey;
            }
        } catch (SQLException e) {
            System.err.println("[TARDIS Shop Door] ResultSet error for player_prefs table! " + e.getMessage());
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException e) {
                System.err.println("[TARDIS Shop Door] Error closing player_prefs table! " + e.getMessage());
            }
        }
        return key;
    }
}
