package org.vada.services.charts;

import org.vada.models.Project;

import java.io.IOException;
import java.sql.SQLException;

public interface ChartService {
    public byte[] drawBurndownChart(Project project) throws IOException, SQLException;
}
