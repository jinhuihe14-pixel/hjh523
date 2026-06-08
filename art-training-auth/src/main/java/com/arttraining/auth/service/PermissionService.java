package com.arttraining.auth.service;

import com.arttraining.auth.entity.Permission;

import java.util.List;

public interface PermissionService {

    List<Permission> tree(String keyword, Integer status);

    List<Permission> list(String keyword, Integer status);

    Permission getById(Long id);

    Permission getByCode(String permissionCode);

    void save(Permission permission);

    void update(Permission permission);

    void delete(Long id);

    void updateStatus(Long id, Integer status);

    List<Permission> getPermissionsByUserId(Long userId);

    List<String> getPermissionCodesByUserId(Long userId);

    List<Permission> getPermissionsByRoleId(Long roleId);

    boolean checkCodeUnique(Long id, String permissionCode);
}
