package com.arttraining.auth.entity;

import com.arttraining.common.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role")
public class Role extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String roleCode;
    private String roleName;
    private Integer roleType;
    private Integer dataScope;
    private Integer sortOrder;
    private Integer status;
    private String remark;
}
