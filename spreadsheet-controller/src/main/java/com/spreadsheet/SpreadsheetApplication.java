package com.spreadsheet;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.spreadsheet")
@EntityScan(basePackages = "com.spreadsheet.repository.entity")
@EnableJpaRepositories(basePackages = "com.spreadsheet.repository")
@OpenAPIDefinition(
    info = @Info(
        title = "Spreadsheet API",
        version = "1.0.0",
        description = "API for managing spreadsheets and their data"
    )
)
public class SpreadsheetApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpreadsheetApplication.class, args);
    }
}
