package com.arttraining.business.entity;

import com.arttraining.common.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_order_item")
public class OrderItem extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private Long orderId;
    private String orderNo;
    private String itemName;
    private String itemType;
    private String specification;
    private String material;
    private BigDecimal quantity;
    private String unit;
    private BigDecimal unitPrice;
    private BigDecimal amount;
    private Integer sortOrder;
    private String remark;
}
