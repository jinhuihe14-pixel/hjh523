package com.arttraining.business.controller;

import com.arttraining.business.entity.OperationLog;
import com.arttraining.business.entity.Order;
import com.arttraining.business.service.OperationLogService;
import com.arttraining.business.service.OrderService;
import com.arttraining.common.page.PageResult;
import com.arttraining.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Tag(name = "报表管理")
@RestController
@RequestMapping("/api/report")
@RequiredArgsConstructor
public class ReportController {

    private final OrderService orderService;
    private final OperationLogService operationLogService;

    @Operation(summary = "业绩统计概览")
    @GetMapping("/overview")
    public Result<Map<String, Object>> overview(@RequestParam(required = false) String startDate,
                                                @RequestParam(required = false) String endDate,
                                                @RequestParam(required = false) Integer orderType,
                                                @RequestParam(required = false) Long customerId) {
        PageResult<Order> pageResult = orderService.page(1, 10000, null, orderType,
                null, null, null, customerId, startDate, endDate);

        List<Order> orders = pageResult.getRecords();

        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal receivedAmount = BigDecimal.ZERO;
        int orderCount = orders.size();
        int urgentCount = 0;
        int completedCount = 0;

        Map<Integer, BigDecimal> typeAmountMap = new HashMap<>();
        Map<String, BigDecimal> dailyAmountMap = new TreeMap<>();

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (Order order : orders) {
            if (order.getTotalAmount() != null) {
                totalAmount = totalAmount.add(order.getTotalAmount());
            }
            if (order.getReceivedAmount() != null) {
                receivedAmount = receivedAmount.add(order.getReceivedAmount());
            }
            if (order.getUrgent() != null && order.getUrgent() == 1) {
                urgentCount++;
            }
            if (order.getOrderStatus() != null && order.getOrderStatus() >= 5) {
                completedCount++;
            }

            Integer type = order.getOrderType();
            if (type != null) {
                BigDecimal typeTotal = typeAmountMap.getOrDefault(type, BigDecimal.ZERO);
                if (order.getTotalAmount() != null) {
                    typeAmountMap.put(type, typeTotal.add(order.getTotalAmount()));
                }
            }

            if (order.getCreateTime() != null) {
                String dateStr = order.getCreateTime().format(dateFormatter);
                BigDecimal dailyTotal = dailyAmountMap.getOrDefault(dateStr, BigDecimal.ZERO);
                if (order.getTotalAmount() != null) {
                    dailyAmountMap.put(dateStr, dailyTotal.add(order.getTotalAmount()));
                }
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("totalAmount", totalAmount);
        result.put("receivedAmount", receivedAmount);
        result.put("orderCount", orderCount);
        result.put("urgentCount", urgentCount);
        result.put("completedCount", completedCount);
        result.put("completionRate", orderCount > 0 ? (double) completedCount / orderCount : 0);
        result.put("typeAmountMap", typeAmountMap);
        result.put("dailyAmountMap", dailyAmountMap);

        return Result.success(result);
    }

    @Operation(summary = "按客户统计业绩")
    @GetMapping("/byCustomer")
    public Result<List<Map<String, Object>>> byCustomer(@RequestParam(required = false) String startDate,
                                                         @RequestParam(required = false) String endDate,
                                                         @RequestParam(required = false) Integer orderType) {
        PageResult<Order> pageResult = orderService.page(1, 10000, null, orderType,
                null, null, null, null, startDate, endDate);

        List<Order> orders = pageResult.getRecords();

        Map<Long, Map<String, Object>> customerMap = new HashMap<>();

        for (Order order : orders) {
            Long customerId = order.getCustomerId();
            if (customerId == null) continue;

            Map<String, Object> data = customerMap.computeIfAbsent(customerId, k -> {
                Map<String, Object> m = new HashMap<>();
                m.put("customerId", customerId);
                m.put("customerName", order.getCustomerName());
                m.put("orderCount", 0);
                m.put("totalAmount", BigDecimal.ZERO);
                m.put("receivedAmount", BigDecimal.ZERO);
                return m;
            });

            data.put("orderCount", (Integer) data.get("orderCount") + 1);
            if (order.getTotalAmount() != null) {
                data.put("totalAmount", ((BigDecimal) data.get("totalAmount")).add(order.getTotalAmount()));
            }
            if (order.getReceivedAmount() != null) {
                data.put("receivedAmount", ((BigDecimal) data.get("receivedAmount")).add(order.getReceivedAmount()));
            }
        }

        List<Map<String, Object>> result = new ArrayList<>(customerMap.values());
        result.sort((a, b) -> ((BigDecimal) b.get("totalAmount")).compareTo((BigDecimal) a.get("totalAmount")));

        return Result.success(result);
    }

    @Operation(summary = "按制作类型统计")
    @GetMapping("/byType")
    public Result<List<Map<String, Object>>> byType(@RequestParam(required = false) String startDate,
                                                     @RequestParam(required = false) String endDate,
                                                     @RequestParam(required = false) Long customerId) {
        PageResult<Order> pageResult = orderService.page(1, 10000, null, null,
                null, null, null, customerId, startDate, endDate);

        List<Order> orders = pageResult.getRecords();

        Map<Integer, Map<String, Object>> typeMap = new HashMap<>();

        for (Order order : orders) {
            Integer type = order.getOrderType();
            if (type == null) type = 6;

            Map<String, Object> data = typeMap.computeIfAbsent(type, k -> {
                Map<String, Object> m = new HashMap<>();
                m.put("orderType", type);
                m.put("typeName", getTypeName(type));
                m.put("orderCount", 0);
                m.put("totalAmount", BigDecimal.ZERO);
                return m;
            });

            data.put("orderCount", (Integer) data.get("orderCount") + 1);
            if (order.getTotalAmount() != null) {
                data.put("totalAmount", ((BigDecimal) data.get("totalAmount")).add(order.getTotalAmount()));
            }
        }

        List<Map<String, Object>> result = new ArrayList<>(typeMap.values());
        result.sort((a, b) -> ((BigDecimal) b.get("totalAmount")).compareTo((BigDecimal) a.get("totalAmount")));

        return Result.success(result);
    }

    @Operation(summary = "导出订单报表")
    @GetMapping("/export/order")
    public ResponseEntity<byte[]> exportOrder(@RequestParam(required = false) String keyword,
                                               @RequestParam(required = false) Integer orderType,
                                               @RequestParam(required = false) Integer orderStatus,
                                               @RequestParam(required = false) Integer urgent,
                                               @RequestParam(required = false) Integer paymentStatus,
                                               @RequestParam(required = false) Long customerId,
                                               @RequestParam(required = false) String startDate,
                                               @RequestParam(required = false) String endDate) throws Exception {
        PageResult<Order> pageResult = orderService.page(1, 10000, keyword, orderType,
                orderStatus, urgent, paymentStatus, customerId, startDate, endDate);

        List<Order> orders = pageResult.getRecords();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("订单报表");

        Row headerRow = sheet.createRow(0);
        String[] headers = {"订单编号", "订单名称", "客户名称", "联系人", "联系电话",
                "订单类型", "订单状态", "是否加急", "订单金额", "已收金额",
                "付款状态", "预计交付日期", "实际交付日期", "创建时间"};
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        int rowNum = 1;
        for (Order order : orders) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(order.getOrderNo() != null ? order.getOrderNo() : "");
            row.createCell(1).setCellValue(order.getOrderName() != null ? order.getOrderName() : "");
            row.createCell(2).setCellValue(order.getCustomerName() != null ? order.getCustomerName() : "");
            row.createCell(3).setCellValue(order.getContactPerson() != null ? order.getContactPerson() : "");
            row.createCell(4).setCellValue(order.getContactPhone() != null ? order.getContactPhone() : "");
            row.createCell(5).setCellValue(getTypeName(order.getOrderType()));
            row.createCell(6).setCellValue(getStatusName(order.getOrderStatus()));
            row.createCell(7).setCellValue(order.getUrgent() != null && order.getUrgent() == 1 ? "是" : "否");
            row.createCell(8).setCellValue(order.getTotalAmount() != null ? order.getTotalAmount().doubleValue() : 0);
            row.createCell(9).setCellValue(order.getReceivedAmount() != null ? order.getReceivedAmount().doubleValue() : 0);
            row.createCell(10).setCellValue(getPaymentStatusName(order.getPaymentStatus()));
            row.createCell(11).setCellValue(order.getExpectDate() != null ? order.getExpectDate().toString() : "");
            row.createCell(12).setCellValue(order.getActualDate() != null ? order.getActualDate().toString() : "");
            row.createCell(13).setCellValue(order.getCreateTime() != null ? order.getCreateTime().format(dateFormatter) : "");
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        String fileName = "订单报表_" + java.time.LocalDate.now() + ".xlsx";
        String encodedFileName = java.net.URLEncoder.encode(fileName, StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFileName)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(out.toByteArray());
    }

    @Operation(summary = "操作记录分页")
    @GetMapping("/operationLog/page")
    public Result<PageResult<OperationLog>> operationLogPage(@RequestParam(defaultValue = "1") Integer current,
                                                              @RequestParam(defaultValue = "10") Integer size,
                                                              @RequestParam(required = false) String bizType,
                                                              @RequestParam(required = false) Long bizId,
                                                              @RequestParam(required = false) String operationType,
                                                              @RequestParam(required = false) String operatorName,
                                                              @RequestParam(required = false) String startTime,
                                                              @RequestParam(required = false) String endTime) {
        return Result.success(operationLogService.page(current, size, bizType, bizId,
                operationType, operatorName, startTime, endTime));
    }

    private String getTypeName(Integer type) {
        if (type == null) return "其他";
        return switch (type) {
            case 1 -> "喷绘";
            case 2 -> "写真";
            case 3 -> "雕刻";
            case 4 -> "印刷";
            case 5 -> "标识标牌";
            case 6 -> "其他";
            default -> "未知";
        };
    }

    private String getStatusName(Integer status) {
        if (status == null) return "";
        return switch (status) {
            case 1 -> "待确认";
            case 2 -> "已确认";
            case 3 -> "制作中";
            case 4 -> "待复核";
            case 5 -> "已完成";
            case 6 -> "已取件";
            case 7 -> "已取消";
            default -> "未知";
        };
    }

    private String getPaymentStatusName(Integer status) {
        if (status == null) return "";
        return switch (status) {
            case 0 -> "未付款";
            case 1 -> "部分付款";
            case 2 -> "已付清";
            default -> "未知";
        };
    }
}
