package com.rentease.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class BookingRequest {

    @NotNull(message = "Property ID is required")
    private Long propertyId;

    @NotNull(message = "Move in date is required")
    @FutureOrPresent(message = "Move in date must be today or in the future")
    private LocalDate moveInDate;

    private String message;

    public BookingRequest() {
    }

    public BookingRequest(Long propertyId, LocalDate moveInDate, String message) {
        this.propertyId = propertyId;
        this.moveInDate = moveInDate;
        this.message = message;
    }

    public Long getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(Long propertyId) {
        this.propertyId = propertyId;
    }

    public LocalDate getMoveInDate() {
        return moveInDate;
    }

    public void setMoveInDate(LocalDate moveInDate) {
        this.moveInDate = moveInDate;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

