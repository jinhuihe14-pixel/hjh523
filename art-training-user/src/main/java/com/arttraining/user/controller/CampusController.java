package com.arttraining.user.controller;

import com.arttraining.common.page.PageResult;
import com.arttraining.common.result.Result;
import com.arttraining.user.entity.Campus;
import com.arttraining.user.service.CampusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "校区管理")
@RestController
@RequestMapping("/api/campus")
@RequiredArgsConstructor
public class CampusController {

    private final CampusService campusService;

    @Operation(summary = "分页查询校区")
    @GetMapping("/page")
    public Result<PageResult<Campus>> page(@RequestParam(defaultValue = "1") Integer current,
                                           @RequestParam(defaultValue = "10") Integer size,
                                           @RequestParam(required = false) String keyword,
                                           @RequestParam(required = false) Integer status) {
        return Result.success(campusService.page(current, size, keyword, status));
    }

    @Operation(summary = "获取校区详情")
    @GetMapping("/{id}")
    public Result<Campus> getById(@PathVariable Long id) {
        return Result.success(campusService.getById(id));
    }

    @Operation(summary = "根据编码获取校区")
    @GetMapping("/code/{campusCode}")
    public Result<Campus> getByCode(@PathVariable String campusCode) {
        return Result.success(campusService.getByCode(campusCode));
    }

    @Operation(summary = "新增校区")
    @PostMapping
    public Result<Void> save(@RequestBody Campus campus) {
        campusService.save(campus);
        return Result.success();
    }

    @Operation(summary = "修改校区")
    @PutMapping
    public Result<Void> update(@RequestBody Campus campus) {
        campusService.update(campus);
        return Result.success();
    }

    @Operation(summary = "删除校区")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        campusService.delete(id);
        return Result.success();
    }

    @Operation(summary = "批量删除校区")
    @DeleteMapping("/batch")
    public Result<Void> deleteBatch(@RequestBody List<Long> ids) {
        campusService.deleteBatch(ids);
        return Result.success();
    }

    @Operation(summary = "修改校区状态")
    @PutMapping("/status")
    public Result<Void> updateStatus(@RequestParam Long id, @RequestParam Integer status) {
        campusService.updateStatus(id, status);
        return Result.success();
    }

    @Operation(summary = "获取全部校区列表")
    @GetMapping("/list")
    public Result<List<Campus>> listAll() {
        return Result.success(campusService.listAll());
    }
}
