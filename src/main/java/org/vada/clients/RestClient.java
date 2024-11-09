package org.vada.clients;

import org.springframework.web.client.RestTemplate;
import org.vada.resources.ProjectInfoResource;

import static org.vada.Settings.BASE_URI;

public class RestClient {
    public static void main(String[] args) {
        RestTemplate restTemplate = new RestTemplate();

        String projectInfo = restTemplate.getForObject(BASE_URI + "reports/info/1", String.class);
        System.out.println(projectInfo);
    }
}
