package com.onuryigitkocaturk.query_monitor.connector;

// Izlenen bir veritabanina baglanmak icin gereken ham bilgiler. connector
// paketi JPA/entity'lerden bagimsiz kalmali - bu yuzden Project entity'si
// degil, bu duz record kullanilir.
public record ConnectionDetails(String host, int port, String databaseName, String username, String password) {

    public String toJdbcUrl() {
        return "jdbc:postgresql://" + host + ":" + port + "/" + databaseName;
    }
}
