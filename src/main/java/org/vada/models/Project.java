package org.vada.models;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Project extends Model{
    public static String tableName = "projects";
    private int id;
    private String title;
    private LocalDateTime startTime;
    private LocalDateTime finishTime;

    private int countHours;
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

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(LocalDateTime finishTime) {
        this.finishTime = finishTime;
    }

    public int getCountHours() {
        return countHours;
    }

    public void setCountHours(int countHours) {
        this.countHours = countHours;
    }

    public Project(int id, String title, LocalDateTime startTime, LocalDateTime finishTime, int totalHours) {
        this.id = id;
        this.title = title;
        this.startTime = startTime;
        this.finishTime = finishTime;
        this.countHours = totalHours;
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
            projects.add(new Project(
                    data.getInt("id"),
                    data.getString("title"),
                    data.getTimestamp("start_time").toLocalDateTime(),
                    data.getTimestamp("end_time").toLocalDateTime(),
                    data.getInt("count_hours")
            ));
        }

        return projects;
    }
}
