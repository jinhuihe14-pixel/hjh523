package com.arttraining.business.service;

import com.arttraining.business.entity.OperationLog;
import com.arttraining.common.page.PageResult;

import java.util.List;

public interface OperationLogService {

    void log(String bizType, Long bizId, String bizNo, String operationType,
             String operationName, String operationContent, String beforeData, String afterData);

    PageResult<OperationLog> page(Integer current, Integer size, String bizType, Long bizId,
                                   String operationType, String operatorName, String startTime, String endTime);

    List<OperationLog> listByBiz(String bizType, Long bizId);
}
