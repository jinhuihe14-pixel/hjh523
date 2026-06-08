package com.arttraining.business.service.impl;

import cn.hutool.core.util.StrUtil;
import com.arttraining.business.entity.Customer;
import com.arttraining.business.entity.CustomerConsume;
import com.arttraining.business.mapper.CustomerConsumeMapper;
import com.arttraining.business.mapper.CustomerMapper;
import com.arttraining.business.service.CustomerService;
import com.arttraining.business.service.OperationLogService;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl extends ServiceImpl<CustomerMapper, Customer> implements CustomerService {

    private final CustomerMapper customerMapper;
    private final CustomerConsumeMapper customerConsumeMapper;
    private final OperationLogService operationLogService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public PageResult<Customer> page(Integer current, Integer size, String keyword, Integer customerType,
                                     Integer level, Integer status) {
        Page<Customer> page = new Page<>(current, size);
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(keyword)) {
            wrapper.and(w -> w.like(Customer::getCustomerName, keyword)
                    .or().like(Customer::getCustomerNo, keyword)
                    .or().like(Customer::getContactPhone, keyword)
                    .or().like(Customer::getContactPerson, keyword));
        }
        if (customerType != null) {
            wrapper.eq(Customer::getCustomerType, customerType);
        }
        if (level != null) {
            wrapper.eq(Customer::getLevel, level);
        }
        if (status != null) {
            wrapper.eq(Customer::getStatus, status);
        }
        Long campusId = UserContextHolder.getCampusId();
        if (campusId != null) {
            wrapper.eq(Customer::getCampusId, campusId);
        }
        wrapper.orderByDesc(Customer::getCreateTime);
        return PageResult.of(page(page, wrapper));
    }

    @Override
    public Customer getById(Long id) {
        return customerMapper.selectById(id);
    }

    @Override
    public Customer getByNo(String customerNo) {
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Customer::getCustomerNo, customerNo);
        return getOne(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(Customer customer) {
        if (!checkNoUnique(null, customer.getCustomerNo())) {
            throw new BusinessException(ResultCode.DATA_ALREADY_EXIST.getCode(), "客户编号已存在");
        }
        if (customer.getCampusId() == null) {
            customer.setCampusId(UserContextHolder.getCampusId());
            customer.setCampusCode(UserContextHolder.getCampusCode());
        }
        if (customer.getStatus() == null) {
            customer.setStatus(1);
        }
        customerMapper.insert(customer);

        operationLogService.log("customer", customer.getId(), customer.getCustomerNo(),
                "create", "新增客户", "新增客户：" + customer.getCustomerName(),
                null, toJson(customer));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Customer customer) {
        if (!checkNoUnique(customer.getId(), customer.getCustomerNo())) {
            throw new BusinessException(ResultCode.DATA_ALREADY_EXIST.getCode(), "客户编号已存在");
        }
        Customer before = getById(customer.getId());
        if (before == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST.getCode(), "客户不存在");
        }
        customerMapper.updateById(customer);

        operationLogService.log("customer", customer.getId(), customer.getCustomerNo(),
                "update", "编辑客户", "编辑客户信息",
                toJson(before), toJson(customer));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        Customer customer = getById(id);
        if (customer == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST.getCode(), "客户不存在");
        }
        customerMapper.deleteById(id);

        operationLogService.log("customer", id, customer.getCustomerNo(),
                "delete", "删除客户", "删除客户：" + customer.getCustomerName(),
                toJson(customer), null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBatch(List<Long> ids) {
        customerMapper.deleteBatchIds(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, Integer status) {
        Customer customer = new Customer();
        customer.setId(id);
        customer.setStatus(status);
        customerMapper.updateById(customer);

        Customer before = getById(id);
        operationLogService.log("customer", id, before.getCustomerNo(),
                "status_change", "状态变更", "客户状态变更为：" + (status == 1 ? "正常" : "禁用"),
                null, null);
    }

    @Override
    public List<Customer> listAll() {
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Customer::getStatus, 1);
        Long campusId = UserContextHolder.getCampusId();
        if (campusId != null) {
            wrapper.eq(Customer::getCampusId, campusId);
        }
        wrapper.orderByAsc(Customer::getCustomerName);
        return list(wrapper);
    }

    @Override
    public boolean checkNoUnique(Long id, String customerNo) {
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Customer::getCustomerNo, customerNo);
        if (id != null) {
            wrapper.ne(Customer::getId, id);
        }
        return count(wrapper) == 0;
    }

    @Override
    public PageResult<CustomerConsume> consumePage(Long customerId, Integer current, Integer size) {
        Page<CustomerConsume> page = new Page<>(current, size);
        LambdaQueryWrapper<CustomerConsume> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CustomerConsume::getCustomerId, customerId);
        wrapper.orderByDesc(CustomerConsume::getConsumeDate);
        return PageResult.of(customerConsumeMapper.selectPage(page, wrapper));
    }

    @Override
    public List<CustomerConsume> listConsumeByCustomer(Long customerId) {
        LambdaQueryWrapper<CustomerConsume> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CustomerConsume::getCustomerId, customerId);
        wrapper.orderByDesc(CustomerConsume::getConsumeDate);
        return customerConsumeMapper.selectList(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addConsume(CustomerConsume consume) {
        if (consume.getConsumeDate() == null) {
            consume.setConsumeDate(LocalDate.now());
        }
        if (consume.getCreateTime() == null) {
            consume.setCreateTime(LocalDateTime.now());
        }
        consume.setCreateBy(UserContextHolder.getUsername());
        consume.setCampusId(UserContextHolder.getCampusId());
        consume.setCampusCode(UserContextHolder.getCampusCode());

        Customer customer = getById(consume.getCustomerId());
        if (customer != null) {
            consume.setCustomerName(customer.getCustomerName());
        }

        customerConsumeMapper.insert(consume);
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
