package com.arttraining.business.controller;

import com.arttraining.business.entity.Customer;
import com.arttraining.business.entity.CustomerConsume;
import com.arttraining.business.entity.OperationLog;
import com.arttraining.business.service.CustomerService;
import com.arttraining.business.service.OperationLogService;
import com.arttraining.common.page.PageResult;
import com.arttraining.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "客户管理")
@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;
    private final OperationLogService operationLogService;

    @Operation(summary = "分页查询客户")
    @GetMapping("/page")
    public Result<PageResult<Customer>> page(@RequestParam(defaultValue = "1") Integer current,
                                             @RequestParam(defaultValue = "10") Integer size,
                                             @RequestParam(required = false) String keyword,
                                             @RequestParam(required = false) Integer customerType,
                                             @RequestParam(required = false) Integer level,
                                             @RequestParam(required = false) Integer status) {
        return Result.success(customerService.page(current, size, keyword, customerType, level, status));
    }

    @Operation(summary = "获取客户详情")
    @GetMapping("/{id}")
    public Result<Customer> getById(@PathVariable Long id) {
        return Result.success(customerService.getById(id));
    }

    @Operation(summary = "根据编号获取客户")
    @GetMapping("/no/{customerNo}")
    public Result<Customer> getByNo(@PathVariable String customerNo) {
        return Result.success(customerService.getByNo(customerNo));
    }

    @Operation(summary = "新增客户")
    @PostMapping
    public Result<Void> save(@RequestBody Customer customer) {
        customerService.save(customer);
        return Result.success();
    }

    @Operation(summary = "修改客户")
    @PutMapping
    public Result<Void> update(@RequestBody Customer customer) {
        customerService.update(customer);
        return Result.success();
    }

    @Operation(summary = "删除客户")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        customerService.delete(id);
        return Result.success();
    }

    @Operation(summary = "批量删除客户")
    @DeleteMapping("/batch")
    public Result<Void> deleteBatch(@RequestBody List<Long> ids) {
        customerService.deleteBatch(ids);
        return Result.success();
    }

    @Operation(summary = "修改客户状态")
    @PutMapping("/status")
    public Result<Void> updateStatus(@RequestParam Long id, @RequestParam Integer status) {
        customerService.updateStatus(id, status);
        return Result.success();
    }

    @Operation(summary = "获取全部客户列表")
    @GetMapping("/list")
    public Result<List<Customer>> listAll() {
        return Result.success(customerService.listAll());
    }

    @Operation(summary = "客户消费记录分页")
    @GetMapping("/consume/page")
    public Result<PageResult<CustomerConsume>> consumePage(@RequestParam Long customerId,
                                                           @RequestParam(defaultValue = "1") Integer current,
                                                           @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(customerService.consumePage(customerId, current, size));
    }

    @Operation(summary = "客户消费记录列表")
    @GetMapping("/consume/list")
    public Result<List<CustomerConsume>> listConsume(@RequestParam Long customerId) {
        return Result.success(customerService.listConsumeByCustomer(customerId));
    }

    @Operation(summary = "新增消费记录")
    @PostMapping("/consume")
    public Result<Void> addConsume(@RequestBody CustomerConsume consume) {
        customerService.addConsume(consume);
        return Result.success();
    }

    @Operation(summary = "客户操作记录")
    @GetMapping("/operationLog/{id}")
    public Result<List<OperationLog>> operationLogs(@PathVariable Long id) {
        return Result.success(operationLogService.listByBiz("customer", id));
    }
}
