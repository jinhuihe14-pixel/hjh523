package com.arttraining.auth.service.impl;

import com.arttraining.auth.dto.LoginDTO;
import com.arttraining.auth.mapper.AuthUserMapper;
import com.arttraining.auth.service.AuthService;
import com.arttraining.auth.service.PermissionService;
import com.arttraining.auth.service.RoleService;
import com.arttraining.auth.vo.LoginVO;
import com.arttraining.common.constant.CommonConstant;
import com.arttraining.common.exception.BusinessException;
import com.arttraining.common.result.ResultCode;
import com.arttraining.common.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthUserMapper authUserMapper;
    private final RoleService roleService;
    private final PermissionService permissionService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private static final long TOKEN_EXPIRE = 24 * 60 * 60 * 1000L;
    private static final long REFRESH_EXPIRE = 7 * 24 * 60 * 60 * 1000L;

    @Override
    public LoginVO login(LoginDTO dto) {
        Map<String, Object> userMap = authUserMapper.selectUserByUsername(dto.getUsername());
        if (userMap == null || userMap.isEmpty()) {
            throw new BusinessException(ResultCode.LOGIN_ERROR);
        }

        String password = (String) userMap.get("password");
        if (!passwordEncoder.matches(dto.getPassword(), password)) {
            throw new BusinessException(ResultCode.LOGIN_ERROR);
        }

        Integer status = userMap.get("status") != null ? ((Number) userMap.get("status")).intValue() : 0;
        if (status != 1) {
            throw new BusinessException(ResultCode.ACCOUNT_DISABLED);
        }

        Long userId = ((Number) userMap.get("id")).longValue();
        String username = (String) userMap.get("username");
        Integer userType = userMap.get("userType") != null ? ((Number) userMap.get("userType")).intValue() : null;
        Long campusId = userMap.get("campusId") != null ? ((Number) userMap.get("campusId")).longValue() : null;
        String campusCode = (String) userMap.get("campusCode");
        String realName = (String) userMap.get("realName");
        String avatar = (String) userMap.get("avatar");

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("userType", userType);
        if (campusId != null) {
            claims.put("campusId", campusId);
        }
        if (campusCode != null) {
            claims.put("campusCode", campusCode);
        }

        String accessToken = JwtUtil.generateToken(claims, TOKEN_EXPIRE);
        String refreshToken = JwtUtil.generateToken(claims, REFRESH_EXPIRE);

        authUserMapper.updateLastLoginInfo(userId, LocalDateTime.now(), null);

        List<String> roles = roleService.getRoleCodesByUserId(userId);
        List<String> permissions = permissionService.getPermissionCodesByUserId(userId);

        return LoginVO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(TOKEN_EXPIRE / 1000)
                .tokenType("Bearer")
                .userId(userId)
                .username(username)
                .realName(realName)
                .avatar(avatar)
                .userType(userType)
                .campusId(campusId)
                .campusCode(campusCode)
                .roles(roles)
                .permissions(permissions)
                .build();
    }

    @Override
    public LoginVO refreshToken(String refreshToken) {
        Claims claims = JwtUtil.parseToken(refreshToken);
        if (claims == null) {
            throw new BusinessException(ResultCode.TOKEN_INVALID);
        }

        Long userId = Long.valueOf(claims.get("userId").toString());
        Map<String, Object> userMap = authUserMapper.selectUserById(userId);
        if (userMap == null || userMap.isEmpty()) {
            throw new BusinessException(ResultCode.USER_NOT_EXIST);
        }

        Integer status = userMap.get("status") != null ? ((Number) userMap.get("status")).intValue() : 0;
        if (status != 1) {
            throw new BusinessException(ResultCode.ACCOUNT_DISABLED);
        }

        String username = (String) userMap.get("username");
        Integer userType = userMap.get("userType") != null ? ((Number) userMap.get("userType")).intValue() : null;
        Long campusId = userMap.get("campusId") != null ? ((Number) userMap.get("campusId")).longValue() : null;
        String campusCode = (String) userMap.get("campusCode");
        String realName = (String) userMap.get("realName");
        String avatar = (String) userMap.get("avatar");

        Map<String, Object> newClaims = new HashMap<>();
        newClaims.put("userId", userId);
        newClaims.put("username", username);
        newClaims.put("userType", userType);
        if (campusId != null) {
            newClaims.put("campusId", campusId);
        }
        if (campusCode != null) {
            newClaims.put("campusCode", campusCode);
        }

        String newAccessToken = JwtUtil.generateToken(newClaims, TOKEN_EXPIRE);
        String newRefreshToken = JwtUtil.generateToken(newClaims, REFRESH_EXPIRE);

        List<String> roles = roleService.getRoleCodesByUserId(userId);
        List<String> permissions = permissionService.getPermissionCodesByUserId(userId);

        return LoginVO.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .expiresIn(TOKEN_EXPIRE / 1000)
                .tokenType("Bearer")
                .userId(userId)
                .username(username)
                .realName(realName)
                .avatar(avatar)
                .userType(userType)
                .campusId(campusId)
                .campusCode(campusCode)
                .roles(roles)
                .permissions(permissions)
                .build();
    }

    @Override
    public void logout(String token) {
        log.info("用户登出: {}", token);
    }

    @Override
    public boolean validateToken(String token) {
        return JwtUtil.validateToken(token);
    }

    @Override
    public Long getUserIdFromToken(String token) {
        return JwtUtil.getUserIdFromToken(token);
    }
}
