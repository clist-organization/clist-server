package com.clist;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@SpringBootApplication
@EnableConfigurationProperties
public class Application {
    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(Application.class, args);

        // Node AI 서버 연결 확인
        String nodeServerUrl = ctx.getEnvironment().getProperty("ai.node-server-url", "http://localhost:3001");
        WebClient webClient = ctx.getBean(WebClient.class);

        try {
            webClient.get()
                    .uri(nodeServerUrl + "/health")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            log.info("Node AI 서버 연결 성공: {}", nodeServerUrl);
        } catch (Exception e) {
            log.warn("Node AI 서버 연결 실패: {} - {}", nodeServerUrl, e.getMessage());
        }
    }
}