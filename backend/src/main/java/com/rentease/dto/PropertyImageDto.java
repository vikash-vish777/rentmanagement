package com.rentease.dto;

import com.rentease.entity.PropertyImage;

public class PropertyImageDto {
    private Long id;
    private String imageUrl;
    private String caption;
    private boolean isPrimary;

    public PropertyImageDto() {
    }

    public static PropertyImageDto fromEntity(PropertyImage image) {
        if (image == null) return null;
        PropertyImageDto dto = new PropertyImageDto();
        dto.setId(image.getId());
        dto.setImageUrl(image.getImageUrl());
        dto.setCaption(image.getCaption());
        dto.setPrimary(image.isPrimary());
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    public void setPrimary(boolean primary) {
        isPrimary = primary;
    }
}

