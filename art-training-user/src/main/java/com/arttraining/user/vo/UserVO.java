package com.arttraining.user.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String username;
    private String realName;
    private String nickName;
    private String avatar;
    private String phone;
    private String email;
    private Integer gender;
    private LocalDate birthday;
    private Integer userType;
    private String userTypeName;
    private Long campusId;
    private String campusCode;
    private String campusName;
    private String employeeNo;
    private String studentNo;
    private LocalDate entryDate;
    private String department;
    private String position;
    private Integer status;
    private LocalDateTime lastLoginTime;
    private String remark;
    private LocalDateTime createTime;
    private List<Long> roleIds;
    private List<String> roleNames;
}
