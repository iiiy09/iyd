package com.iyd.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.iyd.common.BusinessException;
import com.iyd.common.R;
import com.iyd.config.JwtUtil;
import com.iyd.dto.LoginDTO;
import com.iyd.entity.SysUser;
import com.iyd.mapper.SysUserMapper;
import com.iyd.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SysUserServiceImpl implements SysUserService {
    private final SysUserMapper userMapper;
    private final JwtUtil jwtUtil;

    @Override
    public R<?> login(LoginDTO dto) {
        SysUser user = userMapper.selectOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getPhone, dto.getPhone())
        );
        if (user == null) {
            throw new BusinessException("用户不存在，请先注册");
        }
        if (user.getStatus() == 0) {
            throw new BusinessException("账号已被禁用");
        }
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            if (!SecureUtil.md5(dto.getPassword()).equals(user.getPassword())) {
                throw new BusinessException("密码错误");
            }
        }
        String token = jwtUtil.generateToken(user.getId(), user.getPhone(), user.getRole());
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userId", user.getId());
        result.put("nickname", user.getNickname());
        result.put("stage", user.getStage());
        result.put("role", user.getRole());
        return R.ok(result);
    }

    @Override
    public R<?> register(LoginDTO dto) {
        SysUser exist = userMapper.selectOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getPhone, dto.getPhone())
        );
        if (exist != null) {
            throw new BusinessException("该手机号已注册");
        }
        SysUser user = new SysUser();
        user.setPhone(dto.getPhone());
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            user.setPassword(SecureUtil.md5(dto.getPassword()));
        }
        user.setNickname("学习用户" + RandomUtil.randomNumbers(4));
        user.setStage(dto.getStage() != null ? dto.getStage() : "初中");
        user.setRole("student");
        user.setStatus(1);
        userMapper.insert(user);
        String token = jwtUtil.generateToken(user.getId(), user.getPhone(), user.getRole());
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userId", user.getId());
        result.put("nickname", user.getNickname());
        result.put("stage", user.getStage());
        return R.ok(result);
    }

    @Override
    public R<?> getUserInfo(Long userId) {
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        Map<String, Object> info = new HashMap<>();
        info.put("userId", user.getId());
        info.put("phone", user.getPhone());
        info.put("nickname", user.getNickname());
        info.put("avatar", user.getAvatar());
        info.put("stage", user.getStage());
        info.put("role", user.getRole());
        return R.ok(info);
    }

    @Override
    public R<?> updateProfile(Long userId, String nickname, String avatar) {
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (nickname != null) user.setNickname(nickname);
        if (avatar != null) user.setAvatar(avatar);
        userMapper.updateById(user);
        return R.ok();
    }
}
