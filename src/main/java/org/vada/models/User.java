package org.vada.models;

import org.vada.managers.PostgresManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class User extends Model {
    public static String tableName = "users";
    private int Id;
    private String firstName;
    private String secondName;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public User() {

    }

    public User(String firstName, String secondName) {
        this.firstName = firstName;
        this.secondName = secondName;
    }

    @Override
    public String toString() {
        return "User{" +
                "Id=" + Id +
                ", firstName='" + firstName + '\'' +
                ", secondName='" + secondName + '\'' +
                '}';
    }

    public static void insert(Map<String, Object> params) throws SQLException {
        Model.insert(params, tableName);
    }

    public static List<User> all() throws SQLException {
        return getCollection(Model.all(tableName));
    }

    public static User getById(int id) throws SQLException {
        return getCollection(Model.getById(id, tableName)).get(0);
    }

    public static void deleteById(int id) throws SQLException {
        Model.deleteById(id, tableName);
    }

    public static List<User> getByFirstAndSecondName(String firstName, String secondName) throws SQLException {
        Object[] params = new Object[2];
        params[0] = firstName;
        params[1] = secondName;

        return getCollection(PostgresManager.executeSelect(
                "SELECT * FROM " + tableName + " WHERE first_name = ? AND second_name = ?", params)
        );
    }

    private static List<User> getCollection(ResultSet data) throws SQLException {
        ArrayList<User> users = new ArrayList<>();

        while (data.next()) {
            User user = new User(data.getString("first_name"), data.getString("second_name"));
            user.setId(data.getInt("id"));
            users.add(user);
        }

        return users;
    }
}
