package com.onuryigitkocaturk.query_monitor.dto;

import java.util.List;

/**
 * GET /api/users/me/account icin - "Hesabim" sayfasinda gosterilecek,
 * kullaniciya ozel ozet bilgiler (yazdigi toplam sorgu sayisi + guvenilir
 * cihaz listesi).
 */
public class MyAccountResponse {

    private long totalQueriesWritten;
    private List<TrustedDeviceResponse> trustedDevices;

    public MyAccountResponse() {
    }

    public MyAccountResponse(long totalQueriesWritten, List<TrustedDeviceResponse> trustedDevices) {
        this.totalQueriesWritten = totalQueriesWritten;
        this.trustedDevices = trustedDevices;
    }

    public long getTotalQueriesWritten() {
        return totalQueriesWritten;
    }

    public void setTotalQueriesWritten(long totalQueriesWritten) {
        this.totalQueriesWritten = totalQueriesWritten;
    }

    public List<TrustedDeviceResponse> getTrustedDevices() {
        return trustedDevices;
    }

    public void setTrustedDevices(List<TrustedDeviceResponse> trustedDevices) {
        this.trustedDevices = trustedDevices;
    }
}
