package org.vada.services.reports;

import jakarta.ws.rs.core.Response;
import org.vada.Settings;
import org.vada.enums.TaskStatus;
import org.vada.models.Project;
import org.vada.models.Task;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.vada.resources.ProjectInfoResource;
import org.vada.services.charts.ChartService;
import org.vada.services.charts.ChartServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Path("reports")
public class ReportService{
    private final ChartService chartService;
    public ReportService() {
        this.chartService = new ChartServiceImpl();
    }

    @GET
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
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @Path("/burndown-chart/{projectId}")
    public Response generateChart(@PathParam("projectId") int projectId) throws Exception {
        Project project = Project.getById(projectId);

        if (project == null) {
            throw new Exception(String.valueOf(Settings.HTTP_RESPONSE_NOT_FOUND));
        }

        String filename = "burndown-chart-" + project.getTitle() + ".png";
        byte[] chart = this.chartService.drawBurndownChart(project);
        return Response
                .ok(chart)
                .header("Content-Disposition", "inline; filename=" + filename)
                .header("Content-Type", "image/png")
                .build();
    }
}