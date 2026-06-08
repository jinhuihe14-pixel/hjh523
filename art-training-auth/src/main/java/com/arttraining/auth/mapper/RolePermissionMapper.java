package com.arttraining.auth.mapper;

import com.arttraining.auth.entity.RolePermission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RolePermissionMapper extends BaseMapper<RolePermission> {

    int deleteByRoleId(@Param("roleId") Long roleId);

    int batchInsert(@Param("list") List<RolePermission> list);

    List<Long> selectPermissionIdsByRoleId(@Param("roleId") Long roleId);
}
