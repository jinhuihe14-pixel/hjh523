package com.arttraining.auth.aspect;

import com.arttraining.auth.annotation.RequiresPermission;
import com.arttraining.auth.annotation.RequiresRole;
import com.arttraining.common.constant.CommonConstant;
import com.arttraining.common.context.UserContext;
import com.arttraining.common.context.UserContextHolder;
import com.arttraining.common.exception.BusinessException;
import com.arttraining.common.result.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Aspect
@Component
public class PermissionAspect {

    @Around("@annotation(com.arttraining.auth.annotation.RequiresPermission) " +
            "|| @within(com.arttraining.auth.annotation.RequiresPermission)")
    public Object checkPermission(ProceedingJoinPoint joinPoint) throws Throwable {
        UserContext userContext = UserContextHolder.get();
        if (userContext == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }

        List<String> userPermissions = userContext.getPermissions();
        List<String> userRoles = userContext.getRoleCodes();

        if (userRoles != null && userRoles.contains(CommonConstant.ROLE_SUPER_ADMIN)) {
            return joinPoint.proceed();
        }

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        RequiresPermission methodAnnotation = method.getAnnotation(RequiresPermission.class);
        RequiresPermission classAnnotation = method.getDeclaringClass().getAnnotation(RequiresPermission.class);

        RequiresPermission annotation = methodAnnotation != null ? methodAnnotation : classAnnotation;

        if (annotation != null) {
            checkPermissions(userPermissions, annotation);
        }

        return joinPoint.proceed();
    }

    @Around("@annotation(com.arttraining.auth.annotation.RequiresRole) " +
            "|| @within(com.arttraining.auth.annotation.RequiresRole)")
    public Object checkRole(ProceedingJoinPoint joinPoint) throws Throwable {
        UserContext userContext = UserContextHolder.get();
        if (userContext == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }

        List<String> userRoles = userContext.getRoleCodes();

        if (userRoles != null && userRoles.contains(CommonConstant.ROLE_SUPER_ADMIN)) {
            return joinPoint.proceed();
        }

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        RequiresRole methodAnnotation = method.getAnnotation(RequiresRole.class);
        RequiresRole classAnnotation = method.getDeclaringClass().getAnnotation(RequiresRole.class);

        RequiresRole annotation = methodAnnotation != null ? methodAnnotation : classAnnotation;

        if (annotation != null) {
            checkRoles(userRoles, annotation);
        }

        return joinPoint.proceed();
    }

    private void checkPermissions(List<String> userPermissions, RequiresPermission annotation) {
        if (userPermissions == null || userPermissions.isEmpty()) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }

        String[] requiredPermissions = annotation.permissions();
        if (requiredPermissions.length == 0 && annotation.value().length() > 0) {
            requiredPermissions = new String[]{annotation.value()};
        }

        if (requiredPermissions.length == 0) {
            return;
        }

        boolean hasPermission;
        if (annotation.logical() == RequiresPermission.Logical.AND) {
            hasPermission = Arrays.stream(requiredPermissions)
                    .allMatch(userPermissions::contains);
        } else {
            hasPermission = Arrays.stream(requiredPermissions)
                    .anyMatch(userPermissions::contains);
        }

        if (!hasPermission) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
    }

    private void checkRoles(List<String> userRoles, RequiresRole annotation) {
        if (userRoles == null || userRoles.isEmpty()) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }

        String[] requiredRoles = annotation.roles();
        if (requiredRoles.length == 0 && annotation.value().length() > 0) {
            requiredRoles = new String[]{annotation.value()};
        }

        if (requiredRoles.length == 0) {
            return;
        }

        boolean hasRole;
        if (annotation.logical() == RequiresRole.Logical.AND) {
            hasRole = Arrays.stream(requiredRoles)
                    .allMatch(userRoles::contains);
        } else {
            hasRole = Arrays.stream(requiredRoles)
                    .anyMatch(userRoles::contains);
        }

        if (!hasRole) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
    }
}
