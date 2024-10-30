package org.vada.models;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Project extends Model{
    public static String tableName = "projects";
    private int id;
    private String title;
    private List<Task> tasks;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public Project(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public Project() {

    }

    @Override
    public String toString() {
        return "Project{" +
                "id=" + id +
                ", title='" + title;
    }

    public static void insert(Map<String, Object> params) throws SQLException {
        Model.insert(params, tableName);
    }

    public static Project getById(int id) throws SQLException {
        return getCollection(Model.getById(id, tableName)).get(0);
    }

    public static List<Project> all() throws SQLException {
        return getCollection(Model.all(tableName));
    }

    public static void delete(int id) throws SQLException {
        Model.deleteById(id, tableName);
    }

    private static List<Project> getCollection(ResultSet data) throws SQLException {
        ArrayList<Project> projects = new ArrayList<>();

        while (data.next()) {
            projects.add(new Project(data.getInt("id"), data.getString("title")));
        }

        return projects;
    }
}
