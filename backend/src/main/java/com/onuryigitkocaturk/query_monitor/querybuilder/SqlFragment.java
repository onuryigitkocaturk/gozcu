package com.onuryigitkocaturk.query_monitor.querybuilder;

import java.util.List;

// sadece 2 alan taşır.SQL string'i ile parametrelerini beraber taşımak için var.
// record olma sebebi sadece veri taşıyıp immutable olmasını istememiz. class
// yazıp constructor/getter elle yazmaya gerek kalmıyor.
public record SqlFragment(String sql, List<Object> parameters) {
}
