package com.onuryigitkocaturk.query_monitor.connector;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;

// projenin bağlantı bilgilerini alıp, o veritabanına anlık olarak bağlanabilecek bir JdbcTemplate
// üretiyor. her çağrı sıfırdan, pooling yok.

// sabit tek bir @Bean DataSource yerine bunun factory olarak tasarlanmasının sebebi:
// her proje farklı bir veritabanına bağlanabildiği için, Spring'in başlangıçta
// sabitleyebileceği tek bir DataSource yok
// hangi projenin sorgusu çalışıyorsa,
// TableMetadataService/QueryExecutionService o an
// o projenin ConnectionDetails'ini bu factory'ye verip,
// ihtiyaç anında kendi DataSource/JdbcTemplate'ini üretiyor.
@Component
public class JdbcTemplateFactory {

    public JdbcTemplate create(ConnectionDetails connection) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource(); // pool'suz, her seferinde yeni bağlantı açan bir DataSource oluşturuyor.

        // connectionDetails'ten gelen bilgilerle bu DataSource'u dolduruyor,şifre zaten çözülmüş halde geliyor.
        dataSource.setUrl(connection.toJdbcUrl());
        dataSource.setUsername(connection.username());
        dataSource.setPassword(connection.password());

        dataSource.setDriverClassName(connection.driverClassName()); // veritabanı tipine göre doğru JDBC driver'ı seçiliyor.

        return new JdbcTemplate(dataSource);    // bu DataSource üzerine kurulu bir JdbcTemplate döndürüyor,
                                                // TableMetadataService/QueryExecutionService bunu kullanıyor.
    }
}
