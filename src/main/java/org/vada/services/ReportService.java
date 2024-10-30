package org.vada.services;

import org.vada.Settings;
import org.vada.enums.TaskStatus;
import org.vada.models.Project;
import org.vada.models.Task;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.vada.resources.ProjectInfoResource;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Path("reports")
public class ReportService{
    @GET
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/info/{projectId}")
    public ProjectInfoResource getProjectInfo(@PathParam("projectId") int projectId) throws Exception {
        Project project = Project.getById(projectId);

        if (project == null) {
            throw new Exception(String.valueOf(Settings.HTTP_RESPONSE_NOT_FOUND));
        }

        return new ProjectInfoResource(Project.getById(projectId));
    }

    @GET
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/expired-task/{projectId}")
    public List<Task> getExpiredTask(@PathParam("projectId") int projectId) throws Exception {
        List<Task> tasks = Task.getByProjectId(projectId);
        List<Task> expiredTasks = new ArrayList<>();

        for (Task task: tasks) {
            if (Objects.equals(task.getStatus(), TaskStatus.EXPIRED.name())) {
                expiredTasks.add(task);
            }
        }

        return expiredTasks;
    }

    @GET
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_HTML)
    public String generateChart(int projectId) {
        return null;
    }
}