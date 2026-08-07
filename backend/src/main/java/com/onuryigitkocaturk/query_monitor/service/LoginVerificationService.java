package com.onuryigitkocaturk.query_monitor.service;

import com.onuryigitkocaturk.query_monitor.model.User;

public interface LoginVerificationService {

    /** Bir kullanici icin dogrulama kodu uretir, mailler, verificationToken doner. */
    String startVerification(User user);

    /** Kodu dogrular, gecerliyse ilgili kullaniciyi doner - aksi halde exception firlatir. */
    User verifyCode(String verificationToken, String code);
}
