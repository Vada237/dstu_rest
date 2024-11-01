package org.vada.models;


import org.vada.managers.PostgresManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public abstract class Model {
    public static String tableName = "";

    public static void insert(Map<String, Object> params, String tableName) throws SQLException {
        String fields = String.join(", ", params.keySet());
        String[] paramKeys = new String[params.size()];

        for (int i = 0; i < params.size(); i++) {
            paramKeys[i] = "?";
        }

        String paramQueryPart = String.join(", ", paramKeys);
        String query = "INSERT INTO " + tableName + " (" + fields + ") VALUES " + "(" + paramQueryPart + ")";
        PostgresManager.executeUpdate(query, params.values().toArray());
    }

    public static ResultSet all(String tableName) throws SQLException {
        return PostgresManager.executeSelect("SELECT * FROM " + tableName);
    }

    public static ResultSet getById(int id, String tableName) throws SQLException {
        return PostgresManager.executeSelect("SELECT * FROM " + tableName + " WHERE id = ?", Model.getParamId(id));
    }

    public static void deleteById(int id, String tableName) throws SQLException {
        PostgresManager.executeUpdate("DELETE FROM " + tableName + " WHERE id = ?", Model.getParamId(id));
    };

    private static Object[] getParamId(int id) {
        Object[] params = new Object[1];
        params[0] = id;
        return params;
    }
}
