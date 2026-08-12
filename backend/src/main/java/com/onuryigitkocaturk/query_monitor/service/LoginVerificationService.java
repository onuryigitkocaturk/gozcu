package com.onuryigitkocaturk.query_monitor.service;

import com.onuryigitkocaturk.query_monitor.model.LoginVerification;
import com.onuryigitkocaturk.query_monitor.model.User;

public interface LoginVerificationService {

    // bir kullanıcı için doğrulama kodu üretir, verificationToken döner.
    String startVerification(User user, String requestIp, String userAgent, Double latitude, Double longitude);

    // kodu doğrular, geçerliyse ilgili LoginVerification'ı (kullanıcı + konum bilgisiyle) döner, değilse exception fırlatır.
    LoginVerification verifyCode(String verificationToken, String code);
}
