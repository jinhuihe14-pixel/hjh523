package com.arttraining.user.service;

import com.arttraining.common.page.PageResult;
import com.arttraining.user.dto.UserQueryDTO;
import com.arttraining.user.dto.UserSaveDTO;
import com.arttraining.user.vo.UserVO;

import java.util.List;

public interface UserService {

    PageResult<UserVO> page(Integer current, Integer size, UserQueryDTO query);

    UserVO getById(Long id);

    UserVO getByUsername(String username);

    void save(UserSaveDTO dto);

    void update(UserSaveDTO dto);

    void delete(Long id);

    void deleteBatch(List<Long> ids);

    void updateStatus(Long id, Integer status);

    void resetPassword(Long id, String password);

    List<UserVO> listByUserType(Integer userType);

    List<UserVO> listByCampusId(Long campusId);

    boolean checkUsernameUnique(Long id, String username);

    boolean checkPhoneUnique(Long id, String phone);
}
