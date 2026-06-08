package com.arttraining.auth.service.impl;

import com.arttraining.auth.entity.Permission;
import com.arttraining.auth.mapper.PermissionMapper;
import com.arttraining.auth.service.PermissionService;
import com.arttraining.common.constant.CommonConstant;
import com.arttraining.common.exception.BusinessException;
import com.arttraining.common.result.ResultCode;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {

    private final PermissionMapper permissionMapper;

    @Override
    public List<Permission> tree(String keyword, Integer status) {
        List<Permission> all = list(keyword, status);
        return buildTree(all, CommonConstant.ROOT_PARENT_ID);
    }

    @Override
    public List<Permission> list(String keyword, Integer status) {
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(keyword)) {
            wrapper.and(w -> w.like(Permission::getPermissionName, keyword)
                    .or().like(Permission::getPermissionCode, keyword));
        }
        if (status != null) {
            wrapper.eq(Permission::getStatus, status);
        }
        wrapper.orderByAsc(Permission::getParentId).orderByAsc(Permission::getSortOrder);
        return list(wrapper);
    }

    @Override
    public Permission getById(Long id) {
        return permissionMapper.selectById(id);
    }

    @Override
    public Permission getByCode(String permissionCode) {
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Permission::getPermissionCode, permissionCode);
        return getOne(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(Permission permission) {
        if (!checkCodeUnique(null, permission.getPermissionCode())) {
            throw new BusinessException(ResultCode.PERMISSION_CODE_ALREADY_EXIST);
        }
        if (permission.getParentId() == null) {
            permission.setParentId(CommonConstant.ROOT_PARENT_ID);
        }
        permissionMapper.insert(permission);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Permission permission) {
        Permission exist = getById(permission.getId());
        if (exist == null) {
            throw new BusinessException(ResultCode.PERMISSION_NOT_EXIST);
        }
        if (!checkCodeUnique(permission.getId(), permission.getPermissionCode())) {
            throw new BusinessException(ResultCode.PERMISSION_CODE_ALREADY_EXIST);
        }
        permissionMapper.updateById(permission);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Permission::getParentId, id);
        long childCount = count(wrapper);
        if (childCount > 0) {
            throw new BusinessException(ResultCode.DATA_CANNOT_DELETE, "存在子权限，不能删除");
        }
        permissionMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, Integer status) {
        Permission permission = new Permission();
        permission.setId(id);
        permission.setStatus(status);
        permissionMapper.updateById(permission);
    }

    @Override
    public List<Permission> getPermissionsByUserId(Long userId) {
        return permissionMapper.selectPermissionsByUserId(userId);
    }

    @Override
    public List<String> getPermissionCodesByUserId(Long userId) {
        return permissionMapper.selectPermissionCodesByUserId(userId);
    }

    @Override
    public List<Permission> getPermissionsByRoleId(Long roleId) {
        return permissionMapper.selectPermissionsByRoleId(roleId);
    }

    @Override
    public boolean checkCodeUnique(Long id, String permissionCode) {
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Permission::getPermissionCode, permissionCode);
        if (id != null) {
            wrapper.ne(Permission::getId, id);
        }
        return count(wrapper) == 0;
    }

    private List<Permission> buildTree(List<Permission> permissions, Long parentId) {
        Map<Long, List<Permission>> childrenMap = permissions.stream()
                .collect(Collectors.groupingBy(Permission::getParentId));
        for (Permission permission : permissions) {
            permission.setChildren(childrenMap.getOrDefault(permission.getId(), new ArrayList<>()));
        }
        return permissions.stream()
                .filter(p -> p.getParentId().equals(parentId))
                .collect(Collectors.toList());
    }
}
