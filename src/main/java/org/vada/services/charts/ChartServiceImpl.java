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
import org.vada.models.Project;
import org.vada.models.Task;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.List;

import static org.vada.consts.Consts.DEFAULT_WORK_HOURS;
import static org.vada.consts.Consts.HOURS_IN_DAY;

public class ChartServiceImpl implements ChartService {
    private final int CHART_WIDTH = 800;
    private final int CHART_HEIGHT = 600;

    @Override
    public byte[] drawBurndownChart(Project project) throws IOException, SQLException {
        long totalWorkInHours = getProjectWorkedTime(project.getStartTime(), project.getFinishTime());

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
                "Количество затраченных рабочих часов",
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
    private long getProjectWorkedTime(LocalDateTime startTime, LocalDateTime endTime) {
        long TotalWorkInHours = Duration.between(startTime, endTime).toHours();

        LocalDate startDate = startTime.toLocalDate();
        LocalDate endDate = endTime.toLocalDate();

        long sundays = startDate.until(endDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)), ChronoUnit.WEEKS);
        long saturdays = startDate.until(endDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY)), ChronoUnit.WEEKS);

        return (
                (long) (TotalWorkInHours * (float) DEFAULT_WORK_HOURS / (float) HOURS_IN_DAY) -
                        (long) (((saturdays + sundays) * HOURS_IN_DAY) * ((float) DEFAULT_WORK_HOURS / (float) HOURS_IN_DAY))
        );
    }

    private XYDataset createDataset(Project project, long totalWorkHours) throws SQLException {
        LocalDate startDate = project.getStartTime().toLocalDate();
        LocalDate finishDate = project.getFinishTime().toLocalDate();

        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(generateIdealLine(startDate, finishDate, totalWorkHours));
        dataset.addSeries(generateIterationLine(Task.getByProjectId(project.getId())));

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

    private TimeSeries generateIterationLine(List<Task> taskList) {
        return new TimeSeries("Фактические затраты");
    };
}
