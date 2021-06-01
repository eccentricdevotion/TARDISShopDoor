/*
 *  Copyright 2013 eccentric_nz.
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
	Material defaultkey;

	public TardisShopDoorKey(Plugin tardis) {
		String keypath = tardis.getDataFolder() + File.separator + "config.yml";
		config = YamlConfiguration.loadConfiguration(new File(keypath));
		defaultkey = Material.valueOf(Objects.requireNonNull(config.getString("preferences.key")).toUpperCase());
	}

	public Material getKeyPref(String uuid) {
		Material key = defaultkey;
		PreparedStatement statement = null;
		ResultSet rs = null;
		String query = "SELECT key FROM player_prefs WHERE uuid = '" + uuid + "'";
		try {
			statement = connection.prepareStatement(query);
			rs = statement.executeQuery();
			if (rs.isBeforeFirst()) {
				String thekey = rs.getString("key");
				key = (!thekey.equals("")) ? Material.valueOf(thekey) : defaultkey;
			}
		} catch (SQLException e) {
			System.err.println("[TARDIS Shop Door] ResultSet error for player_prefs table! " + e.getMessage());
		} finally {
			try {
				if (rs != null) {
					rs.close();
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
