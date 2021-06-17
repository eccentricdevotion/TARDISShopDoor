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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author eccentric_nz
 */
public class TardisShopDoorDatabase {

    private static final TardisShopDoorDatabase INSTANCE = new TardisShopDoorDatabase();
    public Connection connection = null;
    public Statement statement = null;

    public static synchronized TardisShopDoorDatabase getInstance() {
        return INSTANCE;
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
