package com.arttraining.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

@Mapper
public interface AuthUserMapper extends BaseMapper<Object> {

    Map<String, Object> selectUserByUsername(@Param("username") String username);

    Map<String, Object> selectUserById(@Param("id") Long id);

    void updateLastLoginInfo(@Param("id") Long id,
                             @Param("lastLoginTime") java.time.LocalDateTime lastLoginTime,
                             @Param("lastLoginIp") String lastLoginIp);
}
