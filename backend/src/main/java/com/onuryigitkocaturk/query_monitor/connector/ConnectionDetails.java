package com.onuryigitkocaturk.query_monitor.connector;

// Project entity'sine bağımlı olmadan bağlantı bilgisini taşımak için var burası
public record ConnectionDetails(String host, int port, String databaseName, String username, String password) {

    public String toJdbcUrl() {
        return "jdbc:postgresql://" + host + ":" + port + "/" + databaseName;
    }
}
