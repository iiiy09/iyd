package com.iyd.controller;

import com.iyd.common.R;
import com.iyd.config.JwtUtil;
import com.iyd.dto.TaskDTO;
import com.iyd.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/task")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;
    private final JwtUtil jwtUtil;

    @PostMapping
    public R<?> createTask(@RequestHeader("Authorization") String token,
                            @RequestBody TaskDTO dto) {
        Long userId = jwtUtil.getUserId(token.replace("Bearer ", ""));
        return taskService.createTask(userId, dto);
    }

    @PutMapping("/{taskId}")
    public R<?> updateTask(@PathVariable Long taskId, @RequestBody TaskDTO dto) {
        return taskService.updateTask(taskId, dto);
    }

    @DeleteMapping("/{taskId}")
    public R<?> deleteTask(@PathVariable Long taskId) {
        return taskService.deleteTask(taskId);
    }

    @PostMapping("/{taskId}/checkin")
    public R<?> checkin(@PathVariable Long taskId) {
        return taskService.checkin(taskId);
    }

    @GetMapping("/list")
    public R<?> getTaskList(@RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.getUserId(token.replace("Bearer ", ""));
        return taskService.getTaskList(userId);
    }

    @GetMapping("/summary")
    public R<?> getMonthlySummary(@RequestHeader("Authorization") String token,
                                   @RequestParam String month) {
        Long userId = jwtUtil.getUserId(token.replace("Bearer ", ""));
        return taskService.getMonthlySummary(userId, month);
    }
}
