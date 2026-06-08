package com.arttraining.auth.service;

import com.arttraining.auth.entity.Role;
import com.arttraining.common.page.PageResult;

import java.util.List;

public interface RoleService {

    PageResult<Role> page(Integer current, Integer size, String keyword, Integer status);

    Role getById(Long id);

    Role getByCode(String roleCode);

    void save(Role role);

    void update(Role role);

    void delete(Long id);

    void deleteBatch(List<Long> ids);

    void updateStatus(Long id, Integer status);

    List<Role> listAll();

    List<Role> getRolesByUserId(Long userId);

    List<String> getRoleCodesByUserId(Long userId);

    void assignPermissions(Long roleId, List<Long> permissionIds);

    List<Long> getPermissionIdsByRoleId(Long roleId);

    boolean checkCodeUnique(Long id, String roleCode);
}
