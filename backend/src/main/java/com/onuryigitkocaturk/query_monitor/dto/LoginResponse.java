package com.onuryigitkocaturk.query_monitor.dto;

/**
 * Login iki farkli sonuc dondurebilir: bilinen bir cihazdan giris yapildiysa
 * token direkt doluyor; bilinmeyen bir cihazdan giris yapildiysa token null,
 * verificationRequired=true ve verificationToken dolu geliyor - frontend
 * bu durumda "mailine gelen kodu gir" ekranini gostermeli.
 */
public class LoginResponse {

    private String token;
    private boolean verificationRequired;
    private String verificationToken;

    public LoginResponse() {
    }

    public LoginResponse(String token, boolean verificationRequired, String verificationToken) {
        this.token = token;
        this.verificationRequired = verificationRequired;
        this.verificationToken = verificationToken;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isVerificationRequired() {
        return verificationRequired;
    }

    public void setVerificationRequired(boolean verificationRequired) {
        this.verificationRequired = verificationRequired;
    }

    public String getVerificationToken() {
        return verificationToken;
    }

    public void setVerificationToken(String verificationToken) {
        this.verificationToken = verificationToken;
    }
}
