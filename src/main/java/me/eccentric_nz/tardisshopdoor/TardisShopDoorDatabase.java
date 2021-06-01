/*
 *  Copyright 2013 eccentric_nz.
 */
package me.eccentric_nz.tardisshopdoor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author eccentric_nz
 */
public class TardisShopDoorDatabase {

	private static final TardisShopDoorDatabase instance = new TardisShopDoorDatabase();
	public Connection connection = null;
	public Statement statement = null;

	public static synchronized TardisShopDoorDatabase getInstance() {
		return instance;
	}

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(String path) throws Exception {
		Class.forName("org.sqlite.JDBC");
		connection = DriverManager.getConnection("jdbc:sqlite:" + path);
	}

	/**
	 * Creates the TARDIS default tables in the database.
	 */
	public void createTables() {
		try {
			statement = connection.createStatement();
			String queryDoors = "CREATE TABLE IF NOT EXISTS doors (id INTEGER PRIMARY KEY NOT NULL, name TEXT, location TEXT, type INTEGER, direction TEXT)";
			statement.executeUpdate(queryDoors);
		} catch (SQLException e) {
			System.err.println("[TARDIS Shop Door] Create table error: " + e);
		} finally {
			try {
				if (statement != null) {
					statement.close();
				}
			} catch (SQLException e) {
				System.err.println("[TARDIS Shop Door] Close statement error: " + e);
			}
		}
	}

	/**
	 * @return an exception
	 */
	@Override
	protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException("Clone is not allowed.");
	}
}
