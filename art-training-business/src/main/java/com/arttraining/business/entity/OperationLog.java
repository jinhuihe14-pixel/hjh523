package com.arttraining.business.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("biz_operation_log")
public class OperationLog {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String bizType;
    private Long bizId;
    private String bizNo;
    private String operationType;
    private String operationName;
    private String operationContent;
    private String beforeData;
    private String afterData;
    private Long operatorId;
    private String operatorName;
    private String ip;
    private Long campusId;
    private String campusCode;
    private LocalDateTime createTime;
}
