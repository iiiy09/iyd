package com.iyd.controller;

import com.iyd.common.R;
import com.iyd.config.JwtUtil;
import com.iyd.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/task")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;
    private final JwtUtil jwtUtil;

    @GetMapping("/list")
    public R<?> listTasks(@RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.getUserId(token.replace("Bearer ", ""));
        return taskService.listTasks(userId);
    }

    @PostMapping("")
    public R<?> addTask(@RequestHeader("Authorization") String token,
                         @RequestBody Map<String, Object> body) {
        Long userId = jwtUtil.getUserId(token.replace("Bearer ", ""));
        String taskContent = (String) body.get("taskContent");
        Integer estimatedMinutes = body.get("estimatedMinutes") != null
                ? Integer.parseInt(body.get("estimatedMinutes").toString()) : 30;
        return taskService.addTask(userId, taskContent, estimatedMinutes);
    }

    @PostMapping("/{taskId}/checkin")
    public R<?> checkin(@RequestHeader("Authorization") String token,
                         @PathVariable Long taskId) {
        Long userId = jwtUtil.getUserId(token.replace("Bearer ", ""));
        return taskService.checkin(userId, taskId);
    }

    @DeleteMapping("/{taskId}")
    public R<?> deleteTask(@RequestHeader("Authorization") String token,
                            @PathVariable Long taskId) {
        Long userId = jwtUtil.getUserId(token.replace("Bearer ", ""));
        return taskService.deleteTask(userId, taskId);
    }
}