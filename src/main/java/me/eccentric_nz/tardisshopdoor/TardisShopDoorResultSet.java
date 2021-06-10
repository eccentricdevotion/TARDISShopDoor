/*
 *  Copyright 2013 eccentric_nz.
 */
package me.eccentric_nz.tardisshopdoor;

import me.eccentric_nz.tardisshopdoor.TardisShopDoor.DIRECTION;

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
        ResultSet rs = null;
        try {
            statement = connection.prepareStatement(query);
            rs = statement.executeQuery();
            if (rs.isBeforeFirst()) {
                id = rs.getInt("id");
                name = rs.getString("name");
                type = rs.getInt("type");
                location = rs.getString("location");
                direction = DIRECTION.valueOf(rs.getString("direction"));
                return new TardisShopDoorData(id, name, location, type, direction);
            }
        } catch (SQLException e) {
            System.err.println("[TARDIS Shop Door] ResultSet error for doors table! " + e.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
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
