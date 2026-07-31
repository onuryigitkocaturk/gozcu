package com.onuryigitkocaturk.query_monitor.connector;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

// monitored_db'ye bağlanan ayrı bir DataSource + JdbcTemplate bean'i kuruyor, ana uygulama DB'sinden bağımsız.
@Configuration
public class MonitoredDataSourceConfig {

    @Bean(name = "monitoredDataSource")
    public DataSource monitoredDataSource(
            @Value("${monitored.datasource.url}") String url,
            @Value("${monitored.datasource.username}") String username,
            @Value("${monitored.datasource.password}") String password,
            @Value("${monitored.datasource.driver-class-name}") String driverClassName) {

        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setDriverClassName(driverClassName);
        return dataSource;
    }

    @Bean(name = "monitoredJdbcTemplate")
    public JdbcTemplate monitoredJdbcTemplate(@Qualifier("monitoredDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
