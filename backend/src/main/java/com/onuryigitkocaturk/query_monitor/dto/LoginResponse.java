package com.onuryigitkocaturk.query_monitor.dto;

// login controler'ı iki farklı sonuç döndürebilir. bilinen bir cihazdan giriş yapılırsa
// jwt token verilir, bilinmeyen bir cihazsa token null döner, frontend "maile gelen kodu gir"
// ekranını göstermeli.
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
