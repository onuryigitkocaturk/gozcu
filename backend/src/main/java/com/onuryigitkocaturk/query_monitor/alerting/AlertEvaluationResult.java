package com.onuryigitkocaturk.query_monitor.alerting;

// alert değerlendirmesinin sonucunu taşımak görevi, dto.
public record AlertEvaluationResult(boolean triggered, long matchCount) {
}
