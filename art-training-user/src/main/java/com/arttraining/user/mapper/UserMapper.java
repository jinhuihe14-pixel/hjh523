package com.arttraining.user.mapper;

import com.arttraining.user.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    IPage<User> selectUserPage(Page<User> page, @Param("query") Object query);

    List<Long> selectRoleIdsByUserId(@Param("userId") Long userId);

    User selectUserByUsername(@Param("username") String username);

    User selectUserByPhone(@Param("phone") String phone);
}
