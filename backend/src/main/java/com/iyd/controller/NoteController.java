package com.iyd.controller;

import com.iyd.common.R;
import com.iyd.config.JwtUtil;
import com.iyd.dto.NoteDTO;
import com.iyd.service.NoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/note")
@RequiredArgsConstructor
public class NoteController {
    private final NoteService noteService;
    private final JwtUtil jwtUtil;

    @PostMapping
    public R<?> createNote(@RequestHeader("Authorization") String token,
                            @RequestBody NoteDTO dto) {
        Long userId = jwtUtil.getUserId(token.replace("Bearer ", ""));
        return noteService.createNote(userId, dto);
    }

    @PutMapping("/{noteId}")
    public R<?> updateNote(@PathVariable Long noteId, @RequestBody NoteDTO dto) {
        return noteService.updateNote(noteId, dto);
    }

    @DeleteMapping("/{noteId}")
    public R<?> deleteNote(@PathVariable Long noteId) {
        return noteService.deleteNote(noteId);
    }

    @GetMapping("/list")
    public R<?> getNoteList(@RequestHeader("Authorization") String token,
                             @RequestParam(required = false) Long folderId) {
        Long userId = jwtUtil.getUserId(token.replace("Bearer ", ""));
        return noteService.getNoteList(userId, folderId);
    }

    @PostMapping("/ocr")
    public R<?> ocrExtract(@RequestParam String imageUrl) {
        return noteService.ocrExtract(imageUrl);
    }

    @PostMapping("/{noteId}/mindmap")
    public R<?> generateMindMap(@PathVariable Long noteId) {
        return noteService.generateMindMap(noteId);
    }

    @PostMapping("/folder")
    public R<?> createFolder(@RequestHeader("Authorization") String token,
                              @RequestParam String folderName,
                              @RequestParam(required = false) Long parentId) {
        Long userId = jwtUtil.getUserId(token.replace("Bearer ", ""));
        return noteService.createFolder(userId, folderName, parentId);
    }

    @GetMapping("/folders")
    public R<?> getFolders(@RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.getUserId(token.replace("Bearer ", ""));
        return noteService.getFolders(userId);
    }
}
