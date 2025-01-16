package com.slpixe.springshorturl;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {

    private static final DockerImageName POSTGRES_IMAGE = DockerImageName.parse("postgres:latest");

    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgresContainer() {
        PostgreSQLContainer<?> container = new PostgreSQLContainer<>(POSTGRES_IMAGE);
        container.start();

        // Log container details after starting
        System.out.println("PostgreSQL Container Details:");
        System.out.println("JDBC URL: " + container.getJdbcUrl());
        System.out.println("Username: " + container.getUsername());
        System.out.println("Password: " + container.getPassword());
        System.out.println("Database Name: " + container.getDatabaseName());

        return container;
    }
}
