package com.iyd.controller;

import com.iyd.common.R;
import com.iyd.config.JwtUtil;
import com.iyd.dto.LoginDTO;
import com.iyd.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final SysUserService userService;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public R<?> login(@Valid @RequestBody LoginDTO dto) {
        return userService.login(dto);
    }

    @PostMapping("/register")
    public R<?> register(@Valid @RequestBody LoginDTO dto) {
        return userService.register(dto);
    }

    @GetMapping("/info")
    public R<?> getUserInfo(@RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.getUserId(token.replace("Bearer ", ""));
        return userService.getUserInfo(userId);
    }

    @PutMapping("/profile")
    public R<?> updateProfile(@RequestHeader("Authorization") String token,
                               @RequestParam(required = false) String nickname,
                               @RequestParam(required = false) String avatar) {
        Long userId = jwtUtil.getUserId(token.replace("Bearer ", ""));
        return userService.updateProfile(userId, nickname, avatar);
    }
}
