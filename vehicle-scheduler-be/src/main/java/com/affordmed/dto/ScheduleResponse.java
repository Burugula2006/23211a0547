package com.affordmed.dto;

import java.util.List;

public class ScheduleResponse {

    private int depotId;
    private int availableHours;
    private int totalHoursUsed;
    private int totalImpact;
    private List<String> selectedTaskIds;

    public ScheduleResponse() {
    }

    public ScheduleResponse(int depotId,
                            int availableHours,
                            int totalHoursUsed,
                            int totalImpact,
                            List<String> selectedTaskIds) {
        this.depotId = depotId;
        this.availableHours = availableHours;
        this.totalHoursUsed = totalHoursUsed;
        this.totalImpact = totalImpact;
        this.selectedTaskIds = selectedTaskIds;
    }

    public int getDepotId() {
        return depotId;
    }

    public void setDepotId(int depotId) {
        this.depotId = depotId;
    }

    public int getAvailableHours() {
        return availableHours;
    }

    public void setAvailableHours(int availableHours) {
        this.availableHours = availableHours;
    }

    public int getTotalHoursUsed() {
        return totalHoursUsed;
    }

    public void setTotalHoursUsed(int totalHoursUsed) {
        this.totalHoursUsed = totalHoursUsed;
    }

    public int getTotalImpact() {
        return totalImpact;
    }

    public void setTotalImpact(int totalImpact) {
        this.totalImpact = totalImpact;
    }

    public List<String> getSelectedTaskIds() {
        return selectedTaskIds;
    }

    public void setSelectedTaskIds(List<String> selectedTaskIds) {
        this.selectedTaskIds = selectedTaskIds;
    }
}