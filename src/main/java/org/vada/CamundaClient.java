package org.vada;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.repository.Deployment;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.springframework.core.io.ClassPathResource;
import org.vada.config.CamundaConfig;

import java.io.InputStream;

import static org.vada.Settings.*;

public class CamundaClient {
    public static void run() {
        ProcessEngine processEngine = CamundaConfig.createProcessEngine();

        // Загрузите ваши BPMN процессы
        try (InputStream resource = new ClassPathResource(PROCESS_FOLDER + "/" + BPMN_FILE).getInputStream()) {
            Deployment deployment = processEngine.getRepositoryService()
                    .createDeployment()
                    .addInputStream(BPMN_FILE, resource)
                    .deploy();
            System.out.println("Deployed " + deployment.getName() + " with ID: " + deployment.getId());

            // Запустите процесс
            RuntimeService runtimeService = processEngine.getRuntimeService();
            ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(PROCESS_ID);
            System.out.println("Started process instance: " + processInstance.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}