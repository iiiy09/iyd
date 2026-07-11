package com.iyd.service;

import com.iyd.common.R;
import com.iyd.dto.LoginDTO;

public interface SysUserService {
    R<?> login(LoginDTO dto);
    R<?> register(LoginDTO dto);
    R<?> getUserInfo(Long userId);
    R<?> updateProfile(Long userId, String nickname, String avatar);
}
