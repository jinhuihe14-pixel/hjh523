package com.arttraining.business.entity;

import com.arttraining.common.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_order")
public class Order extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String orderNo;
    private String orderName;
    private Long customerId;
    private String customerName;
    private String contactPerson;
    private String contactPhone;
    private Integer orderType;
    private Integer orderSource;
    private Integer urgent;
    private Integer orderStatus;
    private BigDecimal totalAmount;
    private BigDecimal receivedAmount;
    private Integer paymentStatus;
    private Integer deliveryType;
    private String deliveryAddress;
    private String receiver;
    private String receiverPhone;
    private LocalDate expectDate;
    private LocalDate actualDate;
    private Long designerId;
    private String designerName;
    private Long producerId;
    private String producerName;
    private Long reviewerId;
    private String reviewerName;
    private LocalDateTime reviewTime;
    private String reviewRemark;
    private String files;
    private String requirement;
    private Long campusId;
    private String campusCode;
    private String remark;
}
