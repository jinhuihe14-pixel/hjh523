package com.arttraining.business.controller;

import com.arttraining.business.entity.Order;
import com.arttraining.business.entity.OrderItem;
import com.arttraining.business.entity.OperationLog;
import com.arttraining.business.service.OperationLogService;
import com.arttraining.business.service.OrderService;
import com.arttraining.common.page.PageResult;
import com.arttraining.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Tag(name = "订单管理")
@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OperationLogService operationLogService;

    @Operation(summary = "分页查询订单")
    @GetMapping("/page")
    public Result<PageResult<Order>> page(@RequestParam(defaultValue = "1") Integer current,
                                           @RequestParam(defaultValue = "10") Integer size,
                                           @RequestParam(required = false) String keyword,
                                           @RequestParam(required = false) Integer orderType,
                                           @RequestParam(required = false) Integer orderStatus,
                                           @RequestParam(required = false) Integer urgent,
                                           @RequestParam(required = false) Integer paymentStatus,
                                           @RequestParam(required = false) Long customerId,
                                           @RequestParam(required = false) String startDate,
                                           @RequestParam(required = false) String endDate) {
        return Result.success(orderService.page(current, size, keyword, orderType, orderStatus,
                urgent, paymentStatus, customerId, startDate, endDate));
    }

    @Operation(summary = "获取订单详情")
    @GetMapping("/{id}")
    public Result<Order> getById(@PathVariable Long id) {
        return Result.success(orderService.getById(id));
    }

    @Operation(summary = "根据编号获取订单")
    @GetMapping("/no/{orderNo}")
    public Result<Order> getByNo(@PathVariable String orderNo) {
        return Result.success(orderService.getByNo(orderNo));
    }

    @Operation(summary = "获取订单明细")
    @GetMapping("/item/list")
    public Result<List<OrderItem>> getItems(@RequestParam Long orderId) {
        return Result.success(orderService.getItems(orderId));
    }

    @Operation(summary = "新增订单")
    @PostMapping
    public Result<Void> save(@RequestBody Map<String, Object> params) {
        var objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
        Order order = objectMapper.convertValue(params.get("order"), Order.class);
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> itemList = (List<Map<String, Object>>) params.get("items");
        List<OrderItem> items = itemList.stream()
                .map(item -> objectMapper.convertValue(item, OrderItem.class))
                .toList();
        orderService.save(order, items);
        return Result.success();
    }

    @Operation(summary = "修改订单")
    @PutMapping
    public Result<Void> update(@RequestBody Map<String, Object> params) {
        var objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
        Order order = objectMapper.convertValue(params.get("order"), Order.class);
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> itemList = (List<Map<String, Object>>) params.get("items");
        List<OrderItem> items = itemList != null ? itemList.stream()
                .map(item -> objectMapper.convertValue(item, OrderItem.class))
                .toList() : null;
        orderService.update(order, items);
        return Result.success();
    }

    @Operation(summary = "删除订单")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        orderService.delete(id);
        return Result.success();
    }

    @Operation(summary = "批量删除订单")
    @DeleteMapping("/batch")
    public Result<Void> deleteBatch(@RequestBody List<Long> ids) {
        orderService.deleteBatch(ids);
        return Result.success();
    }

    @Operation(summary = "修改订单状态")
    @PutMapping("/status")
    public Result<Void> updateStatus(@RequestParam Long id, @RequestParam Integer status,
                                      @RequestParam(required = false) String remark) {
        orderService.updateStatus(id, status, remark);
        return Result.success();
    }

    @Operation(summary = "订单复核")
    @PutMapping("/review")
    public Result<Void> review(@RequestParam Long id, @RequestParam(required = false) String remark,
                               @RequestParam(defaultValue = "true") Boolean pass) {
        orderService.review(id, remark, pass);
        return Result.success();
    }

    @Operation(summary = "设置加急")
    @PutMapping("/urgent")
    public Result<Void> setUrgent(@RequestParam Long id, @RequestParam Integer urgent) {
        orderService.setUrgent(id, urgent);
        return Result.success();
    }

    @Operation(summary = "登记收款")
    @PutMapping("/payment")
    public Result<Void> updatePayment(@RequestParam Long id,
                                       @RequestParam(required = false) BigDecimal receivedAmount,
                                       @RequestParam(required = false) Integer paymentStatus) {
        orderService.updatePayment(id, receivedAmount, paymentStatus);
        return Result.success();
    }

    @Operation(summary = "客户订单列表")
    @GetMapping("/customer/list")
    public Result<List<Order>> listByCustomer(@RequestParam Long customerId) {
        return Result.success(orderService.listByCustomer(customerId));
    }

    @Operation(summary = "订单操作记录")
    @GetMapping("/operationLog/{id}")
    public Result<List<OperationLog>> operationLogs(@PathVariable Long id) {
        return Result.success(operationLogService.listByBiz("order", id));
    }
}
