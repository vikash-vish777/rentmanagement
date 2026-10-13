package com.rentease.dto;

import com.rentease.entity.Booking;
import com.rentease.entity.BookingStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class BookingResponse {
    private Long id;
    private PropertyResponse property;
    private UserDto tenant;
    private UserDto landlord;
    private LocalDate bookingDate;
    private LocalDate moveInDate;
    private BookingStatus status;
    private String message;
    private String landlordNote;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public BookingResponse() {
    }

    public static BookingResponse fromEntity(Booking booking) {
        if (booking == null) return null;
        BookingResponse dto = new BookingResponse();
        dto.setId(booking.getId());
        dto.setProperty(PropertyResponse.fromEntity(booking.getProperty()));
        dto.setTenant(UserDto.fromEntity(booking.getTenant()));
        dto.setLandlord(UserDto.fromEntity(booking.getLandlord()));
        dto.setBookingDate(booking.getBookingDate());
        dto.setMoveInDate(booking.getMoveInDate());
        dto.setStatus(booking.getStatus());
        dto.setMessage(booking.getMessage());
        dto.setLandlordNote(booking.getLandlordNote());
        dto.setCreatedAt(booking.getCreatedAt());
        dto.setUpdatedAt(booking.getUpdatedAt());
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PropertyResponse getProperty() {
        return property;
    }

    public void setProperty(PropertyResponse property) {
        this.property = property;
    }

    public UserDto getTenant() {
        return tenant;
    }

    public void setTenant(UserDto tenant) {
        this.tenant = tenant;
    }

    public UserDto getLandlord() {
        return landlord;
    }

    public void setLandlord(UserDto landlord) {
        this.landlord = landlord;
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDate bookingDate) {
        this.bookingDate = bookingDate;
    }

    public LocalDate getMoveInDate() {
        return moveInDate;
    }

    public void setMoveInDate(LocalDate moveInDate) {
        this.moveInDate = moveInDate;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLandlordNote() {
        return landlordNote;
    }

    public void setLandlordNote(String landlordNote) {
        this.landlordNote = landlordNote;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
