package com.arttraining.user.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String username;
    private String realName;
    private String phone;
    private Integer userType;
    private Long campusId;
    private Integer status;
}
