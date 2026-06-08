package com.arttraining.auth.interceptor;

import cn.hutool.core.util.StrUtil;
import com.arttraining.common.constant.CommonConstant;
import com.arttraining.common.context.UserContext;
import com.arttraining.common.context.UserContextHolder;
import com.arttraining.common.exception.BusinessException;
import com.arttraining.common.result.ResultCode;
import com.arttraining.common.util.JwtUtil;
import com.arttraining.auth.service.PermissionService;
import com.arttraining.auth.service.RoleService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final RoleService roleService;
    private final PermissionService permissionService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String token = request.getHeader(CommonConstant.TOKEN_HEADER);
        if (StrUtil.isBlank(token)) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }

        if (token.startsWith(CommonConstant.TOKEN_PREFIX)) {
            token = token.substring(CommonConstant.TOKEN_PREFIX.length());
        }

        Claims claims = JwtUtil.parseToken(token);
        if (claims == null) {
            throw new BusinessException(ResultCode.TOKEN_INVALID);
        }

        Long userId = Long.valueOf(claims.get("userId").toString());
        String username = claims.get("username").toString();
        Integer userType = claims.get("userType") != null
                ? Integer.valueOf(claims.get("userType").toString()) : null;
        Long campusId = claims.get("campusId") != null
                ? Long.valueOf(claims.get("campusId").toString()) : null;
        String campusCode = claims.get("campusCode") != null
                ? claims.get("campusCode").toString() : null;

        List<String> roleCodes = roleService.getRoleCodesByUserId(userId);
        List<String> permissions = permissionService.getPermissionCodesByUserId(userId);

        UserContext userContext = UserContext.builder()
                .userId(userId)
                .username(username)
                .campusId(campusId)
                .campusCode(campusCode)
                .userType(userType)
                .roleCodes(roleCodes)
                .permissions(permissions)
                .token(token)
                .build();

        UserContextHolder.set(userContext);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        UserContextHolder.remove();
    }
}
