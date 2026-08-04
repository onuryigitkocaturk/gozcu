package com.onuryigitkocaturk.query_monitor.querybuilder;

import java.util.List;

// sadece 2 alan taşır.SQL string'i ile parametrelerini beraber taşımak için var.
// record olma sebebi sadece veri taşıyıp immutable olmasını istememiz. class
// yazıp constructor/getter elle yazmaya gerek kalmıyor.
// sql -> sql'in metin kısmı
// parameters -> ["Fiat"]
// parameter kısmı JdbcTemplate'e veriliyor. JdbcTemplate bu "Fiat" değerini ?'nin yerine
// güvenli bir şekilde -> PreparedStatement ile bağlıyor.
// Jdbc -> Java'nın veritabanına bağlanmak için sunduğu standart arayüz. Entity yapısını bilmediğimizden bunu kullanıyoruz.
public record SqlFragment(String sql, List<Object> parameters) {
}
