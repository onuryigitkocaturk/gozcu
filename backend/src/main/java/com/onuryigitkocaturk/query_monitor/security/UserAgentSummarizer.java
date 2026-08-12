package com.onuryigitkocaturk.query_monitor.security;

// Ham User-Agent header'ini "Chrome (macOS)" gibi genel okunur bir ozete
// indirger. Hem dogrulama mailinde hem TrustedDevice listesinde kullanilir.
public final class UserAgentSummarizer {

    private UserAgentSummarizer() {
    }

    public static String summarize(String userAgent) {
        if (userAgent == null || userAgent.isBlank()) {
            return "bilinmiyor";
        }

        String browser;
        if (userAgent.contains("Edg/")) {
            browser = "Edge";
        } else if (userAgent.contains("OPR/") || userAgent.contains("Opera")) {
            browser = "Opera";
        } else if (userAgent.contains("Firefox/")) {
            browser = "Firefox";
        } else if (userAgent.contains("Chrome/")) {
            browser = "Chrome";
        } else if (userAgent.contains("Safari/")) {
            browser = "Safari";
        } else {
            browser = "bilinmeyen tarayıcı";
        }

        String os;
        if (userAgent.contains("Windows")) {
            os = "Windows";
        } else if (userAgent.contains("Mac OS X")) {
            os = "macOS";
        } else if (userAgent.contains("Android")) {
            os = "Android";
        } else if (userAgent.contains("iPhone") || userAgent.contains("iPad") || userAgent.contains("iOS")) {
            os = "iOS";
        } else if (userAgent.contains("Linux")) {
            os = "Linux";
        } else {
            os = "bilinmeyen işletim sistemi";
        }

        return browser + " (" + os + ")";
    }
}
