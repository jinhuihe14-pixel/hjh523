package com.arttraining.user.controller;

import com.arttraining.common.page.PageResult;
import com.arttraining.common.page.PageQuery;
import com.arttraining.common.result.Result;
import com.arttraining.user.dto.UserQueryDTO;
import com.arttraining.user.dto.UserSaveDTO;
import com.arttraining.user.service.UserService;
import com.arttraining.user.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "用户管理")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "分页查询用户")
    @GetMapping("/page")
    public Result<PageResult<UserVO>> page(@RequestParam(defaultValue = "1") Integer current,
                                           @RequestParam(defaultValue = "10") Integer size,
                                           UserQueryDTO query) {
        return Result.success(userService.page(current, size, query));
    }

    @Operation(summary = "获取用户详情")
    @GetMapping("/{id}")
    public Result<UserVO> getById(@PathVariable Long id) {
        return Result.success(userService.getById(id));
    }

    @Operation(summary = "根据用户名获取用户")
    @GetMapping("/username/{username}")
    public Result<UserVO> getByUsername(@PathVariable String username) {
        return Result.success(userService.getByUsername(username));
    }

    @Operation(summary = "新增用户")
    @PostMapping
    public Result<Void> save(@Valid @RequestBody UserSaveDTO dto) {
        userService.save(dto);
        return Result.success();
    }

    @Operation(summary = "修改用户")
    @PutMapping
    public Result<Void> update(@Valid @RequestBody UserSaveDTO dto) {
        userService.update(dto);
        return Result.success();
    }

    @Operation(summary = "删除用户")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return Result.success();
    }

    @Operation(summary = "批量删除用户")
    @DeleteMapping("/batch")
    public Result<Void> deleteBatch(@RequestBody List<Long> ids) {
        userService.deleteBatch(ids);
        return Result.success();
    }

    @Operation(summary = "修改用户状态")
    @PutMapping("/status")
    public Result<Void> updateStatus(@RequestParam Long id, @RequestParam Integer status) {
        userService.updateStatus(id, status);
        return Result.success();
    }

    @Operation(summary = "重置密码")
    @PutMapping("/reset-password")
    public Result<Void> resetPassword(@RequestParam Long id, @RequestParam String password) {
        userService.resetPassword(id, password);
        return Result.success();
    }

    @Operation(summary = "根据用户类型获取用户列表")
    @GetMapping("/type/{userType}")
    public Result<List<UserVO>> listByUserType(@PathVariable Integer userType) {
        return Result.success(userService.listByUserType(userType));
    }

    @Operation(summary = "根据校区获取用户列表")
    @GetMapping("/campus/{campusId}")
    public Result<List<UserVO>> listByCampusId(@PathVariable Long campusId) {
        return Result.success(userService.listByCampusId(campusId));
    }
}
