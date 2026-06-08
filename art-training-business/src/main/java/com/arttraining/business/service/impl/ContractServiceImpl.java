package com.arttraining.business.service.impl;

import cn.hutool.core.util.StrUtil;
import com.arttraining.business.entity.Contract;
import com.arttraining.business.mapper.ContractMapper;
import com.arttraining.business.service.ContractService;
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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContractServiceImpl extends ServiceImpl<ContractMapper, Contract> implements ContractService {

    private final ContractMapper contractMapper;
    private final CustomerService customerService;
    private final OperationLogService operationLogService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public PageResult<Contract> page(Integer current, Integer size, String keyword, Integer contractType,
                                    Integer status, Long customerId, String startDate, String endDate) {
        Page<Contract> page = new Page<>(current, size);
        LambdaQueryWrapper<Contract> wrapper = new LambdaQueryWrapper<>();

        if (StrUtil.isNotBlank(keyword)) {
            wrapper.and(w -> w.like(Contract::getContractNo, keyword)
                    .or().like(Contract::getContractName, keyword)
                    .or().like(Contract::getCustomerName, keyword));
        }
        if (contractType != null) {
            wrapper.eq(Contract::getContractType, contractType);
        }
        if (status != null) {
            wrapper.eq(Contract::getStatus, status);
        }
        if (customerId != null) {
            wrapper.eq(Contract::getCustomerId, customerId);
        }
        if (StrUtil.isNotBlank(startDate)) {
            wrapper.ge(Contract::getSignDate, LocalDate.parse(startDate));
        }
        if (StrUtil.isNotBlank(endDate)) {
            wrapper.le(Contract::getEndDate, LocalDate.parse(endDate));
        }

        Long campusId = UserContextHolder.getCampusId();
        if (campusId != null) {
            wrapper.eq(Contract::getCampusId, campusId);
        }

        wrapper.orderByDesc(Contract::getCreateTime);

        return PageResult.of(page(page, wrapper));
    }

    @Override
    public Contract getById(Long id) {
        return contractMapper.selectById(id);
    }

    @Override
    public Contract getByNo(String contractNo) {
        LambdaQueryWrapper<Contract> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Contract::getContractNo, contractNo);
        return getOne(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(Contract contract) {
        if (!checkNoUnique(null, contract.getContractNo())) {
            throw new BusinessException(ResultCode.DATA_ALREADY_EXIST.getCode(), "合同编号已存在");
        }

        if (contract.getCampusId() == null) {
            contract.setCampusId(UserContextHolder.getCampusId());
            contract.setCampusCode(UserContextHolder.getCampusCode());
        }

        if (contract.getStatus() == null) {
            contract.setStatus(1);
        }
        if (contract.getRemindDays() == null) {
            contract.setRemindDays(30);
        }
        if (contract.getReminded() == null) {
            contract.setReminded(0);
        }

        if (contract.getCustomerId() != null) {
            var customer = customerService.getById(contract.getCustomerId());
            if (customer != null) {
                contract.setCustomerName(customer.getCustomerName());
            }
        }

        updateContractStatusByDate(contract);

        contractMapper.insert(contract);

        operationLogService.log("contract", contract.getId(), contract.getContractNo(),
                "create", "新增合同", "新增合同：" + contract.getContractName(),
                null, toJson(contract));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Contract contract) {
        if (!checkNoUnique(contract.getId(), contract.getContractNo())) {
            throw new BusinessException(ResultCode.DATA_ALREADY_EXIST.getCode(), "合同编号已存在");
        }

        Contract before = getById(contract.getId());
        if (before == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST.getCode(), "合同不存在");
        }

        if (contract.getCustomerId() != null && !contract.getCustomerId().equals(before.getCustomerId())) {
            var customer = customerService.getById(contract.getCustomerId());
            if (customer != null) {
                contract.setCustomerName(customer.getCustomerName());
            }
        }

        updateContractStatusByDate(contract);

        contractMapper.updateById(contract);

        operationLogService.log("contract", contract.getId(), contract.getContractNo(),
                "update", "编辑合同", "编辑合同信息",
                toJson(before), toJson(contract));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        Contract contract = getById(id);
        if (contract == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST.getCode(), "合同不存在");
        }
        contractMapper.deleteById(id);

        operationLogService.log("contract", id, contract.getContractNo(),
                "delete", "删除合同", "删除合同：" + contract.getContractName(),
                toJson(contract), null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBatch(List<Long> ids) {
        contractMapper.deleteBatchIds(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, Integer status) {
        Contract contract = new Contract();
        contract.setId(id);
        contract.setStatus(status);
        contractMapper.updateById(contract);

        Contract before = getById(id);
        operationLogService.log("contract", id, before.getContractNo(),
                "status_change", "状态变更", "合同状态变更为：" + getStatusName(status),
                null, null);
    }

    @Override
    public List<Contract> listByCustomer(Long customerId) {
        LambdaQueryWrapper<Contract> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Contract::getCustomerId, customerId);
        wrapper.orderByDesc(Contract::getCreateTime);
        return list(wrapper);
    }

    @Override
    public List<Contract> listExpiringSoon(Integer days) {
        LocalDate today = LocalDate.now();
        LocalDate expireDate = today.plusDays(days);

        LambdaQueryWrapper<Contract> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Contract::getStatus, 1);
        wrapper.le(Contract::getEndDate, expireDate);
        wrapper.ge(Contract::getEndDate, today);
        wrapper.orderByAsc(Contract::getEndDate);

        Long campusId = UserContextHolder.getCampusId();
        if (campusId != null) {
            wrapper.eq(Contract::getCampusId, campusId);
        }

        return list(wrapper);
    }

    @Override
    @Scheduled(cron = "0 0 9 * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void checkContractExpire() {
        LocalDate today = LocalDate.now();

        LambdaQueryWrapper<Contract> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Contract::getStatus, 1, 2);
        List<Contract> contracts = list(wrapper);

        for (Contract contract : contracts) {
            if (contract.getEndDate() == null) continue;

            long daysUntilExpire = ChronoUnit.DAYS.between(today, contract.getEndDate());
            Integer remindDays = contract.getRemindDays() != null ? contract.getRemindDays() : 30;

            Integer oldStatus = contract.getStatus();
            Integer newStatus = null;

            if (daysUntilExpire < 0) {
                newStatus = 3;
            } else if (daysUntilExpire <= remindDays && oldStatus == 1) {
                newStatus = 2;
            }

            if (newStatus != null && !newStatus.equals(oldStatus)) {
                Contract update = new Contract();
                update.setId(contract.getId());
                update.setStatus(newStatus);
                contractMapper.updateById(update);

                operationLogService.log("contract", contract.getId(), contract.getContractNo(),
                        "status_change", "状态自动变更",
                        "合同状态自动变更为：" + getStatusName(newStatus) + "，到期日：" + contract.getEndDate(),
                        null, null);
            }
        }
    }

    @Override
    public boolean checkNoUnique(Long id, String contractNo) {
        LambdaQueryWrapper<Contract> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Contract::getContractNo, contractNo);
        if (id != null) {
            wrapper.ne(Contract::getId, id);
        }
        return count(wrapper) == 0;
    }

    private void updateContractStatusByDate(Contract contract) {
        if (contract.getEndDate() == null) return;

        LocalDate today = LocalDate.now();
        long daysUntilExpire = ChronoUnit.DAYS.between(today, contract.getEndDate());
        Integer remindDays = contract.getRemindDays() != null ? contract.getRemindDays() : 30;

        if (daysUntilExpire < 0) {
            contract.setStatus(3);
        } else if (daysUntilExpire <= remindDays) {
            contract.setStatus(2);
        }
    }

    private String getStatusName(Integer status) {
        if (status == null) return "";
        return switch (status) {
            case 0 -> "草稿";
            case 1 -> "生效中";
            case 2 -> "即将到期";
            case 3 -> "已到期";
            case 4 -> "已作废";
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
