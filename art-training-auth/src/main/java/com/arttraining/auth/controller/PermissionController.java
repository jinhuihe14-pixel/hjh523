package com.arttraining.auth.controller;

import com.arttraining.auth.entity.Permission;
import com.arttraining.auth.service.PermissionService;
import com.arttraining.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "权限管理")
@RestController
@RequestMapping("/api/permission")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @Operation(summary = "获取权限树")
    @GetMapping("/tree")
    public Result<List<Permission>> tree(@RequestParam(required = false) String keyword,
                                         @RequestParam(required = false) Integer status) {
        return Result.success(permissionService.tree(keyword, status));
    }

    @Operation(summary = "获取权限列表")
    @GetMapping("/list")
    public Result<List<Permission>> list(@RequestParam(required = false) String keyword,
                                         @RequestParam(required = false) Integer status) {
        return Result.success(permissionService.list(keyword, status));
    }

    @Operation(summary = "获取权限详情")
    @GetMapping("/{id}")
    public Result<Permission> getById(@PathVariable Long id) {
        return Result.success(permissionService.getById(id));
    }

    @Operation(summary = "根据编码获取权限")
    @GetMapping("/code/{permissionCode}")
    public Result<Permission> getByCode(@PathVariable String permissionCode) {
        return Result.success(permissionService.getByCode(permissionCode));
    }

    @Operation(summary = "新增权限")
    @PostMapping
    public Result<Void> save(@RequestBody Permission permission) {
        permissionService.save(permission);
        return Result.success();
    }

    @Operation(summary = "修改权限")
    @PutMapping
    public Result<Void> update(@RequestBody Permission permission) {
        permissionService.update(permission);
        return Result.success();
    }

    @Operation(summary = "删除权限")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        permissionService.delete(id);
        return Result.success();
    }

    @Operation(summary = "修改权限状态")
    @PutMapping("/status")
    public Result<Void> updateStatus(@RequestParam Long id, @RequestParam Integer status) {
        permissionService.updateStatus(id, status);
        return Result.success();
    }

    @Operation(summary = "根据用户ID获取权限列表")
    @GetMapping("/user/{userId}")
    public Result<List<Permission>> getPermissionsByUserId(@PathVariable Long userId) {
        return Result.success(permissionService.getPermissionsByUserId(userId));
    }

    @Operation(summary = "根据角色ID获取权限列表")
    @GetMapping("/role/{roleId}")
    public Result<List<Permission>> getPermissionsByRoleId(@PathVariable Long roleId) {
        return Result.success(permissionService.getPermissionsByRoleId(roleId));
    }
}
