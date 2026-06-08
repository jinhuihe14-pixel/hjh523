package com.arttraining.business.interceptor;

import cn.hutool.core.util.StrUtil;
import com.arttraining.common.constant.CommonConstant;
import com.arttraining.common.context.UserContext;
import com.arttraining.common.context.UserContextHolder;
import com.arttraining.common.exception.BusinessException;
import com.arttraining.common.result.ResultCode;
import com.arttraining.common.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.ArrayList;

@Component
public class AuthInterceptor implements HandlerInterceptor {

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
        String realName = claims.get("realName") != null ? claims.get("realName").toString() : null;
        Integer userType = claims.get("userType") != null
                ? Integer.valueOf(claims.get("userType").toString()) : null;
        Long campusId = claims.get("campusId") != null
                ? Long.valueOf(claims.get("campusId").toString()) : null;
        String campusCode = claims.get("campusCode") != null
                ? claims.get("campusCode").toString() : null;

        UserContext userContext = UserContext.builder()
                .userId(userId)
                .username(username)
                .realName(realName)
                .campusId(campusId)
                .campusCode(campusCode)
                .userType(userType)
                .roleCodes(new ArrayList<>())
                .permissions(new ArrayList<>())
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
