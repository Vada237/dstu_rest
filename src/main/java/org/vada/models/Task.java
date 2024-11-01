package org.vada.models;

import org.vada.managers.PostgresManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Task extends Model {
    public static String tableName = "tasks";
    private int id;
    private String title;
    private String startTime;
    private String finishTime;
    private String status;
    private User user;
    private int userId;
    private Project project;
    private int projectId;
    private int totalProgress;
    private List<UserTask> userTasks;
    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(String finishTime) {
        this.finishTime = finishTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public List<UserTask> getUserTasks() {
        return userTasks;
    }

    public void setUserTasks(List<UserTask> userTasks) {
        this.userTasks = userTasks;
    }

    public int getTotalProgress() {
        return this.totalProgress;
    }

    public void setTotalProgress(int totalProgress) {
        this.totalProgress = totalProgress;

        if (this.totalProgress > 100) {
            this.totalProgress = 100;
        }
    }

    public Task(
            String title,
            String startTime,
            String finishTime,
            String status,
            User user,
            Project project,
            int totalProgress
    ) {
        this.title = title;
        this.startTime = startTime;
        this.finishTime = finishTime;
        this.status = status;
        this.user = user;
        this.userId = user.getId();
        this.project = project;
        this.projectId = project.getId();        
        this.totalProgress = totalProgress;
    }

    public Task() {
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", startTime=" + startTime +
                ", finishTime=" + finishTime +
                ", status='" + status + '\'' +
                ", user=" + user +
                ", project=" + project +
                ", total_progress=" + totalProgress +
                '}';
    }

    public static void insert(Map<String, Object> params) throws SQLException {
        Model.insert(params, tableName);
    }

    public static List<Task> all() throws SQLException {
        return getCollection(Model.all(tableName));
    }

    public static void deleteById(int id) throws SQLException {
        Model.deleteById(id, tableName);
    }

    public static Task getById(int id) throws SQLException {
        List<Task> tasks = getCollection(Model.getById(id, tableName));
        if (tasks.isEmpty()) {
            return null;
        }

        return tasks.get(0);
    }

    public static List<Task> getByUserId(int userId) throws SQLException {
        Object[] params = new Object[1];
        params[0] = userId;

        return getCollection(PostgresManager.executeSelect("SELECT * FROM " + tableName + " WHERE current_user_id = ?", params));
    }

    public static List<Task> getByProjectId(int projectId) throws SQLException {
        Object[] params = new Object[1];
        params[0] = projectId;

        return getCollection(PostgresManager.executeSelect("SELECT * FROM " + tableName + " WHERE project_id = ?", params));
    }

    public static List<Task> getCollection(ResultSet data) throws SQLException {
        List<Task> tasks = new ArrayList<Task>();

        while (data.next()) {
            Project project = Project.getById(data.getInt("project_id"));
            User currentUser = User.getById(data.getInt("current_user_id"));

            Task task = new Task(
                    data.getString("title"),
                    data.getString("start_time"),
                    data.getString("end_time"),
                    data.getString("status"),
                    currentUser,
                    project,
                    data.getInt("total_progress")
                    );
            task.setId(data.getInt("id"));
            tasks.add(task);
        }

        return tasks;
    }

    public static void UpdateProgress(Task task, int progress) throws SQLException {
        Object[] params = new Object[2];
        task.setTotalProgress(task.getTotalProgress() + progress);
        
        params[0] = task.getTotalProgress();
        params[1] = task.getId();

        PostgresManager.executeUpdate("UPDATE " + tableName + " SET total_progress = ? WHERE id = ?", params);
    }

    public static List<Task> getByTitle(String title) throws SQLException {
        return getCollection(PostgresManager.executeSelect("SELECT * FROM " + tableName + " WHERE title ilike '%" + title + "%'"));
    }

    public static void updateStatus(int taskId, String status) throws SQLException {
        Object[] params = new Object[2];
        params[0] = status;
        params[1] = taskId;
        PostgresManager.executeUpdate("UPDATE " + tableName + " SET status = ? WHERE id = ?", params);
    }
}

