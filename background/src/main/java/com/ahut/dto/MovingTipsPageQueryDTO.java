package com.ahut.dto;

import lombok.Data;

@Data
public class MovingTipsPageQueryDTO {
    private int page;
    private int pageSize;
    private String title;
    private String content;
    private String category;
    private Boolean isPublished;
}
