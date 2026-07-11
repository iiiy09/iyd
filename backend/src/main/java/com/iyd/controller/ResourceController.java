package com.iyd.controller;

import com.iyd.common.R;
import com.iyd.config.JwtUtil;
import com.iyd.dto.ResourceQueryDTO;
import com.iyd.service.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/resource")
@RequiredArgsConstructor
public class ResourceController {
    private final ResourceService resourceService;
    private final JwtUtil jwtUtil;

    @PostMapping("/search")
    public R<?> searchResources(@RequestBody ResourceQueryDTO dto) {
        return resourceService.searchResources(dto);
    }

    @GetMapping("/{resourceId}")
    public R<?> getResourceDetail(@PathVariable Long resourceId) {
        return resourceService.getResourceDetail(resourceId);
    }

    @GetMapping("/{resourceId}/download")
    public R<?> downloadResource(@PathVariable Long resourceId) {
        return resourceService.downloadResource(resourceId);
    }

    @PostMapping("/{resourceId}/collect")
    public R<?> collectResource(@RequestHeader("Authorization") String token,
                                 @PathVariable Long resourceId) {
        Long userId = jwtUtil.getUserId(token.replace("Bearer ", ""));
        return resourceService.collectResource(userId, resourceId);
    }

    @GetMapping("/collections")
    public R<?> getMyCollections(@RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.getUserId(token.replace("Bearer ", ""));
        return resourceService.getMyCollections(userId);
    }
}
