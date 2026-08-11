package com.onuryigitkocaturk.query_monitor.service;

import com.onuryigitkocaturk.query_monitor.model.User;

public interface LoginVerificationService {

    // bir kullanıcı için doğrulama kodu üretir, verificationToken döner.
    String startVerification(User user, String requestIp, String userAgent);

    // kodu doğrular, geçerliyse ilgili kullanıcıyı döner değilse exception fırlatır.
    User verifyCode(String verificationToken, String code);
}
