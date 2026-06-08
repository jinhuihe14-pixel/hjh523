package com.arttraining.user.mapper;

import com.arttraining.user.entity.UserRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole> {

    int deleteByUserId(@Param("userId") Long userId);

    int batchInsert(@Param("list") List<UserRole> list);

    List<Long> selectRoleIdsByUserId(@Param("userId") Long userId);
}
