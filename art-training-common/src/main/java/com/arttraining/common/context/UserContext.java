package com.arttraining.common.context;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserContext implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long userId;
    private String username;
    private String realName;
    private String phone;
    private String email;
    private Long campusId;
    private String campusCode;
    private String campusName;
    private List<String> roleCodes;
    private List<String> permissions;
    private Integer userType;
    private String token;
}
