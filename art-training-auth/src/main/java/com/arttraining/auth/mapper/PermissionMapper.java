package com.arttraining.auth.mapper;

import com.arttraining.auth.entity.Permission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PermissionMapper extends BaseMapper<Permission> {

    List<Permission> selectPermissionsByUserId(@Param("userId") Long userId);

    List<String> selectPermissionCodesByUserId(@Param("userId") Long userId);

    List<Permission> selectPermissionsByRoleId(@Param("roleId") Long roleId);

    List<Long> selectPermissionIdsByRoleId(@Param("roleId") Long roleId);
}
