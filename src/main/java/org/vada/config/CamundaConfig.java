package org.vada.config;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngineConfiguration;

import static org.vada.Settings.*;

public class CamundaConfig {
    public static ProcessEngine createProcessEngine() {
        ProcessEngineConfiguration configuration = ProcessEngineConfiguration.createStandaloneProcessEngineConfiguration();

        configuration.setJdbcUrl(DB_URL);
        configuration.setJdbcDriver("org." + DRIVER + ".Driver");
        configuration.setJdbcUsername(USER);
        configuration.setJdbcPassword(PASSWORD);
        configuration.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);

        return configuration.buildProcessEngine();
    }
}