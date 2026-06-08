package com.arttraining.user.service;

import com.arttraining.common.page.PageResult;
import com.arttraining.user.entity.Campus;

import java.util.List;

public interface CampusService {

    PageResult<Campus> page(Integer current, Integer size, String keyword, Integer status);

    Campus getById(Long id);

    Campus getByCode(String campusCode);

    void save(Campus campus);

    void update(Campus campus);

    void delete(Long id);

    void deleteBatch(List<Long> ids);

    void updateStatus(Long id, Integer status);

    List<Campus> listAll();

    boolean checkCodeUnique(Long id, String campusCode);
}
