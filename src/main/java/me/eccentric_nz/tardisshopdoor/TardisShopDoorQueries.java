/*
 *  Copyright 2013 eccentric_nz.
 */
package me.eccentric_nz.tardisshopdoor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author eccentric_nz
 */
public class TardisShopDoorQueries {

    TardisShopDoorDatabase service = TardisShopDoorDatabase.getInstance();
    Connection connection = service.getConnection();

    public TardisShopDoorQueries() {
    }

    public void doInsert(String name, String location, int type, String direction) {
        PreparedStatement ps = null;
        try {
            ps = connection.prepareStatement("INSERT INTO doors (name, location, type, direction) VALUES (?, ?, ?, ?)");
            ps.setString(1, name);
            ps.setString(2, location);
            ps.setInt(3, type);
            ps.setString(4, direction);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[TARDIS Shop Door] Insert error for doors table! " + e.getMessage());
        } finally {
            try {

                if (ps != null) {
                    ps.close();
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
