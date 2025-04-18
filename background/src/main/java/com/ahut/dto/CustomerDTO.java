package com.ahut.dto;

import lombok.Data;

@Data
public class CustomerDTO {
    private Long id;
    private String username;
    private String name;
    private String gender;
    private String phone;
    private String email;
    private String idCard;
    private String familyPhone;
    private String photoUrl;
}
