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
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author eccentric_nz
 */
public class TardisShopDoorQueries {

    TardisShopDoorDatabase service = TardisShopDoorDatabase.getInstance();
    Connection connection = service.getConnection();

    public TardisShopDoorQueries() {
    }

    public void doInsert(String name, String location, int type, String direction) {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement("INSERT INTO doors (name, location, type, direction) VALUES (?, ?, ?, ?)");
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, location);
            preparedStatement.setInt(3, type);
            preparedStatement.setString(4, direction);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[TARDIS Shop Door] Insert error for doors table! " + e.getMessage());
        } finally {
            try {

                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (SQLException e) {
                System.err.println("[TARDIS Shop Door] Error closing doors table! " + e.getMessage());
            }
        }
    }

    public void doDelete(String name) {
        Statement statement = null;
        String query = "DELETE FROM doors WHERE name = '" + name + "'";
        try {
            statement = connection.createStatement();
            statement.executeUpdate(query);
        } catch (SQLException e) {
            System.err.println("[TARDIS Shop Door] Delete error for doors table! " + e.getMessage());
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException e) {
                System.err.println("[TARDIS Shop Door] Error closing doors table! " + e.getMessage());
            }
        }
    }
}
