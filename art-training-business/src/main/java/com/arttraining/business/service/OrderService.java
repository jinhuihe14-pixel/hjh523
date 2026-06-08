package com.arttraining.business.service;

import com.arttraining.business.entity.Order;
import com.arttraining.business.entity.OrderItem;
import com.arttraining.common.page.PageResult;

import java.util.List;

public interface OrderService {

    PageResult<Order> page(Integer current, Integer size, String keyword, Integer orderType,
                           Integer orderStatus, Integer urgent, Integer paymentStatus,
                           Long customerId, String startDate, String endDate);

    Order getById(Long id);

    Order getByNo(String orderNo);

    List<OrderItem> getItems(Long orderId);

    void save(Order order, List<OrderItem> items);

    void update(Order order, List<OrderItem> items);

    void delete(Long id);

    void deleteBatch(List<Long> ids);

    void updateStatus(Long id, Integer status, String remark);

    void review(Long id, String remark, Boolean pass);

    void setUrgent(Long id, Integer urgent);

    void updatePayment(Long id, java.math.BigDecimal receivedAmount, Integer paymentStatus);

    List<Order> listByCustomer(Long customerId);
}
