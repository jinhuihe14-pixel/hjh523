package com.arttraining.auth.service.impl;

import com.arttraining.auth.entity.Role;
import com.arttraining.auth.entity.RolePermission;
import com.arttraining.auth.mapper.RoleMapper;
import com.arttraining.auth.mapper.RolePermissionMapper;
import com.arttraining.auth.service.RoleService;
import com.arttraining.common.constant.CommonConstant;
import com.arttraining.common.exception.BusinessException;
import com.arttraining.common.page.PageResult;
import com.arttraining.common.result.ResultCode;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    private final RoleMapper roleMapper;
    private final RolePermissionMapper rolePermissionMapper;

    @Override
    public PageResult<Role> page(Integer current, Integer size, String keyword, Integer status) {
        Page<Role> page = new Page<>(current, size);
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(keyword)) {
            wrapper.and(w -> w.like(Role::getRoleName, keyword)
                    .or().like(Role::getRoleCode, keyword));
        }
        if (status != null) {
            wrapper.eq(Role::getStatus, status);
        }
        wrapper.orderByAsc(Role::getSortOrder).orderByDesc(Role::getCreateTime);
        return PageResult.of(page(page, wrapper));
    }

    @Override
    public Role getById(Long id) {
        return roleMapper.selectById(id);
    }

    @Override
    public Role getByCode(String roleCode) {
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getRoleCode, roleCode);
        return getOne(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(Role role) {
        if (!checkCodeUnique(null, role.getRoleCode())) {
            throw new BusinessException(ResultCode.ROLE_CODE_ALREADY_EXIST);
        }
        roleMapper.insert(role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Role role) {
        Role existRole = getById(role.getId());
        if (existRole == null) {
            throw new BusinessException(ResultCode.ROLE_NOT_EXIST);
        }
        if (CommonConstant.ROLE_SUPER_ADMIN.equals(existRole.getRoleCode())) {
            throw new BusinessException(ResultCode.ROLE_CANNOT_DELETE, "超级管理员角色不能修改");
        }
        if (!checkCodeUnique(role.getId(), role.getRoleCode())) {
            throw new BusinessException(ResultCode.ROLE_CODE_ALREADY_EXIST);
        }
        roleMapper.updateById(role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        Role role = getById(id);
        if (role == null) {
            throw new BusinessException(ResultCode.ROLE_NOT_EXIST);
        }
        if (CommonConstant.ROLE_SUPER_ADMIN.equals(role.getRoleCode())) {
            throw new BusinessException(ResultCode.ROLE_CANNOT_DELETE, "超级管理员角色不能删除");
        }
        roleMapper.deleteById(id);
        rolePermissionMapper.deleteByRoleId(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBatch(List<Long> ids) {
        for (Long id : ids) {
            delete(id);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, Integer status) {
        Role role = new Role();
        role.setId(id);
        role.setStatus(status);
        roleMapper.updateById(role);
    }

    @Override
    public List<Role> listAll() {
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getStatus, 1);
        wrapper.orderByAsc(Role::getSortOrder);
        return list(wrapper);
    }

    @Override
    public List<Role> getRolesByUserId(Long userId) {
        return roleMapper.selectRolesByUserId(userId);
    }

    @Override
    public List<String> getRoleCodesByUserId(Long userId) {
        return roleMapper.selectRoleCodesByUserId(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignPermissions(Long roleId, List<Long> permissionIds) {
        rolePermissionMapper.deleteByRoleId(roleId);
        if (permissionIds != null && !permissionIds.isEmpty()) {
            List<RolePermission> list = new ArrayList<>();
            LocalDateTime now = LocalDateTime.now();
            for (Long permissionId : permissionIds) {
                RolePermission rp = new RolePermission();
                rp.setRoleId(roleId);
                rp.setPermissionId(permissionId);
                rp.setCreateTime(now);
                list.add(rp);
            }
            rolePermissionMapper.batchInsert(list);
        }
    }

    @Override
    public List<Long> getPermissionIdsByRoleId(Long roleId) {
        return rolePermissionMapper.selectPermissionIdsByRoleId(roleId);
    }

    @Override
    public boolean checkCodeUnique(Long id, String roleCode) {
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getRoleCode, roleCode);
        if (id != null) {
            wrapper.ne(Role::getId, id);
        }
        return count(wrapper) == 0;
    }
}
