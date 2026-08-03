package com.onuryigitkocaturk.query_monitor.connector;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;

// Her proje kendi izlenen veritabanina (farkli host/port/db olabilir)
// baglanabildigi icin, artik sabit tek bir DataSource bean'i yeterli degil.
// Bu sinif, verilen ConnectionDetails'e gore RUNTIME'da bir JdbcTemplate
// uretir - pool'lamaz (DriverManagerDataSource), cunku izlenen DB'lere
// erisim sik/yogun degil.
@Component
public class JdbcTemplateFactory {

    public JdbcTemplate create(ConnectionDetails connection) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(connection.toJdbcUrl());
        dataSource.setUsername(connection.username());
        dataSource.setPassword(connection.password());
        dataSource.setDriverClassName("org.postgresql.Driver");
        return new JdbcTemplate(dataSource);
    }
}
