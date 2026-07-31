package com.onuryigitkocaturk.query_monitor.alerting;

// alert değerlendirmesinin sonucunu taşımak görevi.
public record AlertEvaluationResult(boolean triggered, long matchCount) {
}
