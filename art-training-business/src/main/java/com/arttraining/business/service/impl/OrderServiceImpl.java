package com.arttraining.business.service.impl;

import cn.hutool.core.util.StrUtil;
import com.arttraining.business.entity.Order;
import com.arttraining.business.entity.OrderItem;
import com.arttraining.business.mapper.OrderItemMapper;
import com.arttraining.business.mapper.OrderMapper;
import com.arttraining.business.service.CustomerService;
import com.arttraining.business.service.OperationLogService;
import com.arttraining.business.service.OrderService;
import com.arttraining.common.context.UserContextHolder;
import com.arttraining.common.exception.BusinessException;
import com.arttraining.common.page.PageResult;
import com.arttraining.common.result.ResultCode;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final CustomerService customerService;
    private final OperationLogService operationLogService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public PageResult<Order> page(Integer current, Integer size, String keyword, Integer orderType,
                                   Integer orderStatus, Integer urgent, Integer paymentStatus,
                                   Long customerId, String startDate, String endDate) {
        Page<Order> page = new Page<>(current, size);
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();

        if (StrUtil.isNotBlank(keyword)) {
            wrapper.and(w -> w.like(Order::getOrderNo, keyword)
                    .or().like(Order::getOrderName, keyword)
                    .or().like(Order::getCustomerName, keyword)
                    .or().like(Order::getContactPhone, keyword));
        }
        if (orderType != null) {
            wrapper.eq(Order::getOrderType, orderType);
        }
        if (orderStatus != null) {
            wrapper.eq(Order::getOrderStatus, orderStatus);
        }
        if (urgent != null) {
            wrapper.eq(Order::getUrgent, urgent);
        }
        if (paymentStatus != null) {
            wrapper.eq(Order::getPaymentStatus, paymentStatus);
        }
        if (customerId != null) {
            wrapper.eq(Order::getCustomerId, customerId);
        }
        if (StrUtil.isNotBlank(startDate)) {
            wrapper.ge(Order::getCreateTime, LocalDate.parse(startDate).atStartOfDay());
        }
        if (StrUtil.isNotBlank(endDate)) {
            wrapper.le(Order::getCreateTime, LocalDate.parse(endDate).atTime(23, 59, 59));
        }

        Long campusId = UserContextHolder.getCampusId();
        if (campusId != null) {
            wrapper.eq(Order::getCampusId, campusId);
        }

        wrapper.orderByDesc(Order::getUrgent)
                .orderByDesc(Order::getCreateTime);

        return PageResult.of(page(page, wrapper));
    }

    @Override
    public Order getById(Long id) {
        return orderMapper.selectById(id);
    }

    @Override
    public Order getByNo(String orderNo) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getOrderNo, orderNo);
        return getOne(wrapper);
    }

    @Override
    public List<OrderItem> getItems(Long orderId) {
        LambdaQueryWrapper<OrderItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderItem::getOrderId, orderId);
        wrapper.orderByAsc(OrderItem::getSortOrder).orderByAsc(OrderItem::getId);
        return orderItemMapper.selectList(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(Order order, List<OrderItem> items) {
        if (StrUtil.isBlank(order.getOrderNo())) {
            order.setOrderNo(generateOrderNo());
        }

        if (order.getCampusId() == null) {
            order.setCampusId(UserContextHolder.getCampusId());
            order.setCampusCode(UserContextHolder.getCampusCode());
        }

        if (order.getOrderStatus() == null) {
            order.setOrderStatus(1);
        }
        if (order.getPaymentStatus() == null) {
            order.setPaymentStatus(0);
        }
        if (order.getUrgent() == null) {
            order.setUrgent(0);
        }
        if (order.getReceivedAmount() == null) {
            order.setReceivedAmount(BigDecimal.ZERO);
        }
        if (order.getTotalAmount() == null) {
            order.setTotalAmount(BigDecimal.ZERO);
        }

        if (order.getCustomerId() != null) {
            var customer = customerService.getById(order.getCustomerId());
            if (customer != null) {
                order.setCustomerName(customer.getCustomerName());
            }
        }

        orderMapper.insert(order);

        if (items != null && !items.isEmpty()) {
            BigDecimal totalAmount = BigDecimal.ZERO;
            int sort = 0;
            for (OrderItem item : items) {
                item.setOrderId(order.getId());
                item.setOrderNo(order.getOrderNo());
                item.setSortOrder(sort++);
                if (item.getQuantity() == null) item.setQuantity(BigDecimal.ONE);
                if (item.getUnitPrice() == null) item.setUnitPrice(BigDecimal.ZERO);
                if (item.getAmount() == null) {
                    item.setAmount(item.getUnitPrice().multiply(item.getQuantity()));
                }
                totalAmount = totalAmount.add(item.getAmount());
                orderItemMapper.insert(item);
            }
            if (order.getTotalAmount().compareTo(BigDecimal.ZERO) == 0) {
                order.setTotalAmount(totalAmount);
                orderMapper.updateById(order);
            }
        }

        updateCustomerOrderStats(order.getCustomerId());

        operationLogService.log("order", order.getId(), order.getOrderNo(),
                "create", "新增订单", "新增订单：" + order.getOrderName(),
                null, toJson(order));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Order order, List<OrderItem> items) {
        Order before = getById(order.getId());
        if (before == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST.getCode(), "订单不存在");
        }

        if (order.getCustomerId() != null && !order.getCustomerId().equals(before.getCustomerId())) {
            var customer = customerService.getById(order.getCustomerId());
            if (customer != null) {
                order.setCustomerName(customer.getCustomerName());
            }
        }

        orderMapper.updateById(order);

        if (items != null) {
            LambdaQueryWrapper<OrderItem> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(OrderItem::getOrderId, order.getId());
            orderItemMapper.delete(wrapper);

            BigDecimal totalAmount = BigDecimal.ZERO;
            int sort = 0;
            for (OrderItem item : items) {
                item.setId(null);
                item.setOrderId(order.getId());
                item.setOrderNo(order.getOrderNo());
                item.setSortOrder(sort++);
                if (item.getQuantity() == null) item.setQuantity(BigDecimal.ONE);
                if (item.getUnitPrice() == null) item.setUnitPrice(BigDecimal.ZERO);
                if (item.getAmount() == null) {
                    item.setAmount(item.getUnitPrice().multiply(item.getQuantity()));
                }
                totalAmount = totalAmount.add(item.getAmount());
                orderItemMapper.insert(item);
            }

            if (order.getTotalAmount() == null) {
                Order updateOrder = new Order();
                updateOrder.setId(order.getId());
                updateOrder.setTotalAmount(totalAmount);
                orderMapper.updateById(updateOrder);
            }
        }

        operationLogService.log("order", order.getId(), order.getOrderNo(),
                "update", "编辑订单", "编辑订单信息",
                toJson(before), toJson(order));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        Order order = getById(id);
        if (order == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST.getCode(), "订单不存在");
        }
        orderMapper.deleteById(id);

        LambdaQueryWrapper<OrderItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderItem::getOrderId, id);
        orderItemMapper.delete(wrapper);

        updateCustomerOrderStats(order.getCustomerId());

        operationLogService.log("order", id, order.getOrderNo(),
                "delete", "删除订单", "删除订单：" + order.getOrderName(),
                toJson(order), null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBatch(List<Long> ids) {
        for (Long id : ids) {
            delete(id);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, Integer status, String remark) {
        Order before = getById(id);
        if (before == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST.getCode(), "订单不存在");
        }

        Order order = new Order();
        order.setId(id);
        order.setOrderStatus(status);

        if (status == 5 && before.getOrderStatus() == 4) {
            order.setReviewerId(UserContextHolder.getUserId());
            order.setReviewerName(UserContextHolder.getRealName() != null ? UserContextHolder.getRealName() : UserContextHolder.getUsername());
            order.setReviewTime(LocalDateTime.now());
            order.setReviewRemark(remark);
        }

        if (status == 6) {
            order.setActualDate(LocalDate.now());
        }

        orderMapper.updateById(order);

        String statusName = getStatusName(status);
        operationLogService.log("order", id, before.getOrderNo(),
                "status_change", "状态变更", "订单状态变更为：" + statusName + (remark != null ? "，备注：" + remark : ""),
                null, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void review(Long id, String remark, Boolean pass) {
        Order before = getById(id);
        if (before == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST.getCode(), "订单不存在");
        }
        if (before.getOrderStatus() != 4) {
            throw new BusinessException(ResultCode.BUSINESS_ERROR.getCode(), "只有待复核状态的订单才能复核");
        }

        Order order = new Order();
        order.setId(id);
        order.setReviewerId(UserContextHolder.getUserId());
        order.setReviewerName(UserContextHolder.getRealName() != null ? UserContextHolder.getRealName() : UserContextHolder.getUsername());
        order.setReviewTime(LocalDateTime.now());
        order.setReviewRemark(remark);

        if (Boolean.TRUE.equals(pass)) {
            order.setOrderStatus(5);
        } else {
            order.setOrderStatus(3);
        }

        orderMapper.updateById(order);

        operationLogService.log("order", id, before.getOrderNo(),
                "review", "订单复核", "复核" + (Boolean.TRUE.equals(pass) ? "通过" : "不通过") + (remark != null ? "，备注：" + remark : ""),
                null, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setUrgent(Long id, Integer urgent) {
        Order order = new Order();
        order.setId(id);
        order.setUrgent(urgent);
        orderMapper.updateById(order);

        Order before = getById(id);
        operationLogService.log("order", id, before.getOrderNo(),
                "update", "加急标记", "设置订单" + (urgent == 1 ? "加急" : "普通"),
                null, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePayment(Long id, BigDecimal receivedAmount, Integer paymentStatus) {
        Order order = new Order();
        order.setId(id);
        if (receivedAmount != null) {
            order.setReceivedAmount(receivedAmount);
        }
        if (paymentStatus != null) {
            order.setPaymentStatus(paymentStatus);
        }
        orderMapper.updateById(order);

        Order before = getById(id);
        operationLogService.log("order", id, before.getOrderNo(),
                "update", "收款登记", "登记收款：" + receivedAmount + "，付款状态：" + getPaymentStatusName(paymentStatus),
                null, null);
    }

    @Override
    public List<Order> listByCustomer(Long customerId) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getCustomerId, customerId);
        wrapper.orderByDesc(Order::getCreateTime);
        wrapper.last("limit 100");
        return list(wrapper);
    }

    private String generateOrderNo() {
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.likeRight(Order::getOrderNo, "ORD" + dateStr);
        wrapper.orderByDesc(Order::getOrderNo);
        wrapper.last("limit 1");
        Order last = getOne(wrapper);

        int seq = 1;
        if (last != null && last.getOrderNo() != null) {
            String seqStr = last.getOrderNo().substring(11);
            seq = Integer.parseInt(seqStr) + 1;
        }
        return "ORD" + dateStr + String.format("%04d", seq);
    }

    private void updateCustomerOrderStats(Long customerId) {
        if (customerId == null) return;

        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getCustomerId, customerId);
        wrapper.ne(Order::getOrderStatus, 7);
        List<Order> orders = list(wrapper);

        var customer = customerService.getById(customerId);
        if (customer != null) {
            customer.setOrderCount(orders.size());
            BigDecimal total = orders.stream()
                    .map(Order::getTotalAmount)
                    .filter(java.util.Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            customer.setTotalAmount(total);

            if (!orders.isEmpty()) {
                customer.setLastOrderTime(orders.get(0).getCreateTime());
            }

            customerService.update(customer);
        }
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

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
