package com.arttraining.user.entity;

import com.arttraining.common.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_campus")
public class Campus extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String campusCode;
    private String campusName;
    private String address;
    private String phone;
    private String principal;
    private Long principalId;
    private String province;
    private String city;
    private String district;
    private Integer sortOrder;
    private Integer status;
    private String remark;
}
