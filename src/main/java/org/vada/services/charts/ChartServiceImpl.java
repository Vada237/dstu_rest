package org.vada.services.charts;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.vada.consts.Consts;
import org.vada.models.Project;
import org.vada.models.Task;
import org.vada.models.UserTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.vada.consts.Consts.*;

public class ChartServiceImpl implements ChartService {
    private final int CHART_WIDTH = 800;
    private final int CHART_HEIGHT = 600;

    @Override
    public byte[] drawBurndownChart(Project project) throws IOException, SQLException {
        long totalWorkInHours = project.getCountHours();

        XYDataset dataset = createDataset(project, totalWorkInHours);
        JFreeChart chart = createChart(dataset, project.getTitle());

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ChartUtils.writeChartAsPNG(outputStream, chart, CHART_WIDTH, CHART_HEIGHT);

        return outputStream.toByteArray();
    }

    private JFreeChart createChart(XYDataset dataset, String projectTitle) {
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "Burndown chart for project " + projectTitle,
                "Даты ведения проекта",
                "Количество оставшихся рабочих часов",
                dataset,
                true,
                true,
                false
        );

        XYPlot plot = chart.getXYPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesLinesVisible(0, true);
        renderer.setSeriesShapesVisible(0, false);
        plot.setRenderer(renderer);

        return chart;
    }

    private XYDataset createDataset(Project project, long totalWorkHours) throws SQLException {
        LocalDate startDate = project.getStartTime().toLocalDate();
        LocalDate finishDate = project.getFinishTime().toLocalDate();

        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(generateIdealLine(startDate, finishDate, totalWorkHours));
        dataset.addSeries(generateIterationLine(Task.getByProjectId(project.getId()), startDate, totalWorkHours));

        return dataset;
    }

    private TimeSeries generateIdealLine(LocalDate startDate, LocalDate finishDate, long totalWorkHours) {
        TimeSeries idealBurndown = new TimeSeries("Затраты по оценке проекта");

        long totalDays = startDate.until(finishDate).getDays();
        double dailyWorkHours = (double) totalWorkHours / totalDays;
        double remainingWorkHours = totalWorkHours;

        for (long day = 0; day <= totalDays; day++) {
            Date date = Date.from(startDate.plusDays(day).atStartOfDay(ZoneId.systemDefault()).toInstant());
            idealBurndown.add(new Day(date), remainingWorkHours);
            remainingWorkHours -= dailyWorkHours;
        }

        return idealBurndown;
    }


    private TimeSeries generateIterationLine(List<Task> taskList, LocalDate startingDate, double idealWorkingHours) throws SQLException {
        TimeSeries factBurndown = new TimeSeries("Фактические затраты");

        Map<LocalDate, Double> factWorkingHours = new TreeMap<>(getFactHWorkingHoursMapping(taskList));

        double remainingWorkingHours = idealWorkingHours;

        Day startDay = new Day(Date.from(startingDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        factBurndown.add(startDay, idealWorkingHours);

        for (Map.Entry<LocalDate, Double> entry : factWorkingHours.entrySet()) {
            Day iterDay = new Day(Date.from(entry.getKey().atStartOfDay(ZoneId.systemDefault()).toInstant()));
            remainingWorkingHours -= entry.getValue();
            factBurndown.addOrUpdate(iterDay, remainingWorkingHours);
        }

        return factBurndown;
    }

    public Map<LocalDate, Double> getFactHWorkingHoursMapping(List<Task> taskList) throws SQLException {
        Map<LocalDate, Double> factWorkedHours = new HashMap<LocalDate, Double>();

        for (Task task : taskList) {
            List<UserTask> userTasks = UserTask.getByTaskId(task.getId());

            for (UserTask userTask : userTasks) {
                LocalDate iterationDate = getNextMondayIfWeekend(
                        LocalDate.parse(userTask.getCreatedAt(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                );

                int trackedTime = userTask.getTrackedTime() / SECONDS_IN_HOURS;

                if (factWorkedHours.containsKey(iterationDate)) {
                    double currentValue = factWorkedHours.get(iterationDate);
                    factWorkedHours.put(iterationDate, currentValue + (double) trackedTime);
                } else {
                    factWorkedHours.put(iterationDate, (double) trackedTime);
                }
            }
        }

        return factWorkedHours;
    }

    private LocalDate getNextMondayIfWeekend(LocalDate date) {
        return switch (date.getDayOfWeek()) {
            case DayOfWeek.SATURDAY -> date.plusDays(2);
            case DayOfWeek.SUNDAY -> date.plusDays(1);
            default -> date;
        };
    }
}
