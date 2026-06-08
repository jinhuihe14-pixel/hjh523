package com.arttraining.business.service.impl;

import cn.hutool.core.util.StrUtil;
import com.arttraining.business.entity.OperationLog;
import com.arttraining.business.mapper.OperationLogMapper;
import com.arttraining.business.service.OperationLogService;
import com.arttraining.common.context.UserContextHolder;
import com.arttraining.common.page.PageResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLog>
        implements OperationLogService {

    private final OperationLogMapper operationLogMapper;

    @Override
    public void log(String bizType, Long bizId, String bizNo, String operationType,
                    String operationName, String operationContent, String beforeData, String afterData) {
        OperationLog log = new OperationLog();
        log.setBizType(bizType);
        log.setBizId(bizId);
        log.setBizNo(bizNo);
        log.setOperationType(operationType);
        log.setOperationName(operationName);
        log.setOperationContent(operationContent);
        log.setBeforeData(beforeData);
        log.setAfterData(afterData);
        log.setOperatorId(UserContextHolder.getUserId());
        log.setOperatorName(UserContextHolder.getRealName() != null ? UserContextHolder.getRealName() : UserContextHolder.getUsername());
        log.setIp(getClientIp());
        log.setCampusId(UserContextHolder.getCampusId());
        log.setCampusCode(UserContextHolder.getCampusCode());
        log.setCreateTime(LocalDateTime.now());
        operationLogMapper.insert(log);
    }

    @Override
    public PageResult<OperationLog> page(Integer current, Integer size, String bizType, Long bizId,
                                          String operationType, String operatorName, String startTime, String endTime) {
        Page<OperationLog> page = new Page<>(current, size);
        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(bizType)) {
            wrapper.eq(OperationLog::getBizType, bizType);
        }
        if (bizId != null) {
            wrapper.eq(OperationLog::getBizId, bizId);
        }
        if (StrUtil.isNotBlank(operationType)) {
            wrapper.eq(OperationLog::getOperationType, operationType);
        }
        if (StrUtil.isNotBlank(operatorName)) {
            wrapper.like(OperationLog::getOperatorName, operatorName);
        }
        if (StrUtil.isNotBlank(startTime)) {
            wrapper.ge(OperationLog::getCreateTime, LocalDateTime.parse(startTime + " 00:00:00",
                    java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        if (StrUtil.isNotBlank(endTime)) {
            wrapper.le(OperationLog::getCreateTime, LocalDateTime.parse(endTime + " 23:59:59",
                    java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        wrapper.orderByDesc(OperationLog::getCreateTime);
        return PageResult.of(page(page, wrapper));
    }

    @Override
    public List<OperationLog> listByBiz(String bizType, Long bizId) {
        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OperationLog::getBizType, bizType);
        wrapper.eq(OperationLog::getBizId, bizId);
        wrapper.orderByDesc(OperationLog::getCreateTime);
        return list(wrapper);
    }

    private String getClientIp() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String ip = request.getHeader("X-Forwarded-For");
                if (StrUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getHeader("X-Real-IP");
                }
                if (StrUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getRemoteAddr();
                }
                return ip;
            }
        } catch (Exception e) {
            // ignore
        }
        return "127.0.0.1";
    }
}
