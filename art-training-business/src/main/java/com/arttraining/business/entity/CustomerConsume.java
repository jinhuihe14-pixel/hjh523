package com.arttraining.business.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("biz_customer_consume")
public class CustomerConsume {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long customerId;
    private String customerName;
    private Long orderId;
    private String orderNo;
    private Integer consumeType;
    private BigDecimal amount;
    private BigDecimal balance;
    private LocalDate consumeDate;
    private Long campusId;
    private String campusCode;
    private String remark;
    private LocalDateTime createTime;
    private String createBy;
}
