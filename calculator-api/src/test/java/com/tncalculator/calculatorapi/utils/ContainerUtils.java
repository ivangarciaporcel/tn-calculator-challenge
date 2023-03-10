package com.tncalculator.calculatorapi.utils;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

public class ContainerUtils {

    private ContainerUtils(){}

    public static PostgreSQLContainer postgreSQLContainer() {
        return new PostgreSQLContainer(
                DockerImageName.parse("postgres").withTag("13.7-alpine3.16"))
                .withDatabaseName("tn_calculator")
                .withUsername("postgres")
                .withPassword("postgres");
    }

}
