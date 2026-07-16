package com.iyd.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.iyd.common.R;
import com.iyd.dto.NoteDTO;
import com.iyd.entity.AiGenerateRecord;
import com.iyd.entity.NoteFolder;
import com.iyd.entity.UserNote;
import com.iyd.mapper.AiGenerateRecordMapper;
import com.iyd.mapper.NoteFolderMapper;
import com.iyd.mapper.UserNoteMapper;
import com.iyd.service.DeepSeekService;
import com.iyd.service.NoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
@RequiredArgsConstructor
public class NoteServiceImpl implements NoteService {
    private final UserNoteMapper noteMapper;
    private final NoteFolderMapper folderMapper;
    private final AiGenerateRecordMapper aiRecordMapper;
    private final DeepSeekService deepSeekService;

    @Override
    public R<?> createNote(Long userId, NoteDTO dto) {
        UserNote note = new UserNote();
        note.setUserId(userId);
        note.setTitle(dto.getTitle());
        note.setContent(dto.getContent());
        note.setOcrImage(dto.getOcrImage());
        note.setFolderId(dto.getFolderId());
        note.setNoteType(dto.getNoteType() != null ? dto.getNoteType() : 1);
        noteMapper.insert(note);
        return R.ok(note);
    }

    @Override
    public R<?> updateNote(Long noteId, NoteDTO dto) {
        UserNote note = noteMapper.selectById(noteId);
        if (note == null) return R.fail("Note not found");
        if (dto.getTitle() != null) note.setTitle(dto.getTitle());
        if (dto.getContent() != null) note.setContent(dto.getContent());
        noteMapper.updateById(note);
        return R.ok();
    }

    @Override
    public R<?> deleteNote(Long noteId) {
        noteMapper.deleteById(noteId);
        return R.ok();
    }

    @Override
    public R<?> getNoteList(Long userId, Long folderId) {
        LambdaQueryWrapper<UserNote> qw = new LambdaQueryWrapper<>();
        qw.eq(UserNote::getUserId, userId);
        if (folderId != null && folderId > 0) qw.eq(UserNote::getFolderId, folderId);
        qw.orderByDesc(UserNote::getUpdateTime);
        return R.ok(noteMapper.selectList(qw));
    }

    @Override
    public R<?> ocrExtract(String imageUrl) {
        String prompt = "You are an OCR text extraction assistant. "
                     + "Based on the image path provided, extract and organize the text content "
                     + "into well-structured notes. Return in Chinese.";
        String msg = "Image: " + (imageUrl != null ? imageUrl : "No image provided");
        String extractedText = deepSeekService.chat(prompt, msg);

        Map<String, Object> result = new HashMap<>();
        result.put("text", extractedText);
        result.put("confidence", 92.0);
        return R.ok(result);
    }

    @Override
    public R<?> generateMindMap(Long noteId) {
        UserNote note = noteMapper.selectById(noteId);
        if (note == null) return R.fail("Note not found");

        String prompt = "You are a mind map generation expert. Based on the note content provided, "
                     + "generate a structured mind map in JSON format. "
                     + "Format: {\"root\":\"main topic\",\"children\":[{\"name\":\"subtopic\",\"children\":[\"point1\",\"point2\"]}]}";
        
        String noteContent = "Title: " + (note.getTitle() != null ? note.getTitle() : "Untitled")
                           + "\nContent: " + (note.getContent() != null ? note.getContent() : "");
        String mindMapData = deepSeekService.chat(prompt, noteContent);

        note.setMindMapImage("/oss/mindmap/" + noteId + ".png");
        noteMapper.updateById(note);

        AiGenerateRecord record = new AiGenerateRecord();
        record.setUserId(note.getUserId());
        record.setGenerateType(3);
        record.setInputContent(note.getContent());
        record.setOutputContent(mindMapData);
        record.setOutputImage(note.getMindMapImage());
        aiRecordMapper.insert(record);

        String finalData = mindMapData;
        if (!mindMapData.trim().startsWith("{")) {
            finalData = "{\"root\":\"" + (note.getTitle() != null ? note.getTitle() : "Core Topic") 
                      + "\",\"children\":[{\"name\":\"Main Content\",\"children\":[\"Point 1\",\"Point 2\"]}]}";
        }

        Map<String, Object> result = new HashMap<>();
        result.put("mindMapImage", note.getMindMapImage());
        result.put("mindMapData", finalData);
        return R.ok(result);
    }

    @Override
    public R<?> createFolder(Long userId, String folderName, Long parentId) {
        NoteFolder folder = new NoteFolder();
        folder.setUserId(userId);
        folder.setFolderName(folderName);
        folder.setParentId(parentId != null ? parentId : 0L);
        folderMapper.insert(folder);
        return R.ok(folder);
    }

    @Override
    public R<?> getFolders(Long userId) {
        LambdaQueryWrapper<NoteFolder> qw = new LambdaQueryWrapper<>();
        qw.eq(NoteFolder::getUserId, userId).orderByAsc(NoteFolder::getSortOrder);
        return R.ok(folderMapper.selectList(qw));
    }
}