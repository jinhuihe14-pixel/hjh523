package com.arttraining.auth.vo;

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
public class LoginVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String accessToken;
    private String refreshToken;
    private Long expiresIn;
    private String tokenType;

    private Long userId;
    private String username;
    private String realName;
    private String avatar;
    private Integer userType;
    private Long campusId;
    private String campusCode;
    private String campusName;

    private List<String> roles;
    private List<String> permissions;
}
