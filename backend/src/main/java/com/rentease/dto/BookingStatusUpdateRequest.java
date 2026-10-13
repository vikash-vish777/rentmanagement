package com.rentease.dto;

import com.rentease.entity.BookingStatus;
import jakarta.validation.constraints.NotNull;

public class BookingStatusUpdateRequest {

    @NotNull(message = "Status is required")
    private BookingStatus status;

    private String landlordNote;

    public BookingStatusUpdateRequest() {
    }

    public BookingStatusUpdateRequest(BookingStatus status, String landlordNote) {
        this.status = status;
        this.landlordNote = landlordNote;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public String getLandlordNote() {
        return landlordNote;
    }

    public void setLandlordNote(String landlordNote) {
        this.landlordNote = landlordNote;
    }
}

