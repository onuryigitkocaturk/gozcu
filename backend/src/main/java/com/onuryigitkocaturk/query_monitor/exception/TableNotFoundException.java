package com.onuryigitkocaturk.query_monitor.exception;

public class TableNotFoundException extends RuntimeException {

    public TableNotFoundException(String message) {
        super(message);
    }
}
