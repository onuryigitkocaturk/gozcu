package com.onuryigitkocaturk.query_monitor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// Bir proje kaydedilmeden ONCE, girilen baglanti bilgisiyle gercekten
// baglanilabiliyor mu diye test etmek icin kullanilir - hicbir yere
// kaydedilmez, sadece o istek suresince kullanilir.
public class ConnectionTestRequest {

    @NotBlank
    private String dbHost;

    @NotNull
    private Integer dbPort;

    @NotBlank
    private String dbName;

    @NotBlank
    private String dbUsername;

    @NotBlank
    private String dbPassword;

    public String getDbHost() {
        return dbHost;
    }

    public void setDbHost(String dbHost) {
        this.dbHost = dbHost;
    }

    public Integer getDbPort() {
        return dbPort;
    }

    public void setDbPort(Integer dbPort) {
        this.dbPort = dbPort;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getDbUsername() {
        return dbUsername;
    }

    public void setDbUsername(String dbUsername) {
        this.dbUsername = dbUsername;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public void setDbPassword(String dbPassword) {
        this.dbPassword = dbPassword;
    }
}
