/*
 *  Copyright 2013 eccentric_nz.
 */
package me.eccentric_nz.tardisshopdoor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * @author eccentric_nz
 */
public class TardisPrefsDatabase {

    private static final TardisPrefsDatabase instance = new TardisPrefsDatabase();
    public Connection connection = null;
    public Statement statement = null;

    public static synchronized TardisPrefsDatabase getInstance() {
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
     * @return an exception
     */
    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("Clone is not allowed.");
    }
}
