package org.vada.resources;

import org.vada.models.Project;
import org.vada.models.Task;
import org.vada.models.UserTask;

import java.sql.SQLException;
import java.util.List;

public class ProjectInfoResource {
    private final Project project;
    public ProjectInfoResource(Project project) throws SQLException {
        this.project = project;
        List<Task> taskList = Task.getByProjectId(project.getId());

        for (Task task: taskList) {
            task.setUserTasks(UserTask.getByTaskId(task.getId()));
        }

        project.setTasks(taskList);
    }

    public Project getProject() {
        return this.project;
    }
}
