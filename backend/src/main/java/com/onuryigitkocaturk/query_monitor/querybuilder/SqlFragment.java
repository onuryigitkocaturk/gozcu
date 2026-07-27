package com.onuryigitkocaturk.query_monitor.querybuilder;

import java.util.List;

/**
 * Bir QueryNode'un cevrildigi SQL parcasi: parametreli bir WHERE kosulu
 * ("field > ?") ve buna karsilik gelen deger listesi (PreparedStatement'a
 * sirayla baglanacak).
 */
public record SqlFragment(String sql, List<Object> parameters) {
}
