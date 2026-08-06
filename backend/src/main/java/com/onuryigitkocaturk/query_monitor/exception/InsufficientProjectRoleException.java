package com.onuryigitkocaturk.query_monitor.exception;

// Bir kullanici, kendi rolunun izin verdigi seviyenin ustunde bir islem
// yapmaya calistiginda (orn. Maintainer birini Owner yapmaya calisirsa).
public class InsufficientProjectRoleException extends RuntimeException {

    public InsufficientProjectRoleException(String message) {
        super(message);
    }
}
