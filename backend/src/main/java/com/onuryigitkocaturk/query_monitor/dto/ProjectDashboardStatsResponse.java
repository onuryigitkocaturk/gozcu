package com.onuryigitkocaturk.query_monitor.dto;

import java.time.LocalDateTime;

/**
 * GET /api/projects/{projectId}/dashboard-stats icin - proje listesindeki
 * her satirda gosterilecek ozet sayilar. Tek tek entity donmez, sadece
 * COUNT sonuclari tasir.
 */
public class ProjectDashboardStatsResponse {

    private long tableCount;
    private long queryCount;
    private long activeQueryCount;
    private long activeAlertCount;
    private long triggeredLast7Days;
    private LocalDateTime lastTriggeredAt;

    public ProjectDashboardStatsResponse() {
    }

    public ProjectDashboardStatsResponse(long tableCount, long queryCount, long activeQueryCount,
                                          long activeAlertCount, long triggeredLast7Days,
                                          LocalDateTime lastTriggeredAt) {
        this.tableCount = tableCount;
        this.queryCount = queryCount;
        this.activeQueryCount = activeQueryCount;
        this.activeAlertCount = activeAlertCount;
        this.triggeredLast7Days = triggeredLast7Days;
        this.lastTriggeredAt = lastTriggeredAt;
    }

    public long getTableCount() {
        return tableCount;
    }

    public void setTableCount(long tableCount) {
        this.tableCount = tableCount;
    }

    public long getQueryCount() {
        return queryCount;
    }

    public void setQueryCount(long queryCount) {
        this.queryCount = queryCount;
    }

    public long getActiveQueryCount() {
        return activeQueryCount;
    }

    public void setActiveQueryCount(long activeQueryCount) {
        this.activeQueryCount = activeQueryCount;
    }

    public long getActiveAlertCount() {
        return activeAlertCount;
    }

    public void setActiveAlertCount(long activeAlertCount) {
        this.activeAlertCount = activeAlertCount;
    }

    public long getTriggeredLast7Days() {
        return triggeredLast7Days;
    }

    public void setTriggeredLast7Days(long triggeredLast7Days) {
        this.triggeredLast7Days = triggeredLast7Days;
    }

    public LocalDateTime getLastTriggeredAt() {
        return lastTriggeredAt;
    }

    public void setLastTriggeredAt(LocalDateTime lastTriggeredAt) {
        this.lastTriggeredAt = lastTriggeredAt;
    }
}
