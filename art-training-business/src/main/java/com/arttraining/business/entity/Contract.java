package com.arttraining.business.entity;

import com.arttraining.common.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_contract")
public class Contract extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String contractNo;
    private String contractName;
    private Long customerId;
    private String customerName;
    private Integer contractType;
    private LocalDate signDate;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal contractAmount;
    private Integer settlementMethod;
    private BigDecimal discountRate;
    private String contactPerson;
    private String customerContact;
    private String customerPhone;
    private String files;
    private Integer status;
    private Integer remindDays;
    private Integer reminded;
    private Long campusId;
    private String campusCode;
    private String remark;
}
