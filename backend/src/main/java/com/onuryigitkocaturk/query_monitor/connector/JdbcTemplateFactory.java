package com.onuryigitkocaturk.query_monitor.connector;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;

// Her proje kendi izlenen veritabanina (farkli host/port/db olabilir)
// bağlanabildiği için, artık sabit tek bir DataSource bean'i yeterli değil.
// Bu sınıf, verilen ConnectionDetails'e gore RUNTIME'da bir JdbcTemplate
// üretir - pool'lamaz (DriverManagerDataSource), çünkü izlenen DB'lere
// erişim yoğun degil.
@Component
public class JdbcTemplateFactory {

    public JdbcTemplate create(ConnectionDetails connection) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource(); // pool'suz, her seferinde yeni bağlantı açan bir DataSource oluşturuyor.

        // ConnectionDetails'ten gelen bilgilerle bu DataSource'u dolduruyor (şifre zaten çözülmüş halde geliyor).
        dataSource.setUrl(connection.toJdbcUrl());
        dataSource.setUsername(connection.username());
        dataSource.setPassword(connection.password());

        dataSource.setDriverClassName("org.postgresql.Driver"); // PostgreSQL JDBC driver'ını kullanacağını belirtiyor.

        return new JdbcTemplate(dataSource);    // bu DataSource üzerine kurulu bir JdbcTemplate döndürüyor,
                                                // TableMetadataService/QueryExecutionService bunu kullanıyor.
    }
}
