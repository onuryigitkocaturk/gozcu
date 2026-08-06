package com.onuryigitkocaturk.query_monitor.enums;

// Bir kullanicinin BELIRLI bir projedeki rolu (global Role'den bagimsiz).
// Siralama onemli: enum sabitlerinin tanim sirasi, hiyerarsi karsilastirmalarinda
// (ordinal()) "en az su rol" kontrolu icin kullanilir - REPORTER < DEVELOPER <
// MAINTAINER < OWNER.
public enum ProjectRole {
    REPORTER,
    DEVELOPER,
    MAINTAINER,
    OWNER
}
