package org.vada.managers;

import org.vada.Settings;

import java.sql.*;

public class PostgresManager {
    public static Connection conn = PostgresManager.connect();

    public static Connection connect() {
        try {
            return DriverManager.getConnection(Settings.URL, Settings.USER, Settings.PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static ResultSet executeSelect(String query) throws SQLException {
        PreparedStatement statement = conn.prepareStatement(query);
        return statement.executeQuery();
    }

    public static ResultSet executeSelect(String query, Object[] params) throws SQLException {
        PreparedStatement statement = conn.prepareStatement(query);
        setParams(params, statement);
        return statement.executeQuery();
    }

    public static void executeUpdate(String query) throws SQLException {
        PreparedStatement statement = conn.prepareStatement(query);
        statement.executeUpdate();
    }

    public static void executeUpdate(String query, Object[] params) throws SQLException {
        PreparedStatement statement = conn.prepareStatement(query);
        setParams(params, statement);
        statement.executeUpdate();
    }

    private static void setParams(Object[] params, PreparedStatement statement) throws SQLException {
        for (int i = 1; i < params.length + 1; i++) {
            statement.setObject(i, params[i-1]);
        }
    }
}
