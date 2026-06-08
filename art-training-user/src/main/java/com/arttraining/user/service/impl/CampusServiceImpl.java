package com.arttraining.user.service.impl;

import com.arttraining.common.exception.BusinessException;
import com.arttraining.common.page.PageResult;
import com.arttraining.common.result.ResultCode;
import com.arttraining.user.entity.Campus;
import com.arttraining.user.mapper.CampusMapper;
import com.arttraining.user.service.CampusService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CampusServiceImpl extends ServiceImpl<CampusMapper, Campus> implements CampusService {

    private final CampusMapper campusMapper;

    @Override
    public PageResult<Campus> page(Integer current, Integer size, String keyword, Integer status) {
        Page<Campus> page = new Page<>(current, size);
        LambdaQueryWrapper<Campus> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(keyword)) {
            wrapper.and(w -> w.like(Campus::getCampusName, keyword)
                    .or().like(Campus::getCampusCode, keyword));
        }
        if (status != null) {
            wrapper.eq(Campus::getStatus, status);
        }
        wrapper.orderByAsc(Campus::getSortOrder).orderByDesc(Campus::getCreateTime);
        return PageResult.of(page(page, wrapper));
    }

    @Override
    public Campus getById(Long id) {
        return campusMapper.selectById(id);
    }

    @Override
    public Campus getByCode(String campusCode) {
        LambdaQueryWrapper<Campus> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Campus::getCampusCode, campusCode);
        return getOne(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(Campus campus) {
        if (!checkCodeUnique(null, campus.getCampusCode())) {
            throw new BusinessException(ResultCode.CAMPUS_CODE_ALREADY_EXIST);
        }
        campusMapper.insert(campus);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Campus campus) {
        if (!checkCodeUnique(campus.getId(), campus.getCampusCode())) {
            throw new BusinessException(ResultCode.CAMPUS_CODE_ALREADY_EXIST);
        }
        campusMapper.updateById(campus);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        Campus campus = getById(id);
        if (campus == null) {
            throw new BusinessException(ResultCode.CAMPUS_NOT_EXIST);
        }
        campusMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBatch(List<Long> ids) {
        campusMapper.deleteBatchIds(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, Integer status) {
        Campus campus = new Campus();
        campus.setId(id);
        campus.setStatus(status);
        campusMapper.updateById(campus);
    }

    @Override
    public List<Campus> listAll() {
        LambdaQueryWrapper<Campus> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Campus::getStatus, 1);
        wrapper.orderByAsc(Campus::getSortOrder);
        return list(wrapper);
    }

    @Override
    public boolean checkCodeUnique(Long id, String campusCode) {
        LambdaQueryWrapper<Campus> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Campus::getCampusCode, campusCode);
        if (id != null) {
            wrapper.ne(Campus::getId, id);
        }
        return count(wrapper) == 0;
    }
}
