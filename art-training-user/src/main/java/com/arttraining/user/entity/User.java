package com.arttraining.user.entity;

import com.arttraining.common.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
public class User extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String username;
    private String password;
    private String realName;
    private String nickName;
    private String avatar;
    private String phone;
    private String email;
    private Integer gender;
    private LocalDate birthday;
    private Integer userType;
    private Long campusId;
    private String campusCode;
    private String employeeNo;
    private String studentNo;
    private LocalDate entryDate;
    private String department;
    private String position;
    private String idCard;
    private String emergencyContact;
    private String emergencyPhone;
    private Integer status;
    private LocalDateTime lastLoginTime;
    private String lastLoginIp;
    private LocalDateTime passwordUpdateTime;
    private String remark;
}
