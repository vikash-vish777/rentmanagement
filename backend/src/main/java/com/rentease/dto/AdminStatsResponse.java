package com.rentease.dto;

import java.util.Map;

public class AdminStatsResponse {
    private long totalUsers;
    private long totalTenants;
    private long totalLandlords;
    private long totalProperties;
    private long activeProperties;
    private long totalBookings;
    private long pendingBookings;
    private long confirmedBookings;
    private Double averageRent;
    private Map<String, Long> propertiesByCity;

    public AdminStatsResponse() {
    }

    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getTotalTenants() {
        return totalTenants;
    }

    public void setTotalTenants(long totalTenants) {
        this.totalTenants = totalTenants;
    }

    public long getTotalLandlords() {
        return totalLandlords;
    }

    public void setTotalLandlords(long totalLandlords) {
        this.totalLandlords = totalLandlords;
    }

    public long getTotalProperties() {
        return totalProperties;
    }

    public void setTotalProperties(long totalProperties) {
        this.totalProperties = totalProperties;
    }

    public long getActiveProperties() {
        return activeProperties;
    }

    public void setActiveProperties(long activeProperties) {
        this.activeProperties = activeProperties;
    }

    public long getTotalBookings() {
        return totalBookings;
    }

    public void setTotalBookings(long totalBookings) {
        this.totalBookings = totalBookings;
    }

    public long getPendingBookings() {
        return pendingBookings;
    }

    public void setPendingBookings(long pendingBookings) {
        this.pendingBookings = pendingBookings;
    }

    public long getConfirmedBookings() {
        return confirmedBookings;
    }

    public void setConfirmedBookings(long confirmedBookings) {
        this.confirmedBookings = confirmedBookings;
    }

    public Double getAverageRent() {
        return averageRent;
    }

    public void setAverageRent(Double averageRent) {
        this.averageRent = averageRent;
    }

    public Map<String, Long> getPropertiesByCity() {
        return propertiesByCity;
    }

    public void setPropertiesByCity(Map<String, Long> propertiesByCity) {
        this.propertiesByCity = propertiesByCity;
    }
}
