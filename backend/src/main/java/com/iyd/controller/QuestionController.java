package com.iyd.controller;

import com.iyd.common.R;
import com.iyd.config.JwtUtil;
import com.iyd.dto.QuestionDTO;
import com.iyd.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/question")
@RequiredArgsConstructor
public class QuestionController {
    private final QuestionService questionService;
    private final JwtUtil jwtUtil;

    @PostMapping("/search")
    public R<?> searchQuestion(@RequestBody QuestionDTO dto) {
        return questionService.searchQuestion(dto);
    }

    @GetMapping("/errors")
    public R<?> getErrorList(@RequestHeader("Authorization") String token,
                              @RequestParam(defaultValue = "1") Integer page,
                              @RequestParam(defaultValue = "10") Integer size) {
        Long userId = jwtUtil.getUserId(token.replace("Bearer ", ""));
        return questionService.getErrorList(userId, page, size);
    }

    @PutMapping("/error/{errorId}/mastered")
    public R<?> markMastered(@PathVariable Long errorId) {
        return questionService.markMastered(errorId);
    }

    @GetMapping("/pk/questions")
    public R<?> getPkQuestions(@RequestParam(defaultValue = "初中") String stage) {
        return questionService.getPkQuestions(stage);
    }

    @PostMapping("/pk/submit")
    public R<?> submitPkBattle(@RequestHeader("Authorization") String token,
                                @RequestParam Long battleId,
                                @RequestParam String answers) {
        Long userId = jwtUtil.getUserId(token.replace("Bearer ", ""));
        return questionService.submitPkBattle(battleId, userId, answers);
    }
}
