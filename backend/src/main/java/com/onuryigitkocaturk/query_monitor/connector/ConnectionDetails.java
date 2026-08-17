package com.onuryigitkocaturk.query_monitor.connector;

import com.onuryigitkocaturk.query_monitor.enums.DatabaseType;

// Project entity'sine bağımlı olmadan bağlantı bilgisini taşımak için var burası
public record ConnectionDetails(DatabaseType type, String host, int port, String databaseName,
                                 String username, String password) {

    // JDBC driver'ın bağlanabilmesi için host/port/dbName/tip bilgisini tek bir standart string'e (URL)
    // çevirmemiz gerekiyor, bu metod da o çeviriyi tek bir yerde yapıyor.
    public String toJdbcUrl() {
        return switch (type) {
            case POSTGRESQL -> "jdbc:postgresql://" + host + ":" + port + "/" + databaseName;
            case MYSQL -> "jdbc:mysql://" + host + ":" + port + "/" + databaseName;
            case MSSQL -> "jdbc:sqlserver://" + host + ":" + port + ";databaseName=" + databaseName
                    + ";encrypt=false";
        };
    }

    public String driverClassName() {
        return switch (type) {
            case POSTGRESQL -> "org.postgresql.Driver";
            case MYSQL -> "com.mysql.cj.jdbc.Driver";
            case MSSQL -> "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        };
    }

    // veritabanlarında varsayılan şema kavramı farklı
    public String metadataSchema() {
        return switch (type) {
            case POSTGRESQL -> "public";
            case MYSQL -> databaseName;
            case MSSQL -> "dbo";
        };
    }
}
