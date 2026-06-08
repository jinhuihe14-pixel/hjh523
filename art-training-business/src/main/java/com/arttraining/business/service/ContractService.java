package com.arttraining.business.service;

import com.arttraining.business.entity.Contract;
import com.arttraining.common.page.PageResult;

import java.util.List;

public interface ContractService {

    PageResult<Contract> page(Integer current, Integer size, String keyword, Integer contractType,
                              Integer status, Long customerId, String startDate, String endDate);

    Contract getById(Long id);

    Contract getByNo(String contractNo);

    void save(Contract contract);

    void update(Contract contract);

    void delete(Long id);

    void deleteBatch(List<Long> ids);

    void updateStatus(Long id, Integer status);

    List<Contract> listByCustomer(Long customerId);

    List<Contract> listExpiringSoon(Integer days);

    void checkContractExpire();

    boolean checkNoUnique(Long id, String contractNo);
}
