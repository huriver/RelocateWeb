package com.ahut.dto;

import lombok.Data;

@Data
public class MovingNewsPageQueryDTO {
    private int page;
    private int pageSize;
    private String title;
    private String content;
    private Boolean isPublished;
}
