package com.spreadsheet.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DatabaseConfig {

    @Bean
    @ConfigurationProperties("spring.datasource")
    public DataSource writeDataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        return dataSource;
    }

    @Bean
    public DataSource readDataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:postgresql://localhost:5433/spreadsheet_db");
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUsername("spreadsheet_user");
        dataSource.setPassword("spreadsheet_password");
        dataSource.setMaximumPoolSize(10);
        dataSource.setMinimumIdle(2);
        return dataSource;
    }

    @Bean
    public DataSource shard0DataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:postgresql://localhost:5433/spreadsheet_db_shard_0");
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUsername("spreadsheet_user");
        dataSource.setPassword("spreadsheet_password");
        dataSource.setMaximumPoolSize(10);
        dataSource.setMinimumIdle(2);
        return dataSource;
    }

    @Bean
    public DataSource shard1DataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:postgresql://localhost:5433/spreadsheet_db_shard_1");
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUsername("spreadsheet_user");
        dataSource.setPassword("spreadsheet_password");
        dataSource.setMaximumPoolSize(10);
        dataSource.setMinimumIdle(2);
        return dataSource;
    }

    @Bean
    public DataSource shard2DataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:postgresql://localhost:5433/spreadsheet_db_shard_2");
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUsername("spreadsheet_user");
        dataSource.setPassword("spreadsheet_password");
        dataSource.setMaximumPoolSize(10);
        dataSource.setMinimumIdle(2);
        return dataSource;
    }

    @Bean
    public DataSource shard3DataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:postgresql://localhost:5433/spreadsheet_db_shard_3");
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUsername("spreadsheet_user");
        dataSource.setPassword("spreadsheet_password");
        dataSource.setMaximumPoolSize(10);
        dataSource.setMinimumIdle(2);
        return dataSource;
    }

    @Bean
    @Primary
    public DataSource routingDataSource() {
        RoutingDataSource routingDataSource = new RoutingDataSource();
        
        Map<Object, Object> dataSourceMap = new HashMap<>();
        dataSourceMap.put(DataSourceType.WRITE, writeDataSource());
        dataSourceMap.put(DataSourceType.READ, readDataSource());
        dataSourceMap.put("SHARD_0", shard0DataSource());
        dataSourceMap.put("SHARD_1", shard1DataSource());
        dataSourceMap.put("SHARD_2", shard2DataSource());
        dataSourceMap.put("SHARD_3", shard3DataSource());
        
        routingDataSource.setTargetDataSources(dataSourceMap);
        routingDataSource.setDefaultTargetDataSource(writeDataSource());
        
        return routingDataSource;
    }
}
