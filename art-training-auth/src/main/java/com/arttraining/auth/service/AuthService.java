package com.arttraining.auth.service;

import com.arttraining.auth.dto.LoginDTO;
import com.arttraining.auth.vo.LoginVO;

public interface AuthService {

    LoginVO login(LoginDTO dto);

    LoginVO refreshToken(String refreshToken);

    void logout(String token);

    boolean validateToken(String token);

    Long getUserIdFromToken(String token);
}
