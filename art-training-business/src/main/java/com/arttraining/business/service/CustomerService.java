package com.arttraining.business.service;

import com.arttraining.business.entity.Customer;
import com.arttraining.business.entity.CustomerConsume;
import com.arttraining.common.page.PageResult;

import java.util.List;

public interface CustomerService {

    PageResult<Customer> page(Integer current, Integer size, String keyword, Integer customerType,
                              Integer level, Integer status);

    Customer getById(Long id);

    Customer getByNo(String customerNo);

    void save(Customer customer);

    void update(Customer customer);

    void delete(Long id);

    void deleteBatch(List<Long> ids);

    void updateStatus(Long id, Integer status);

    List<Customer> listAll();

    boolean checkNoUnique(Long id, String customerNo);

    PageResult<CustomerConsume> consumePage(Long customerId, Integer current, Integer size);

    List<CustomerConsume> listConsumeByCustomer(Long customerId);

    void addConsume(CustomerConsume consume);
}
