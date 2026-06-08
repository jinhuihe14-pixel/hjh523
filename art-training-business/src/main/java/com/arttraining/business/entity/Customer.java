package com.arttraining.business.entity;

import com.arttraining.common.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_customer")
public class Customer extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String customerNo;
    private String customerName;
    private Integer customerType;
    private String contactPerson;
    private String contactPhone;
    private String contactEmail;
    private String address;
    private String province;
    private String city;
    private String district;
    private String wechat;
    private String qq;
    private String companyName;
    private String taxNo;
    private String bankName;
    private String bankAccount;
    private String invoiceTitle;
    private Integer level;
    private Integer source;
    private String preferences;
    private BigDecimal totalAmount;
    private Integer orderCount;
    private LocalDateTime lastOrderTime;
    private Long campusId;
    private String campusCode;
    private Integer status;
    private String remark;
}
