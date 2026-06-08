package com.arttraining.auth.controller;

import com.arttraining.auth.entity.Role;
import com.arttraining.auth.service.RoleService;
import com.arttraining.common.page.PageResult;
import com.arttraining.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "角色管理")
@RestController
@RequestMapping("/api/role")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @Operation(summary = "分页查询角色")
    @GetMapping("/page")
    public Result<PageResult<Role>> page(@RequestParam(defaultValue = "1") Integer current,
                                         @RequestParam(defaultValue = "10") Integer size,
                                         @RequestParam(required = false) String keyword,
                                         @RequestParam(required = false) Integer status) {
        return Result.success(roleService.page(current, size, keyword, status));
    }

    @Operation(summary = "获取角色详情")
    @GetMapping("/{id}")
    public Result<Role> getById(@PathVariable Long id) {
        return Result.success(roleService.getById(id));
    }

    @Operation(summary = "根据编码获取角色")
    @GetMapping("/code/{roleCode}")
    public Result<Role> getByCode(@PathVariable String roleCode) {
        return Result.success(roleService.getByCode(roleCode));
    }

    @Operation(summary = "新增角色")
    @PostMapping
    public Result<Void> save(@RequestBody Role role) {
        roleService.save(role);
        return Result.success();
    }

    @Operation(summary = "修改角色")
    @PutMapping
    public Result<Void> update(@RequestBody Role role) {
        roleService.update(role);
        return Result.success();
    }

    @Operation(summary = "删除角色")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        roleService.delete(id);
        return Result.success();
    }

    @Operation(summary = "批量删除角色")
    @DeleteMapping("/batch")
    public Result<Void> deleteBatch(@RequestBody List<Long> ids) {
        roleService.deleteBatch(ids);
        return Result.success();
    }

    @Operation(summary = "修改角色状态")
    @PutMapping("/status")
    public Result<Void> updateStatus(@RequestParam Long id, @RequestParam Integer status) {
        roleService.updateStatus(id, status);
        return Result.success();
    }

    @Operation(summary = "获取全部角色列表")
    @GetMapping("/list")
    public Result<List<Role>> listAll() {
        return Result.success(roleService.listAll());
    }

    @Operation(summary = "根据用户ID获取角色列表")
    @GetMapping("/user/{userId}")
    public Result<List<Role>> getRolesByUserId(@PathVariable Long userId) {
        return Result.success(roleService.getRolesByUserId(userId));
    }

    @Operation(summary = "分配角色权限")
    @PostMapping("/{roleId}/permissions")
    public Result<Void> assignPermissions(@PathVariable Long roleId, @RequestBody List<Long> permissionIds) {
        roleService.assignPermissions(roleId, permissionIds);
        return Result.success();
    }

    @Operation(summary = "获取角色的权限ID列表")
    @GetMapping("/{roleId}/permissions")
    public Result<List<Long>> getPermissionIdsByRoleId(@PathVariable Long roleId) {
        return Result.success(roleService.getPermissionIdsByRoleId(roleId));
    }
}
