package com.iyd.controller;

import com.iyd.common.R;
import com.iyd.config.JwtUtil;
import com.iyd.dto.ReciteDTO;
import com.iyd.service.ReciteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recite")
@RequiredArgsConstructor
public class ReciteController {
    private final ReciteService reciteService;
    private final JwtUtil jwtUtil;

    @PostMapping("/submit")
    public R<?> submitRecite(@RequestHeader("Authorization") String token,
                              @RequestBody ReciteDTO dto) {
        Long userId = jwtUtil.getUserId(token.replace("Bearer ", ""));
        return reciteService.submitRecite(userId, dto);
    }

    @GetMapping("/history")
    public R<?> getReciteHistory(@RequestHeader("Authorization") String token,
                                  @RequestParam(defaultValue = "1") Integer page,
                                  @RequestParam(defaultValue = "10") Integer size) {
        Long userId = jwtUtil.getUserId(token.replace("Bearer ", ""));
        return reciteService.getReciteHistory(userId, page, size);
    }

    @GetMapping("/words")
    public R<?> getWordList(@RequestHeader("Authorization") String token,
                             @RequestParam(required = false) String stage,
                             @RequestParam(required = false) String category) {
        Long userId = jwtUtil.getUserId(token.replace("Bearer ", ""));
        return reciteService.getWordList(userId, stage, category);
    }

    @PostMapping("/word/{wordId}/review")
    public R<?> reviewWord(@RequestHeader("Authorization") String token,
                            @PathVariable Long wordId,
                            @RequestParam Boolean known) {
        Long userId = jwtUtil.getUserId(token.replace("Bearer ", ""));
        return reciteService.reviewWord(userId, wordId, known);
    }

    @PostMapping("/speech/evaluate")
    public R<?> speechEvaluate(@RequestHeader("Authorization") String token,
                                @RequestParam String word,
                                @RequestParam String audioUrl) {
        Long userId = jwtUtil.getUserId(token.replace("Bearer ", ""));
        return reciteService.speechEvaluate(userId, word, audioUrl);
    }
}
