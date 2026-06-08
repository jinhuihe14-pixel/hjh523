package com.arttraining.auth.entity;

import com.arttraining.common.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_permission")
public class Permission extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private Long parentId;
    private String permissionCode;
    private String permissionName;
    private Integer permissionType;
    private String path;
    private String component;
    private String icon;
    private Integer sortOrder;
    private Integer status;
    private Integer visible;
    private String remark;

    @TableField(exist = false)
    private List<Permission> children;
}
