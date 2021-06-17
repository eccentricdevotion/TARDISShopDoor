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

import me.eccentric_nz.tardisshopdoor.TardisShopDoorPlugin.DIRECTION;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author eccentric_nz
 */
public class TardisShopDoorResultSet {

    TardisShopDoorDatabase service = TardisShopDoorDatabase.getInstance();
    Connection connection = service.getConnection();

    public TardisShopDoorResultSet() {
    }

    public TardisShopDoorData resultSet(String location) {
        String query = "SELECT * FROM doors WHERE location = '" + location + "'";
        return getResults(query);
    }

    public TardisShopDoorData resultSet(String name, int type) {
        String query = "SELECT * FROM doors WHERE name = '" + name + "' AND type = '" + type + "'";
        return getResults(query);
    }

    private TardisShopDoorData getResults(String query) {
        int id;
        String name;
        String location;
        int type;
        DIRECTION direction;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();
            if (resultSet.isBeforeFirst()) {
                id = resultSet.getInt("id");
                name = resultSet.getString("name");
                type = resultSet.getInt("type");
                location = resultSet.getString("location");
                direction = DIRECTION.valueOf(resultSet.getString("direction"));
                return new TardisShopDoorData(id, name, location, type, direction);
            }
        } catch (SQLException e) {
            System.err.println("[TARDIS Shop Door] ResultSet error for doors table! " + e.getMessage());
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException e) {
                System.err.println("[TARDIS Shop Door] Error closing doors table! " + e.getMessage());
            }
        }
        return null;
    }
}
