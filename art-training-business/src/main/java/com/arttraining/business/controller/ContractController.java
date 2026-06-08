package com.arttraining.business.controller;

import com.arttraining.business.entity.Contract;
import com.arttraining.business.entity.OperationLog;
import com.arttraining.business.service.ContractService;
import com.arttraining.business.service.OperationLogService;
import com.arttraining.common.page.PageResult;
import com.arttraining.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "合同管理")
@RestController
@RequestMapping("/api/contract")
@RequiredArgsConstructor
public class ContractController {

    private final ContractService contractService;
    private final OperationLogService operationLogService;

    @Operation(summary = "分页查询合同")
    @GetMapping("/page")
    public Result<PageResult<Contract>> page(@RequestParam(defaultValue = "1") Integer current,
                                            @RequestParam(defaultValue = "10") Integer size,
                                            @RequestParam(required = false) String keyword,
                                            @RequestParam(required = false) Integer contractType,
                                            @RequestParam(required = false) Integer status,
                                            @RequestParam(required = false) Long customerId,
                                            @RequestParam(required = false) String startDate,
                                            @RequestParam(required = false) String endDate) {
        return Result.success(contractService.page(current, size, keyword, contractType,
                status, customerId, startDate, endDate));
    }

    @Operation(summary = "获取合同详情")
    @GetMapping("/{id}")
    public Result<Contract> getById(@PathVariable Long id) {
        return Result.success(contractService.getById(id));
    }

    @Operation(summary = "根据编号获取合同")
    @GetMapping("/no/{contractNo}")
    public Result<Contract> getByNo(@PathVariable String contractNo) {
        return Result.success(contractService.getByNo(contractNo));
    }

    @Operation(summary = "新增合同")
    @PostMapping
    public Result<Void> save(@RequestBody Contract contract) {
        contractService.save(contract);
        return Result.success();
    }

    @Operation(summary = "修改合同")
    @PutMapping
    public Result<Void> update(@RequestBody Contract contract) {
        contractService.update(contract);
        return Result.success();
    }

    @Operation(summary = "删除合同")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        contractService.delete(id);
        return Result.success();
    }

    @Operation(summary = "批量删除合同")
    @DeleteMapping("/batch")
    public Result<Void> deleteBatch(@RequestBody List<Long> ids) {
        contractService.deleteBatch(ids);
        return Result.success();
    }

    @Operation(summary = "修改合同状态")
    @PutMapping("/status")
    public Result<Void> updateStatus(@RequestParam Long id, @RequestParam Integer status) {
        contractService.updateStatus(id, status);
        return Result.success();
    }

    @Operation(summary = "客户合同列表")
    @GetMapping("/customer/list")
    public Result<List<Contract>> listByCustomer(@RequestParam Long customerId) {
        return Result.success(contractService.listByCustomer(customerId));
    }

    @Operation(summary = "即将到期的合同")
    @GetMapping("/expiring/soon")
    public Result<List<Contract>> listExpiringSoon(@RequestParam(defaultValue = "30") Integer days) {
        return Result.success(contractService.listExpiringSoon(days));
    }

    @Operation(summary = "合同操作记录")
    @GetMapping("/operationLog/{id}")
    public Result<List<OperationLog>> operationLogs(@PathVariable Long id) {
        return Result.success(operationLogService.listByBiz("contract", id));
    }
}
