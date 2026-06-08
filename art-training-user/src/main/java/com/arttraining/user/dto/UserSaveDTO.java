package com.arttraining.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Data
public class UserSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    @NotBlank(message = "用户名不能为空")
    private String username;

    private String password;

    @NotBlank(message = "真实姓名不能为空")
    private String realName;

    private String nickName;
    private String avatar;
    private String phone;
    private String email;
    private Integer gender;
    private LocalDate birthday;

    @NotNull(message = "用户类型不能为空")
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
    private String remark;

    private List<Long> roleIds;
}
