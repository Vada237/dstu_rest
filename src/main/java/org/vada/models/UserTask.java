package org.vada.models;

import org.vada.managers.PostgresManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class UserTask extends Model{
    public static String tableName = "user_tasks";

    private int id;
    private User user;
    private int userId;
    private Task task;
    private int taskId;
    private int trackedTime;
    private Date createdAt;
    private Date updatedAt;
    private int totalProgress;

    public int getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Task getTask() {
        return task;
    }

    public int getTrackedTime() {
        return trackedTime;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public void setTrackedTime(int trackedTime) {
        this.trackedTime = trackedTime;
    }

    public int getTotalProgress() {
        return this.totalProgress;
    }

    public void setTotalProgress(int totalProgress) {
        this.totalProgress = totalProgress;
    }

    public UserTask(int id, int userId, int taskId, int trackedTime, int totalProgress) {
        this.id = id;
        this.userId = userId;
        this.taskId = taskId;
        this.trackedTime = trackedTime;
        this.totalProgress = totalProgress;
    }

    public static void insert(Map<String, Object> params) throws SQLException {
        Model.insert(params, tableName);
    }

    public static List<UserTask> all() throws SQLException {
        return getCollection(Model.all(tableName));
    }

    public static UserTask getById(int id) throws SQLException {
        return getCollection(Model.getById(id, tableName)).get(0);
    }

    public static void delete(int id) throws SQLException {
        Model.deleteById(id, tableName);
    }

    public static List<UserTask> getByTaskId(int taskId) throws SQLException {
        Object[] params = new Object[1];
        params[0] = taskId;

        return getCollection(PostgresManager.executeSelect("SELECT * FROM " + tableName + " WHERE task_id = ?", params));
    }
    private static List<UserTask> getCollection(ResultSet data) throws SQLException {
        List<UserTask> userTasks = new ArrayList<>();

        while (data.next()) {
            userTasks.add(new UserTask(
                    data.getInt("id"),
                    data.getInt("user_id"),
                    data.getInt("task_id"),
                    data.getInt("tracked_time"),
                    data.getInt("total_progress")
            ));
        }

        return userTasks;
    }
}
