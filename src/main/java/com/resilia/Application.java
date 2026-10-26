package com.resilia;

import com.vaadin.flow.component.page.AppShellConfigurator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import com.vaadin.flow.theme.aura.Aura;
import com.vaadin.flow.component.dependency.StyleSheet;

@SpringBootApplication
@EnableAsync
@StyleSheet(Aura.STYLESHEET)
@StyleSheet("styles.css")
@StyleSheet(Aura.STYLESHEET)
@StyleSheet("styles.css")
public class Application implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
