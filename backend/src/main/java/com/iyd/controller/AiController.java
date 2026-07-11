package com.iyd.controller;

import com.iyd.common.R;
import com.iyd.config.JwtUtil;
import com.iyd.dto.AiGenerateDTO;
import com.iyd.service.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {
    private final AiService aiService;
    private final JwtUtil jwtUtil;

    @PostMapping("/generate")
    public R<?> generateContent(@RequestHeader("Authorization") String token,
                                 @RequestBody AiGenerateDTO dto) {
        Long userId = jwtUtil.getUserId(token.replace("Bearer ", ""));
        return aiService.generateContent(userId, dto);
    }

    @PostMapping("/ppt")
    public R<?> generatePptOutline(@RequestHeader("Authorization") String token,
                                    @RequestParam String topic,
                                    @RequestParam(required = false) String points) {
        Long userId = jwtUtil.getUserId(token.replace("Bearer ", ""));
        return aiService.generatePptOutline(userId, topic, points);
    }

    @PostMapping("/chat")
    public R<?> chatAssist(@RequestHeader("Authorization") String token,
                            @RequestParam String question) {
        Long userId = jwtUtil.getUserId(token.replace("Bearer ", ""));
        return aiService.chatAssist(userId, question);
    }

    @GetMapping("/analyze/study")
    public R<?> analyzeStudyData(@RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.getUserId(token.replace("Bearer ", ""));
        return aiService.analyzeStudyData(userId);
    }

    @GetMapping("/history")
    public R<?> getGenerateHistory(@RequestHeader("Authorization") String token,
                                    @RequestParam(required = false) Integer type,
                                    @RequestParam(defaultValue = "1") Integer page,
                                    @RequestParam(defaultValue = "10") Integer size) {
        Long userId = jwtUtil.getUserId(token.replace("Bearer ", ""));
        return aiService.getGenerateHistory(userId, type, page, size);
    }
}
