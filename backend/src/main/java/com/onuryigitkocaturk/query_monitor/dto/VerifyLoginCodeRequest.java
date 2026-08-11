package com.onuryigitkocaturk.query_monitor.dto;

import jakarta.validation.constraints.NotBlank;

public class VerifyLoginCodeRequest {

    @NotBlank
    private String verificationToken;

    @NotBlank
    private String code;

    // zorunlu değil
    private String screenResolution;

    public String getVerificationToken() {
        return verificationToken;
    }

    public void setVerificationToken(String verificationToken) {
        this.verificationToken = verificationToken;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getScreenResolution() {
        return screenResolution;
    }

    public void setScreenResolution(String screenResolution) {
        this.screenResolution = screenResolution;
    }
}
