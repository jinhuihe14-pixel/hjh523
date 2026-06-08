package com.arttraining.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.arttraining.common.constant.CommonConstant;
import com.arttraining.common.exception.BusinessException;
import com.arttraining.common.page.PageResult;
import com.arttraining.common.result.ResultCode;
import com.arttraining.user.dto.UserQueryDTO;
import com.arttraining.user.dto.UserSaveDTO;
import com.arttraining.user.entity.Campus;
import com.arttraining.user.entity.User;
import com.arttraining.user.entity.UserRole;
import com.arttraining.user.mapper.UserMapper;
import com.arttraining.user.mapper.UserRoleMapper;
import com.arttraining.user.service.CampusService;
import com.arttraining.user.service.UserService;
import com.arttraining.user.vo.UserVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final UserMapper userMapper;
    private final UserRoleMapper userRoleMapper;
    private final CampusService campusService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public PageResult<UserVO> page(Integer current, Integer size, UserQueryDTO query) {
        Page<User> page = new Page<>(current, size);
        Page<User> userPage = userMapper.selectUserPage(page, query);
        List<UserVO> voList = userPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        return PageResult.of(voList, userPage.getTotal(), userPage.getSize(), userPage.getCurrent());
    }

    @Override
    public UserVO getById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            return null;
        }
        return convertToVO(user);
    }

    @Override
    public UserVO getByUsername(String username) {
        User user = userMapper.selectUserByUsername(username);
        if (user == null) {
            return null;
        }
        return convertToVO(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(UserSaveDTO dto) {
        if (!checkUsernameUnique(null, dto.getUsername())) {
            throw new BusinessException(ResultCode.USERNAME_ALREADY_EXIST);
        }
        if (StrUtil.isNotBlank(dto.getPhone()) && !checkPhoneUnique(null, dto.getPhone())) {
            throw new BusinessException(ResultCode.PHONE_ALREADY_EXIST);
        }

        User user = BeanUtil.copyProperties(dto, User.class);
        String password = StrUtil.isNotBlank(dto.getPassword()) ? dto.getPassword() : CommonConstant.DEFAULT_PASSWORD;
        user.setPassword(passwordEncoder.encode(password));
        user.setStatus(dto.getStatus() == null ? 1 : dto.getStatus());

        if (dto.getCampusId() != null) {
            Campus campus = campusService.getById(dto.getCampusId());
            if (campus != null) {
                user.setCampusCode(campus.getCampusCode());
            }
        }

        userMapper.insert(user);

        if (dto.getRoleIds() != null && !dto.getRoleIds().isEmpty()) {
            saveUserRoles(user.getId(), dto.getRoleIds());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(UserSaveDTO dto) {
        User existUser = userMapper.selectById(dto.getId());
        if (existUser == null) {
            throw new BusinessException(ResultCode.USER_NOT_EXIST);
        }

        if (!checkUsernameUnique(dto.getId(), dto.getUsername())) {
            throw new BusinessException(ResultCode.USERNAME_ALREADY_EXIST);
        }
        if (StrUtil.isNotBlank(dto.getPhone()) && !checkPhoneUnique(dto.getId(), dto.getPhone())) {
            throw new BusinessException(ResultCode.PHONE_ALREADY_EXIST);
        }

        User user = BeanUtil.copyProperties(dto, User.class);

        if (dto.getCampusId() != null) {
            Campus campus = campusService.getById(dto.getCampusId());
            if (campus != null) {
                user.setCampusCode(campus.getCampusCode());
            }
        }

        userMapper.updateById(user);

        if (dto.getRoleIds() != null) {
            userRoleMapper.deleteByUserId(user.getId());
            if (!dto.getRoleIds().isEmpty()) {
                saveUserRoles(user.getId(), dto.getRoleIds());
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_EXIST);
        }
        userMapper.deleteById(id);
        userRoleMapper.deleteByUserId(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBatch(List<Long> ids) {
        userMapper.deleteBatchIds(ids);
        for (Long id : ids) {
            userRoleMapper.deleteByUserId(id);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, Integer status) {
        User user = new User();
        user.setId(id);
        user.setStatus(status);
        userMapper.updateById(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(Long id, String password) {
        User user = new User();
        user.setId(id);
        user.setPassword(passwordEncoder.encode(password));
        user.setPasswordUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
    }

    @Override
    public List<UserVO> listByUserType(Integer userType) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUserType, userType);
        wrapper.eq(User::getStatus, 1);
        wrapper.orderByDesc(User::getCreateTime);
        return list(wrapper).stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserVO> listByCampusId(Long campusId) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getCampusId, campusId);
        wrapper.eq(User::getStatus, 1);
        wrapper.orderByDesc(User::getCreateTime);
        return list(wrapper).stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public boolean checkUsernameUnique(Long id, String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        if (id != null) {
            wrapper.ne(User::getId, id);
        }
        return count(wrapper) == 0;
    }

    @Override
    public boolean checkPhoneUnique(Long id, String phone) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone, phone);
        if (id != null) {
            wrapper.ne(User::getId, id);
        }
        return count(wrapper) == 0;
    }

    private void saveUserRoles(Long userId, List<Long> roleIds) {
        List<UserRole> userRoles = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        for (Long roleId : roleIds) {
            UserRole userRole = new UserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(roleId);
            userRole.setCreateTime(now);
            userRoles.add(userRole);
        }
        userRoleMapper.batchInsert(userRoles);
    }

    private UserVO convertToVO(User user) {
        UserVO vo = BeanUtil.copyProperties(user, UserVO.class);
        vo.setUserTypeName(getUserTypeName(user.getUserType()));
        List<Long> roleIds = userRoleMapper.selectRoleIdsByUserId(user.getId());
        vo.setRoleIds(roleIds);
        if (user.getCampusId() != null) {
            Campus campus = campusService.getById(user.getCampusId());
            if (campus != null) {
                vo.setCampusName(campus.getCampusName());
            }
        }
        return vo;
    }

    private String getUserTypeName(Integer userType) {
        if (userType == null) {
            return "";
        }
        return switch (userType) {
            case 0 -> "超级管理员";
            case 1 -> "集团管理员";
            case 2 -> "校区校长";
            case 3 -> "授课教师";
            case 4 -> "课程顾问";
            case 5 -> "学员";
            case 6 -> "财务人员";
            default -> "未知";
        };
    }
}
